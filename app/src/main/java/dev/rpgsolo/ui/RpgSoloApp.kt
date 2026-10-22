package dev.rpgsolo.ui

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import dev.rpgsolo.R

/** As 6 abas do plano (Fase 10). */
enum class Tab(@StringRes val labelRes: Int, val icon: String, @StringRes val titleRes: Int) {
    JOGAR(R.string.tab_jogar, "🎲", R.string.title_jogar),
    PERSONAGENS(R.string.tab_personagens, "🧙", R.string.title_personagens),
    REGRAS(R.string.tab_regras, "📖", R.string.title_regras),
    CAMPANHAS(R.string.tab_campanhas, "🗺", R.string.title_campanhas),
    SISTEMAS(R.string.tab_sistemas, "⚙", R.string.title_sistemas),
    CONFIG(R.string.tab_config, "🔑", R.string.title_config)
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
                    Text(stringResource(selected.titleRes), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                    Text(stringResource(R.string.app_brand), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Tab.values().forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selected,
                            onClick = { selectedName = tab.name },
                            icon = { Text(tab.icon, modifier = Modifier.semantics { contentDescription = stringResource(R.string.navigation_label, stringResource(tab.labelRes)) }, fontSize = 20.sp) },
                            label = { Text(stringResource(tab.labelRes), maxLines = 1, style = MaterialTheme.typography.labelSmall) },
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
                if (!model.ready) {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val failure = model.startupError
                        if (failure == null) {
                            CircularProgressIndicator()
                            Text(stringResource(R.string.startup_loading), Modifier.padding(top = 12.dp))
                        } else {
                            Text(stringResource(R.string.startup_error, failure), color = MaterialTheme.colorScheme.error)
                        }
                    }
                    return@Box
                }
                Crossfade(targetState = selected, label = stringResource(R.string.crossfade_tab)) { tab ->
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
