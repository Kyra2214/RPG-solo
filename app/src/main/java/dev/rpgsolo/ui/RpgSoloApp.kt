package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** As 6 abas do plano (Fase 10). As 6 abas têm conteúdo. */
enum class Tab(val label: String) {
    JOGAR("Jogar"),
    PERSONAGENS("Personagens"),
    REGRAS("Regras"),
    CAMPANHAS("Campanhas"),
    SISTEMAS("Sistemas"),
    CONFIG("Config")
}

@Composable
fun RpgSoloApp() {
    val dark = isSystemInDarkTheme()
    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        val model = remember { AppModel() }
        var selected by remember { mutableStateOf(Tab.JOGAR) }
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Tab.values().forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selected,
                            onClick = { selected = tab },
                            icon = {},
                            label = { Text(tab.label, maxLines = 1) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (selected) {
                    Tab.JOGAR -> PlayScreen(model)
                    Tab.CONFIG -> ConfigScreen(model)
                    Tab.PERSONAGENS -> CharactersScreen(model)
                    Tab.REGRAS -> RulesScreen(model)
                    Tab.CAMPANHAS -> CampaignsScreen(model)
                    Tab.SISTEMAS -> SystemsScreen(model)
                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(selected.label) }
                }
            }
        }
    }
}
