package dev.rpgsolo.ui

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rpgsolo.R

/** As 6 abas do plano (Fase 10). U-06: [inBar] = destino direto da barra; as demais ficam no menu "Mais". */
enum class Tab(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    val inBar: Boolean = true,
) {
    JOGAR(R.string.tab_jogar, Icons.Filled.PlayArrow, R.string.title_jogar),
    PERSONAGENS(R.string.tab_personagens, Icons.Filled.Person, R.string.title_personagens),
    REGRAS(R.string.tab_regras, Icons.AutoMirrored.Filled.List, R.string.title_regras, inBar = false),
    CAMPANHAS(R.string.tab_campanhas, Icons.Filled.DateRange, R.string.title_campanhas),
    SISTEMAS(R.string.tab_sistemas, Icons.Filled.Build, R.string.title_sistemas, inBar = false),
    CONFIG(R.string.tab_config, Icons.Filled.Settings, R.string.title_config, inBar = false),
}

@Composable
fun RpgSoloApp() {
    RpgTheme {
        val model: AppModel = viewModel()
        var selectedName by rememberSaveable { mutableStateOf(Tab.JOGAR.name) }
        val selected = Tab.valueOf(selectedName)
        // U-05: ao concluir as três etapas do primeiro uso fora da aba Jogar, leva o jogador até ela.
        val setupComplete = model.ready && model.setupStatus().complete
        var wasComplete by remember { mutableStateOf<Boolean?>(null) }
        LaunchedEffect(model.ready, setupComplete) {
            if (model.ready) {
                if (wasComplete == false && setupComplete) selectedName = Tab.JOGAR.name
                wasComplete = setupComplete
            }
        }
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density = density.density, fontScale = model.fontScale)) {
            // U-07: janelas largas (tablet, paisagem, dobrável aberto) usam trilho lateral com as 6 abas e conteúdo com largura limitada.
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val wide = maxWidth >= WIDE_LAYOUT_MIN_WIDTH
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                stringResource(selected.titleRes),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                stringResource(R.string.app_brand),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    bottomBar = {
                        if (!wide) {
                            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                                Tab.values().filter { it.inBar }.forEach { tab ->
                                    NavigationBarItem(
                                        selected = tab == selected,
                                        onClick = { selectedName = tab.name },
                                        icon = {
                                            val tabLabel = stringResource(tab.labelRes)
                                            val navigationLabel = stringResource(R.string.navigation_label, tabLabel)
                                            Icon(
                                                tab.icon,
                                                contentDescription = null,
                                                modifier =
                                                    Modifier.semantics {
                                                        contentDescription = navigationLabel
                                                    },
                                            )
                                        },
                                        label = {
                                            Text(
                                                stringResource(tab.labelRes),
                                                maxLines = 1,
                                                style = MaterialTheme.typography.labelSmall,
                                            )
                                        },
                                        colors =
                                            NavigationBarItemDefaults.colors(
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            ),
                                    )
                                }
                                var moreOpen by remember { mutableStateOf(false) }
                                val moreLabel = stringResource(R.string.tab_more)
                                val moreNavigationLabel = stringResource(R.string.navigation_label, moreLabel)
                                NavigationBarItem(
                                    selected = !selected.inBar,
                                    onClick = { moreOpen = true },
                                    icon = {
                                        Icon(
                                            Icons.Filled.Menu,
                                            contentDescription = null,
                                            modifier = Modifier.semantics { contentDescription = moreNavigationLabel },
                                        )
                                        DropdownMenu(expanded = moreOpen, onDismissRequest = { moreOpen = false }) {
                                            Tab.values().filter { !it.inBar }.forEach { tab ->
                                                val tabLabel = stringResource(tab.labelRes)
                                                val navigationLabel = stringResource(R.string.navigation_label, tabLabel)
                                                DropdownMenuItem(
                                                    text = { Text(tabLabel) },
                                                    leadingIcon = { Icon(tab.icon, contentDescription = null) },
                                                    onClick = {
                                                        selectedName = tab.name
                                                        moreOpen = false
                                                    },
                                                    modifier = Modifier.semantics { contentDescription = navigationLabel },
                                                )
                                            }
                                        }
                                    },
                                    label = { Text(moreLabel, maxLines = 1, style = MaterialTheme.typography.labelSmall) },
                                    colors =
                                        NavigationBarItemDefaults.colors(
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        ),
                                )
                            }
                        }
                    },
                ) { padding ->
                    Row(Modifier.fillMaxSize().padding(padding)) {
                        if (wide) {
                            NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                                Tab.values().forEach { tab ->
                                    val tabLabel = stringResource(tab.labelRes)
                                    val navigationLabel = stringResource(R.string.navigation_label, tabLabel)
                                    NavigationRailItem(
                                        selected = tab == selected,
                                        onClick = { selectedName = tab.name },
                                        icon = {
                                            Icon(
                                                tab.icon,
                                                contentDescription = null,
                                                modifier = Modifier.semantics { contentDescription = navigationLabel },
                                            )
                                        },
                                        label = { Text(tabLabel, maxLines = 1, style = MaterialTheme.typography.labelSmall) },
                                    )
                                }
                            }
                        }
                        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                            Box(Modifier.widthIn(max = WIDE_CONTENT_MAX_WIDTH).fillMaxSize()) {
                                if (!model.ready) {
                                    Column(
                                        Modifier.fillMaxSize().padding(24.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
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
        }
    }
}

private val WIDE_LAYOUT_MIN_WIDTH = 600.dp
private val WIDE_CONTENT_MAX_WIDTH = 840.dp
