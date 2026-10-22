package dev.rpgsolo.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

/** CP-04.3: compartilha um PDF pelo seletor do sistema, via cópia temporária no cache (sem permissão de armazenamento). */
object PdfShare {
    /** Nome de arquivo seguro: sem acentos nem símbolos, no máximo 60 caracteres. */
    fun fileName(title: String, suffix: String): String {
        val base = java.text.Normalizer.normalize(title, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^A-Za-z0-9]+"), "-")
            .trim('-')
            .take(60)
            .ifEmpty { "aventura" }
        return "$base-$suffix.pdf"
    }

    fun chooser(context: Context, bytes: ByteArray, name: String, title: String): Intent {
        val dir = File(context.cacheDir, "pdf").apply { mkdirs() }
        val file = File(dir, name).apply { writeBytes(bytes) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, title)
    }
}
