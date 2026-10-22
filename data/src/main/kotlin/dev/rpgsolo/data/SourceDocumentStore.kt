package dev.rpgsolo.data

import dev.rpgsolo.engine.SourceImporter
import java.io.File
import java.io.FileOutputStream
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/** Armazena documentos originais no diretório privado do app, fora do log narrativo e do banco Room. */
class SourceDocumentStore(private val directory: File) {
    private fun fileFor(sourceId: String): File {
        require(sourceId.matches(Regex("[A-Za-z0-9_-]{1,128}"))) { "ID de fonte inválido" }
        return File(directory, "$sourceId.source")
    }

    /** Grava por arquivo temporário no mesmo diretório para evitar conteúdo parcial após interrupção. */
    fun save(
        sourceId: String,
        bytes: ByteArray,
        expectedSha256: String,
    ): File {
        require(bytes.isNotEmpty()) { "arquivo de fonte vazio" }
        require(bytes.size <= SourceImporter.MAX_BYTES) { "arquivo de fonte acima do limite permitido" }
        require(SourceImporter.sha256(bytes) == expectedSha256) { "hash do arquivo não corresponde aos metadados" }
        if (!directory.exists() && !directory.mkdirs()) throw IllegalStateException("não foi possível criar armazenamento de fontes")
        val target = fileFor(sourceId)
        val temp = File.createTempFile("source-", ".tmp", directory)
        try {
            FileOutputStream(temp).use { stream ->
                stream.write(bytes)
                stream.fd.sync()
            }
            try {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            return target
        } finally {
            temp.delete()
        }
    }

    /** Retorna null quando ainda não existe conteúdo; conteúdo divergente falha fechado. */
    fun read(
        sourceId: String,
        expectedSha256: String,
        expectedByteSize: Int,
    ): ByteArray? {
        val file = fileFor(sourceId)
        if (!file.isFile) return null
        val bytes = file.readBytes()
        check(bytes.size == expectedByteSize) { "tamanho do documento-fonte diverge dos metadados" }
        check(SourceImporter.sha256(bytes) == expectedSha256) { "hash do documento-fonte diverge dos metadados" }
        return bytes
    }

    fun exists(sourceId: String): Boolean = fileFor(sourceId).isFile

    fun delete(sourceId: String): Boolean {
        val file = fileFor(sourceId)
        return !file.exists() || file.delete()
    }
}
