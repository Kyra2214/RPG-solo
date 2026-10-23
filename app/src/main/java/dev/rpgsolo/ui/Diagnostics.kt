package dev.rpgsolo.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import dev.rpgsolo.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * U-08: relatório de diagnóstico local. Guarda só a classe e os frames das falhas não tratadas;
 * nunca grava mensagem de exceção, prompt, texto de jogada, dado de campanha nem chave de API.
 */
object Diagnostics {
    private const val DIR = "diagnostics"
    private const val CRASH_FILE = "last-crash.txt"
    private const val REPORT_FILE = "rpg-solo-diagnostico.txt"
    private const val MAX_FRAMES = 40
    private const val MAX_CAUSES = 5

    @Volatile private var installed = false

    /** Classe e frames da falha e das causas encadeadas, sem a mensagem (que pode conter texto do jogador). */
    fun describe(error: Throwable): String {
        val out = StringBuilder()
        var current: Throwable? = error
        var depth = 0
        while (current != null && depth < MAX_CAUSES) {
            out.append(if (depth == 0) "" else "Causa: ").append(current.javaClass.name).append('\n')
            current.stackTrace.take(MAX_FRAMES).forEach { out.append("  at ").append(it).append('\n') }
            val next = current.cause
            current = if (next === current) null else next
            depth++
        }
        return out.toString()
    }

    /** Encadeia um handler que registra a última falha não tratada e repassa ao handler anterior. */
    fun install(context: Context) {
        if (installed) return
        installed = true
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching { record(app, thread.name, error) }
            previous?.uncaughtException(thread, error)
        }
    }

    private fun timestamp(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

    private fun crashFile(context: Context): File = File(File(context.filesDir, DIR).apply { mkdirs() }, CRASH_FILE)

    private fun record(
        context: Context,
        threadName: String,
        error: Throwable,
    ) {
        crashFile(context).writeText("${timestamp()} · thread: $threadName\n" + describe(error), Charsets.UTF_8)
    }

    fun lastCrash(context: Context): String? = crashFile(context).takeIf { it.isFile }?.readText(Charsets.UTF_8)

    fun clear(context: Context) {
        crashFile(context).delete()
    }

    fun buildReport(context: Context): String {
        val version =
            runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull().orEmpty()
        val crash = lastCrash(context)
        return buildString {
            appendLine(context.getString(R.string.diag_report_title))
            appendLine(context.getString(R.string.diag_report_privacy))
            appendLine(context.getString(R.string.diag_report_generated, timestamp()))
            appendLine(context.getString(R.string.diag_report_app, version, context.packageName))
            appendLine(context.getString(R.string.diag_report_android, Build.VERSION.RELEASE, Build.VERSION.SDK_INT))
            appendLine(context.getString(R.string.diag_report_device, Build.MANUFACTURER, Build.MODEL))
            appendLine()
            if (crash == null) {
                appendLine(context.getString(R.string.diag_report_no_crash))
            } else {
                appendLine(context.getString(R.string.diag_report_last_crash))
                append(crash)
            }
        }
    }

    fun chooser(context: Context): Intent {
        val dir = File(context.cacheDir, DIR).apply { mkdirs() }
        val file = File(dir, REPORT_FILE).apply { writeText(buildReport(context), Charsets.UTF_8) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        val title = context.getString(R.string.diag_share_title)
        val send =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        return Intent.createChooser(send, title)
    }
}
