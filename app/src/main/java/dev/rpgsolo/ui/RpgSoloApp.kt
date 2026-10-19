package dev.rpgsolo.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** As 6 abas do plano (Fase 10). */
enum class Tab(val label: String, val icon: String, val title: String) {
    JOGAR("Jogar", "🎲", "A mesa"),
    PERSONAGENS("Heróis", "🧙", "Personagens"),
    REGRAS("Regras", "📖", "Livro de regras"),
    CAMPANHAS("Campanhas", "🗺", "Campanhas"),
    SISTEMAS("Sistemas", "⚙", "Sistemas"),
    CONFIG("Config", "🔑", "Configuração")
}

@Composable
fun RpgSoloApp() {
    RpgTheme {
        val model: AppModel = viewModel()
        var selectedName by rememberSaveable { mutableStateOf(Tab.JOGAR.name) }
        val selected = Tab.valueOf(selectedName)
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density = density.density, fontScale = model.fontScale)) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(selected.title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                    Text("RPG SOLO", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Tab.values().forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selected,
                            onClick = { selectedName = tab.name },
                            icon = { Text(tab.icon, fontSize = 20.sp) },
                            label = { Text(tab.label, maxLines = 1, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                Crossfade(targetState = selected, label = "aba") { tab ->
                    when (tab) {
                        Tab.JOGAR -> PlayScreen(model) { destination -> selectedName = destination.name }
                        Tab.CONFIG -> ConfigScreen(model)
                        Tab.PERSONAGENS -> CharactersScreen(model)
                        Tab.REGRAS -> RulesScreen(model)
                        Tab.CAMPANHAS -> CampaignsScreen(model)
                        Tab.SISTEMAS -> SystemsScreen(model)
                    }
                }
            }
        }
        }
    }
}
