package dev.rpgsolo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.rpgsolo.ui.Diagnostics
import dev.rpgsolo.ui.RpgSoloApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Diagnostics.install(this)
        setContent { RpgSoloApp() }
    }
}
