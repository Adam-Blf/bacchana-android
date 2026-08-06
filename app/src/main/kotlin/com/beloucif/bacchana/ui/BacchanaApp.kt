package com.beloucif.bacchana.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beloucif.bacchana.BuildConfig
import com.beloucif.bacchana.analytics.AnalyticsTracker
import com.beloucif.bacchana.analytics.ConsentStore
import com.beloucif.bacchana.billing.EntitlementRepository
import com.beloucif.bacchana.content.PackRepository
import com.beloucif.bacchana.content.PremiumCatalogEntry
import com.beloucif.bacchana.core.GameMode
import com.beloucif.bacchana.data.PlayerStore
import com.beloucif.bacchana.ui.screens.AuctionScreen
import com.beloucif.bacchana.ui.screens.BorderlandScreen
import com.beloucif.bacchana.ui.screens.ConsentBanner
import com.beloucif.bacchana.ui.screens.HubScreen
import com.beloucif.bacchana.ui.screens.PaywallScreen
import com.beloucif.bacchana.ui.screens.PromptScreen
import com.beloucif.bacchana.ui.screens.QuizScreen
import com.beloucif.bacchana.ui.screens.RankingScreen
import com.beloucif.bacchana.ui.screens.RecapScreen
import com.beloucif.bacchana.ui.screens.RouletteScreen
import com.beloucif.bacchana.ui.screens.SettingsScreen
import com.beloucif.bacchana.ui.screens.TribunalScreen
import com.beloucif.bacchana.ui.screens.WelcomeScreen
import com.beloucif.bacchana.ui.screens.WouldYouRatherScreen
import com.beloucif.bacchana.ui.theme.ThemePreference
import kotlinx.coroutines.launch

