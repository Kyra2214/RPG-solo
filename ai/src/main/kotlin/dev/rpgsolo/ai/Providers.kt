package dev.rpgsolo.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

private fun parseRoot(body: String): JsonObject =
    try {
        Json.parseToJsonElement(body).jsonObject
    } catch (e: Exception) {
        throw AiError.BadResponse("não é JSON")
    }

private fun JsonObject.intAt(key: String): Int? = (this[key] as? JsonPrimitive)?.intOrNull

/** Provedores no estilo OpenAI (`/chat/completions`): vários serviços e modelos locais falam esse formato. */
class OpenAiCompatibleProvider(
    private val config: ProviderConfig,
    private val transport: HttpTransport
) : AiProvider {
    override val name = "openai-compatible"

    override fun complete(request: AiRequest): AiResponse {
        val body = buildJsonObject {
            put("model", config.model)
            put("messages", buildJsonArray {
                request.messages.forEach { m ->
                    add(buildJsonObject {
                        put("role", m.role.wire)
                        put("content", m.content)
                    })
                }
            })
            put("max_tokens", request.maxTokens)
            request.temperature?.let { put("temperature", it) }
            if (request.jsonMode) put("response_format", buildJsonObject { put("type", "json_object") })
        }.toString()
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer ${config.apiKey}"
        ) + config.extraHeaders
        val reply = transport.post(config.endpoint, headers, body)
        checkHttp(reply)
        return parseOpenAiResponse(reply)
    }

    override fun stream(request: AiRequest, onChunk: (String) -> Unit): AiResponse {
        val body = buildJsonObject {
            put("model", config.model)
            put("messages", buildJsonArray {
                request.messages.forEach { m -> add(buildJsonObject {
                    put("role", m.role.wire); put("content", m.content)
                }) }
            })
            put("max_tokens", request.maxTokens)
            request.temperature?.let { put("temperature", it) }
            if (request.jsonMode) put("response_format", buildJsonObject { put("type", "json_object") })
            put("stream", true)
        }.toString()
        val headers = mapOf("Content-Type" to "application/json", "Accept" to "text/event-stream", "Authorization" to "Bearer ${config.apiKey}") + config.extraHeaders
        val streamed = StringBuilder()
        val reply = transport.postStream(config.endpoint, headers, body) { line ->
            val data = line.removePrefix("data:").trim()
            if (data.isNotEmpty() && data != "[DONE]") {
                val delta = runCatching {
                    parseRoot(data)["choices"]?.jsonArray?.firstOrNull()?.jsonObject
                        ?.get("delta")?.jsonObject?.get("content")?.jsonPrimitive?.contentOrNull
                }.getOrNull()
                if (!delta.isNullOrEmpty()) { streamed.append(delta); onChunk(delta) }
            }
        }
        if (reply.status == 400 || reply.status == 422) {
            return complete(request).also { onChunk(it.text) }
        }
        checkHttp(reply)
        if (streamed.isNotEmpty()) {
            val usage = runCatching { parseRoot(reply.body.substringAfterLast("data:").trim()) }.getOrNull()
            return AiResponse(streamed.toString(), usage?.let { root ->
                val u = root["usage"] as? JsonObject
                val i = u?.intAt("prompt_tokens"); val o = u?.intAt("completion_tokens")
                if (i != null && o != null) AiUsage(i, o) else null
            })
        }
        return parseOpenAiResponse(reply).also { onChunk(it.text) }
    }

    private fun parseOpenAiResponse(reply: HttpReply): AiResponse {
        checkHttp(reply)
        val root = parseRoot(reply.body)
        val text = try {
            root["choices"]?.jsonArray?.firstOrNull()?.jsonObject
                ?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.contentOrNull
        } catch (e: Exception) { null }
            ?: throw AiError.BadResponse("sem texto em choices[0].message.content")
        val usage = root["usage"]?.let { u ->
            val o = (u as? JsonObject) ?: return@let null
            val i = o.intAt("prompt_tokens"); val out = o.intAt("completion_tokens")
            if (i != null && out != null) AiUsage(
                i,
                out,
                o.intAt("cache_read_input_tokens") ?: 0,
                o.intAt("cache_creation_input_tokens") ?: 0
            ) else null
        }
        return AiResponse(text, usage)
    }
}

