package dev.rpgsolo.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/** PT-02: descoberta só de texto, sondagem dos candidatos e aviso de cadeia esgotada. */
class ModelosAutomaticosPt02Test {
    private val chat = "https://openrouter.ai/api/v1/chat/completions"

    private fun ok(text: String = "OK") = HttpReply(200, """{"choices":[{"message":{"content":"$text"}}]}""")

    /** Transporte que responde por modelo (lido do corpo do pedido) e guarda a ordem das sondas. */
    private class PorModelo(private val respostas: Map<String, Any>) : HttpTransport {
        val sondados = mutableListOf<String>()

        override fun post(
            url: String,
            headers: Map<String, String>,
            body: String,
        ): HttpReply {
            val model = Regex(""""model":"([^"]+)"""").find(body)!!.groupValues[1]
            sondados += model
            val r = respostas[model] ?: error("modelo sem roteiro: $model")
            if (r is AiError) throw r
            return r as HttpReply
        }
    }

    @Test fun descobertaExigeEntradaESaidaSoTexto() {
        val body =
            """
            {"data":[
              {"id":"vendor/texto:free","pricing":{"prompt":"0","completion":"0"},
               "architecture":{"input_modalities":["text"],"output_modalities":["text"]}},
              {"id":"vendor/entrada-imagem:free","pricing":{"prompt":"0","completion":"0"},
               "architecture":{"input_modalities":["text","image"],"output_modalities":["text"]}},
              {"id":"nvidia/nemotron-3.5-super-vl:free","pricing":{"prompt":"0","completion":"0"},
               "architecture":{"input_modalities":["text"],"output_modalities":["text"]}},
              {"id":"vendor/ocr-doc:free","pricing":{"prompt":"0","completion":"0"}},
              {"id":"vendor/audio-turbo:free","pricing":{"prompt":"0","completion":"0"}},
              {"id":"vendor/video-gen:free","pricing":{"prompt":"0","completion":"0"}},
              {"id":"vendor/rerank-x:free","pricing":{"prompt":"0","completion":"0"}},
              {"id":"vendor/sem-arquitetura:free","pricing":{"prompt":"0","completion":"0"}}
            ]}
            """.trimIndent()
        val ids = ModelDiscovery.parse(ProviderPreset.OPENROUTER, body).map { it.id }.sorted()
        assertEquals(listOf("vendor/sem-arquitetura:free", "vendor/texto:free"), ids)
    }

    @Test fun sondaMantemSoQuemRespondeTextoEParaNoLimite() {
        val t =
            PorModelo(
                mapOf(
                    "a" to ok(),
                    "b" to HttpReply(200, """{"choices":[{"message":{"content":null}}]}"""),
                    "c" to HttpReply(429, "limite"),
                    "d" to ok(),
                    "e" to ok(),
                    "f" to ok(),
                ),
            )
        val r = ModelProbe.select(chat, " sk-or-x ", listOf("a", "b", "c", "d", "e", "f"), t, want = 3)
        assertEquals(listOf("a", "d", "e"), r)
        assertEquals(listOf("a", "b", "c", "d", "e"), t.sondados)
    }

    @Test fun sondaRespeitaOMaximoDeTentativas() {
        val t = PorModelo((1..10).associate { "m$it" to HttpReply(500, "erro") })
        val r = ModelProbe.select(chat, "k", (1..10).map { "m$it" }, t, want = 6, maxProbes = 4)
        assertTrue(r.isEmpty())
        assertEquals(4, t.sondados.size)
    }

    @Test fun sondaSemCandidatosDevolveVazio() {
        assertTrue(ModelProbe.select(chat, "k", emptyList(), PorModelo(emptyMap())).isEmpty())
    }

    @Test fun sondaPedeTextoCurtoEnviaAChaveSemEspacos() {
        val t = FakeTransport(listOf(ok()))
        ModelProbe.select(chat, "  sk-or-abc  ", listOf("m1"), t)
        assertEquals("Bearer sk-or-abc", t.lastHeaders["Authorization"])
        assertTrue("\"max_tokens\":256" in t.lastBody)
        assertEquals(chat, t.lastUrl)
    }

    @Test fun chaveInvalidaInterrompeASondagem() {
        val t = PorModelo(mapOf("a" to HttpReply(401, "no"), "b" to ok()))
        try {
            ModelProbe.select(chat, "k", listOf("a", "b"), t)
            fail("deveria interromper")
        } catch (e: AiError.InvalidKey) {
            assertEquals(listOf("a"), t.sondados)
        }
    }

    @Test fun semInternetInterrompeASondagem() {
        val t = PorModelo(mapOf("a" to AiError.NoInternet(), "b" to ok()))
        try {
            ModelProbe.select(chat, "k", listOf("a", "b"), t)
            fail("deveria interromper")
        } catch (e: AiError.NoInternet) {
            assertEquals(listOf("a"), t.sondados)
        }
    }

    @Test fun cadeiaEsgotadaAvisaUmaVezEMantemOErro() {
        var avisos = 0
        val a = ScriptedProvider(listOf(AiError.ServerError(500)))
        val b = ScriptedProvider(listOf(AiError.BadResponse("vazio")))
        val f = FallbackProvider(listOf("a" to a, "b" to b), onAllFailed = { avisos++ })
        try {
            f.complete(AiRequest(listOf(AiMessage(Role.USER, "oi"))))
            fail("deveria falhar")
        } catch (e: AiError) {
            assertEquals(1, avisos)
        }
    }

    @Test fun cadeiaComSucessoNaoAvisa() {
        var avisos = 0
        val a = ScriptedProvider(listOf(AiError.BadResponse("vazio")))
        val b = ScriptedProvider(listOf("pronto"))
        val f = FallbackProvider(listOf("a" to a, "b" to b), onAllFailed = { avisos++ })
        assertEquals("pronto", f.complete(AiRequest(listOf(AiMessage(Role.USER, "oi")))).text)
        assertEquals(0, avisos)
        assertEquals("b", f.lastModel)
    }

    @Test fun falhaNoAvisoNaoEscondeOErroOriginal() {
        val a = ScriptedProvider(listOf(AiError.BadResponse("vazio")))
        val f = FallbackProvider(listOf("a" to a), onAllFailed = { throw IllegalStateException("gancho quebrado") })
        try {
            f.complete(AiRequest(listOf(AiMessage(Role.USER, "oi"))))
            fail("deveria falhar")
        } catch (e: AiError.BadResponse) {
            assertFalse(e.message.isNullOrBlank())
        }
    }
}