/** Root composable: owns the NavHost and the shared player session. */
@Composable
fun BacchanaApp(
    packRepository: PackRepository,
    playerStore: PlayerStore,
    consentStore: ConsentStore,
    entitlementRepository: EntitlementRepository,
    analyticsTracker: AnalyticsTracker,
    themePreference: ThemePreference,
    onToggleTheme: () -> Unit,
) {
    val navController = rememberNavController()
    val playerSessionViewModel: PlayerSessionViewModel =
        viewModel(factory = PlayerSessionViewModel.Factory(playerStore))
    val players by playerSessionViewModel.players.collectAsState()

    var premiumCatalog by remember { mutableStateOf<List<PremiumCatalogEntry>>(emptyList()) }
    LaunchedEffect(Unit) { premiumCatalog = packRepository.loadPremiumCatalog() }

    // Defaults to true (banner hidden) until the first DataStore emission arrives, avoiding a
    // one-frame flash of the banner for players who already decided in a previous session.
    val hasDecidedConsent by consentStore.hasDecided.collectAsState(initial = true)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = BacchanaRoutes.WELCOME) {
        composable(BacchanaRoutes.WELCOME) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("welcome") }
            WelcomeScreen(
                players = players,
                onAddPlayer = playerSessionViewModel::addPlayer,
                onRemovePlayer = playerSessionViewModel::removePlayer,
                onSetPlayerAttributes = playerSessionViewModel::setPlayerAttributes,
                onStart = {
                    playerSessionViewModel.reactivateAll()
                    navController.navigate(BacchanaRoutes.HUB)
                },
            )
        }

        composable(BacchanaRoutes.HUB) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("hub") }
            val isPremium by entitlementRepository.isPremium.collectAsState()
            HubScreen(
                playerCount = players.size,
                isPremium = isPremium,
                packRepository = packRepository,
                premiumCatalog = premiumCatalog,
                onSelectBorderland = { navController.navigate(BacchanaRoutes.BORDERLAND) },
                onSelectPromptMode = { mode -> navController.navigate(BacchanaRoutes.prompt(mode.name)) },
                onSelectRoulette = { navController.navigate(BacchanaRoutes.ROULETTE) },
                onSelectTribunal = { navController.navigate(BacchanaRoutes.TRIBUNAL) },
                onSelectAuction = { navController.navigate(BacchanaRoutes.AUCTION) },
                onSelectQuiz = { navController.navigate(BacchanaRoutes.QUIZ) },
                onSelectRanking = { navController.navigate(BacchanaRoutes.RANKING) },
                onSelectWouldYouRather = { navController.navigate(BacchanaRoutes.WOULD_YOU_RATHER) },
                onOpenPaywall = { navController.navigate(BacchanaRoutes.PAYWALL) },
                onOpenSettings = { navController.navigate(BacchanaRoutes.SETTINGS) },
                themePreference = themePreference,
                onToggleTheme = onToggleTheme,
            )
        }

        composable(BacchanaRoutes.SETTINGS) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("settings") }
            val isPremium by entitlementRepository.isPremium.collectAsState()
            val analyticsEnabled by consentStore.analyticsGranted.collectAsState(initial = false)
            SettingsScreen(
                themePreference = themePreference,
                onToggleTheme = onToggleTheme,
                isPremium = isPremium,
                billingEnabled = BuildConfig.BILLING_ENABLED,
                onOpenPaywall = { navController.navigate(BacchanaRoutes.PAYWALL) },
                onRestorePurchases = entitlementRepository::restorePurchases,
                analyticsEnabled = analyticsEnabled,
                onSetAnalyticsConsent = { granted ->
                    scope.launch {
                        consentStore.setConsent(granted)
                        analyticsTracker.setConsent(granted)
                    }
                },
                appVersionName = BuildConfig.VERSION_NAME,
                onResetTablee = playerSessionViewModel::resetAll,
                onBack = { navController.popBackStack() },
            )
        }

        composable(BacchanaRoutes.PAYWALL) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("paywall") }
            val context = LocalContext.current
            PaywallScreen(
                billingEnabled = BuildConfig.BILLING_ENABLED,
                premiumCatalog = premiumCatalog,
                analyticsTracker = analyticsTracker,
                onPurchase = { plan -> entitlementRepository.purchasePremium(context as Activity, plan) },
                onRestore = entitlementRepository::restorePurchases,
                onClose = { navController.popBackStack() },
            )
        }

        composable(BacchanaRoutes.ROULETTE) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("roulette") }
            RouletteScreen(
                onQuit = { spinsPlayed ->
                    analyticsTracker.trackEvent(
                        "session_completed",
                        mapOf("mode" to "roulette", "turns" to spinsPlayed),
                    )
                    navController.popBackStack()
                },
            )
        }

        composable(BacchanaRoutes.TRIBUNAL) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("tribunal") }
            TribunalScreen(
                players = players,
                onQuit = { trialsPlayed ->
                    analyticsTracker.trackEvent(
                        "session_completed",
                        mapOf("mode" to "tribunal", "turns" to trialsPlayed),
                    )
                    navController.popBackStack()
                },
            )
        }

        composable(BacchanaRoutes.AUCTION) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("auction") }
            AuctionScreen(
                onQuit = { roundsPlayed ->
                    analyticsTracker.trackEvent(
                        "session_completed",
                        mapOf("mode" to "auction", "turns" to roundsPlayed),
                    )
                    navController.popBackStack()
                },
            )
        }

        composable(BacchanaRoutes.QUIZ) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("quiz") }
            QuizScreen(
                players = players,
                onQuit = { turnsPlayed ->
                    analyticsTracker.trackEvent(
                        "session_completed",
                        mapOf("mode" to "quiz", "turns" to turnsPlayed),
                    )
                    navController.popBackStack()
                },
            )
        }

        composable(BacchanaRoutes.RANKING) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("ranking") }
            RankingScreen(
                players = players,
                onQuit = { roundsPlayed ->
                    analyticsTracker.trackEvent(
                        "session_completed",
                        mapOf("mode" to "ranking", "turns" to roundsPlayed),
                    )
                    navController.popBackStack()
                },
            )
        }

        composable(BacchanaRoutes.WOULD_YOU_RATHER) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("would_you_rather") }
            WouldYouRatherScreen(
                players = players,
                onQuit = { roundsPlayed ->
                    analyticsTracker.trackEvent(
                        "session_completed",
                        mapOf("mode" to "would_you_rather", "turns" to roundsPlayed),
                    )
                    navController.popBackStack()
                },
            )
        }

        composable(BacchanaRoutes.BORDERLAND) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("borderland") }
            val viewModel: BorderlandViewModel = viewModel(factory = BorderlandViewModel.Factory())
            BorderlandScreen(
                players = players,
                viewModel = viewModel,
                onGameOver = { navController.navigate(BacchanaRoutes.RECAP) },
                onExit = { navController.popBackStack() },
            )
        }

        composable(
            route = BacchanaRoutes.PROMPT,
            arguments = listOf(navArgument("mode") { type = NavType.StringType }),
        ) { backStackEntry ->
            val modeName = backStackEntry.arguments?.getString("mode") ?: GameMode.PICOLO.name
            val mode = runCatching { GameMode.valueOf(modeName) }.getOrDefault(GameMode.PICOLO)
            LaunchedEffect(mode) { analyticsTracker.trackScreen("prompt_${mode.name}") }
            val viewModel: PromptViewModel = viewModel(factory = PromptViewModel.Factory(packRepository))
            PromptScreen(
                mode = mode,
                players = players,
                viewModel = viewModel,
                onExit = { navController.popBackStack() },
            )
        }

        composable(BacchanaRoutes.RECAP) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("recap") }
            RecapScreen(
                players = players,
                onReplay = { navController.popBackStack(BacchanaRoutes.HUB, inclusive = false) },
                onBackToHub = { navController.popBackStack(BacchanaRoutes.HUB, inclusive = false) },
            )
        }
        }

        if (!hasDecidedConsent) {
            ConsentBanner(
                onAccept = {
                    scope.launch {
                        consentStore.setConsent(true)
                        analyticsTracker.setConsent(true)
                    }
                },
                onDecline = {
                    scope.launch {
                        consentStore.setConsent(false)
                        analyticsTracker.setConsent(false)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            )
        }
    }
}