/** Segundo provedor (6.1.3): formato Messages da Anthropic. Prova que a troca de provedor não toca no motor. */
class AnthropicProvider(
    private val config: ProviderConfig,
    private val transport: HttpTransport,
    private val apiVersion: String = "2023-06-01"
) : AiProvider {
    override val name = "anthropic"

    override fun complete(request: AiRequest): AiResponse {
        val system = request.messages.filter { it.role == Role.SYSTEM }.joinToString("\n\n") { it.content }
        val body = buildJsonObject {
            put("model", config.model)
            put("max_tokens", request.maxTokens)
            if (system.isNotBlank()) {
                if (config.cacheSystemPrompt) {
                    put("system", buildJsonArray {
                        add(buildJsonObject {
                            put("type", "text")
                            put("text", system)
                            put("cache_control", buildJsonObject { put("type", "ephemeral") })
                        })
                    })
                } else put("system", system)
            }
            request.temperature?.let { put("temperature", it) }
            put("messages", buildJsonArray {
                request.messages.filter { it.role != Role.SYSTEM }.forEach { m ->
                    add(buildJsonObject {
                        put("role", m.role.wire)
                        put("content", m.content)
                    })
                }
            })
        }.toString()
        val headers = mapOf(
            "Content-Type" to "application/json",
            "x-api-key" to config.apiKey,
            "anthropic-version" to apiVersion
        ) + config.extraHeaders
        val reply = transport.post(config.endpoint, headers, body)
        checkHttp(reply)
        return parseAnthropicResponse(reply)
    }

    override fun stream(request: AiRequest, onChunk: (String) -> Unit): AiResponse {
        val system = request.messages.filter { it.role == Role.SYSTEM }.joinToString("\n\n") { it.content }
        val body = buildJsonObject {
            put("model", config.model); put("max_tokens", request.maxTokens); put("stream", true)
            if (system.isNotBlank()) {
                if (config.cacheSystemPrompt) {
                    put("system", buildJsonArray {
                        add(buildJsonObject {
                            put("type", "text")
                            put("text", system)
                            put("cache_control", buildJsonObject { put("type", "ephemeral") })
                        })
                    })
                } else put("system", system)
            }
            request.temperature?.let { put("temperature", it) }
            put("messages", buildJsonArray {
                request.messages.filter { it.role != Role.SYSTEM }.forEach { m -> add(buildJsonObject {
                    put("role", m.role.wire); put("content", m.content)
                }) }
            })
        }.toString()
        val headers = mapOf("Content-Type" to "application/json", "Accept" to "text/event-stream", "x-api-key" to config.apiKey,
            "anthropic-version" to apiVersion) + config.extraHeaders
        val streamed = StringBuilder()
        var inputTokens: Int? = null
        var outputTokens: Int? = null
        var cacheReadTokens = 0
        var cacheCreationTokens = 0
        val reply = transport.postStream(config.endpoint, headers, body) { line ->
            val data = line.removePrefix("data:").trim()
            if (data.isNotEmpty() && data != "[DONE]") {
                val root = runCatching { parseRoot(data) }.getOrNull()
                if (root != null) {
                    when (root["type"]?.jsonPrimitive?.contentOrNull) {
                        "message_start" -> {
                            val usage = (root["message"] as? JsonObject)?.get("usage") as? JsonObject
                            inputTokens = usage?.intAt("input_tokens") ?: inputTokens
                            cacheReadTokens = usage?.intAt("cache_read_input_tokens") ?: cacheReadTokens
                            cacheCreationTokens = usage?.intAt("cache_creation_input_tokens") ?: cacheCreationTokens
                            outputTokens = usage?.intAt("output_tokens") ?: outputTokens
                        }
                        "message_delta" -> {
                            val usage = root["usage"] as? JsonObject
                            outputTokens = usage?.intAt("output_tokens") ?: outputTokens
                        }
                        "content_block_delta" -> {
                            val delta = runCatching {
                                root["delta"]?.jsonObject?.get("text")?.jsonPrimitive?.contentOrNull
                            }.getOrNull()
                            if (!delta.isNullOrEmpty()) { streamed.append(delta); onChunk(delta) }
                        }
                    }
                }
            }
        }
        if (reply.status == 400 || reply.status == 422) {
            return complete(request).also { onChunk(it.text) }
        }
        checkHttp(reply)
        if (streamed.isNotEmpty()) {
            val usage = if (inputTokens != null && outputTokens != null) {
                AiUsage(inputTokens!!, outputTokens!!, cacheReadTokens, cacheCreationTokens)
            } else null
            return AiResponse(streamed.toString(), usage)
        }
        return parseAnthropicResponse(reply).also { onChunk(it.text) }
    }

    private fun parseAnthropicResponse(reply: HttpReply): AiResponse {
        checkHttp(reply)
        val root = parseRoot(reply.body)
        val text = try {
            root["content"]?.jsonArray?.map { it.jsonObject }
                ?.filter { it["type"]?.jsonPrimitive?.contentOrNull == "text" }
                ?.joinToString("") { it["text"]?.jsonPrimitive?.contentOrNull.orEmpty() }
        } catch (e: Exception) { null }
        if (text.isNullOrEmpty()) throw AiError.BadResponse("sem blocos de texto em content")
        val usage = (root["usage"] as? JsonObject)?.let { o ->
            val i = o.intAt("input_tokens"); val out = o.intAt("output_tokens")
            if (i != null && out != null) AiUsage(i, out) else null
        }
        return AiResponse(text, usage)
    }
}
