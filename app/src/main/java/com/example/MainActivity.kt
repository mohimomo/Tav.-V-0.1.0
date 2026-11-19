package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppLanguage
import com.example.ui.AppStrings
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.components.AboutAppDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AlarmsDirectoryScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.CircuitsScreen
import com.example.ui.screens.ComponentsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.OfflineManualScreen
import com.example.ui.screens.OperationGuideScreen
import com.example.ui.screens.PresentationScreen
import com.example.ui.screens.QrScannerScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ShiftLogScreen
import com.example.ui.screens.TelemetryScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TavFurnaceApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TavFurnaceApp(viewModel: AppViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isAr = uiState.language == AppLanguage.ARABIC
    var showLanguageMenu by remember { mutableStateOf(false) }

    // Handle back button: return to home screen if on a sub-screen
    BackHandler(enabled = uiState.currentScreen != NavigationScreen.HOME) {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CyanPrimary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.navigateTo(NavigationScreen.HOME) }
                            .testTag("appbar_icon_btn")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "TAV Furnace",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(2.dp)
                        )
                    }
                },
                actions = {
                    // Language toggle & dropdown selector button
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showLanguageMenu = true }
                                .padding(end = 4.dp)
                                .testTag("appbar_language_toggle_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.language.badge,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Language",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🇸🇦 العربية (AR)") },
                                onClick = {
                                    viewModel.setLanguage(AppLanguage.ARABIC)
                                    showLanguageMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.language == AppLanguage.ARABIC) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CyanPrimary)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🇫🇷 Français (FR)") },
                                onClick = {
                                    viewModel.setLanguage(AppLanguage.FRENCH)
                                    showLanguageMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.language == AppLanguage.FRENCH) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CyanPrimary)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🇬🇧 English (EN)") },
                                onClick = {
                                    viewModel.setLanguage(AppLanguage.ENGLISH)
                                    showLanguageMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.language == AppLanguage.ENGLISH) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CyanPrimary)
                                    }
                                }
                            )
                        }
                    }

                    // About App button
                    IconButton(
                        onClick = { viewModel.openAboutDialog() },
                        modifier = Modifier.testTag("appbar_about_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = AppStrings.navAbout(uiState.language),
                            tint = CyanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = uiState.currentScreen == NavigationScreen.HOME,
                    onClick = { viewModel.navigateTo(NavigationScreen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(AppStrings.navHome(uiState.language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        indicatorColor = CyanPrimary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. Components & Schematics
                NavigationBarItem(
                    selected = uiState.currentScreen == NavigationScreen.COMPONENTS,
                    onClick = { viewModel.navigateTo(NavigationScreen.COMPONENTS) },
                    icon = { Icon(Icons.Default.Architecture, contentDescription = "Components") },
                    label = { Text(AppStrings.navComponents(uiState.language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        indicatorColor = CyanPrimary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_item_components")
                )

                // 3. Alarms & Faults
                NavigationBarItem(
                    selected = uiState.currentScreen == NavigationScreen.ALARMS,
                    onClick = { viewModel.navigateTo(NavigationScreen.ALARMS) },
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Alarms") },
                    label = { Text(AppStrings.navAlarms(uiState.language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FurnaceAmber,
                        indicatorColor = FurnaceAmber.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_item_alarms")
                )

                // 4. Interactive Circuits & Schematics
                NavigationBarItem(
                    selected = uiState.currentScreen == NavigationScreen.CIRCUITS,
                    onClick = { viewModel.navigateTo(NavigationScreen.CIRCUITS) },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = "Circuits") },
                    label = { Text(AppStrings.navCircuits(uiState.language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        indicatorColor = CyanPrimary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_item_circuits")
                )

                // 5. Smart Search
                NavigationBarItem(
                    selected = uiState.currentScreen == NavigationScreen.SEARCH,
                    onClick = { viewModel.navigateTo(NavigationScreen.SEARCH) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text(AppStrings.navSearch(uiState.language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        indicatorColor = CyanPrimary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_item_search")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(targetState = uiState.currentScreen, label = "screen_transition") { screen ->
            when (screen) {
                NavigationScreen.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.SEARCH -> SearchScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.ALARMS -> AlarmsDirectoryScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.COMPONENTS -> ComponentsScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.MAINTENANCE -> MaintenanceScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.OPERATION -> OperationGuideScreen(uiState = uiState, contentPadding = innerPadding)
                NavigationScreen.MANUAL -> OfflineManualScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.ABOUT -> AboutScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.CIRCUITS -> CircuitsScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.AI_CHAT -> AiChatScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.CALCULATORS -> CalculatorsScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.SHIFT_LOG -> ShiftLogScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.TELEMETRY -> TelemetryScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.SCANNER -> QrScannerScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.PRESENTATION -> PresentationScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.PREDICTIVE_HEALTH -> com.example.ui.screens.PredictiveHealthScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.QR_LABELS -> com.example.ui.screens.QrLabelGeneratorScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.PYROMETRY -> com.example.ui.screens.PyrometryScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.VIBRATION_ANALYSIS -> com.example.ui.screens.VibrationAnalysisScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.SAFETY_LOTO -> com.example.ui.screens.SafetyLotoScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.RECIPE_DESIGNER -> com.example.ui.screens.RecipeDesignerScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.FAULT_TREE -> com.example.ui.screens.FaultTreeScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.SHIFT_HANDOVER -> com.example.ui.screens.ShiftHandoverScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
                NavigationScreen.DATA_EXCHANGE -> com.example.ui.screens.DataExchangeScreen(uiState = uiState, viewModel = viewModel, contentPadding = innerPadding)
            }
        }
    }

    if (uiState.showAboutDialog) {
        AboutAppDialog(
            language = uiState.language,
            onDismissRequest = { viewModel.closeAboutDialog() }
        )
    }
}
