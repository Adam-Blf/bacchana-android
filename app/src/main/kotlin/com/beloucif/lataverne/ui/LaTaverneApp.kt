package com.beloucif.lataverne.ui

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
import com.beloucif.lataverne.BuildConfig
import com.beloucif.lataverne.analytics.AnalyticsTracker
import com.beloucif.lataverne.analytics.ConsentStore
import com.beloucif.lataverne.billing.EntitlementRepository
import com.beloucif.lataverne.content.PackRepository
import com.beloucif.lataverne.content.PremiumCatalogEntry
import com.beloucif.lataverne.core.GameMode
import com.beloucif.lataverne.data.PlayerStore
import com.beloucif.lataverne.ui.screens.AuctionScreen
import com.beloucif.lataverne.ui.screens.BorderlandScreen
import com.beloucif.lataverne.ui.screens.ConsentBanner
import com.beloucif.lataverne.ui.screens.HubScreen
import com.beloucif.lataverne.ui.screens.PaywallScreen
import com.beloucif.lataverne.ui.screens.PromptScreen
import com.beloucif.lataverne.ui.screens.QuizScreen
import com.beloucif.lataverne.ui.screens.RankingScreen
import com.beloucif.lataverne.ui.screens.RecapScreen
import com.beloucif.lataverne.ui.screens.RouletteScreen
import com.beloucif.lataverne.ui.screens.TribunalScreen
import com.beloucif.lataverne.ui.screens.WelcomeScreen
import com.beloucif.lataverne.ui.screens.WouldYouRatherScreen
import kotlinx.coroutines.launch

/** Root composable: owns the NavHost and the shared player session. */
@Composable
fun LaTaverneApp(
    packRepository: PackRepository,
    playerStore: PlayerStore,
    consentStore: ConsentStore,
    entitlementRepository: EntitlementRepository,
    analyticsTracker: AnalyticsTracker,
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
        NavHost(navController = navController, startDestination = LaTaverneRoutes.WELCOME) {
        composable(LaTaverneRoutes.WELCOME) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("welcome") }
            WelcomeScreen(
                players = players,
                onAddPlayer = playerSessionViewModel::addPlayer,
                onRemovePlayer = playerSessionViewModel::removePlayer,
                onStart = {
                    playerSessionViewModel.reactivateAll()
                    navController.navigate(LaTaverneRoutes.HUB)
                },
            )
        }

        composable(LaTaverneRoutes.HUB) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("hub") }
            val isPremium by entitlementRepository.isPremium.collectAsState()
            HubScreen(
                playerCount = players.size,
                isPremium = isPremium,
                packRepository = packRepository,
                premiumCatalog = premiumCatalog,
                onSelectBorderland = { navController.navigate(LaTaverneRoutes.BORDERLAND) },
                onSelectPromptMode = { mode -> navController.navigate(LaTaverneRoutes.prompt(mode.name)) },
                onSelectRoulette = { navController.navigate(LaTaverneRoutes.ROULETTE) },
                onSelectTribunal = { navController.navigate(LaTaverneRoutes.TRIBUNAL) },
                onSelectAuction = { navController.navigate(LaTaverneRoutes.AUCTION) },
                onSelectQuiz = { navController.navigate(LaTaverneRoutes.QUIZ) },
                onSelectRanking = { navController.navigate(LaTaverneRoutes.RANKING) },
                onSelectWouldYouRather = { navController.navigate(LaTaverneRoutes.WOULD_YOU_RATHER) },
                onOpenPaywall = { navController.navigate(LaTaverneRoutes.PAYWALL) },
            )
        }

        composable(LaTaverneRoutes.PAYWALL) {
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

        composable(LaTaverneRoutes.ROULETTE) {
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

        composable(LaTaverneRoutes.TRIBUNAL) {
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

        composable(LaTaverneRoutes.AUCTION) {
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

        composable(LaTaverneRoutes.QUIZ) {
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

        composable(LaTaverneRoutes.RANKING) {
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

        composable(LaTaverneRoutes.WOULD_YOU_RATHER) {
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

        composable(LaTaverneRoutes.BORDERLAND) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("borderland") }
            val viewModel: BorderlandViewModel = viewModel(factory = BorderlandViewModel.Factory())
            BorderlandScreen(
                players = players,
                viewModel = viewModel,
                onGameOver = { navController.navigate(LaTaverneRoutes.RECAP) },
                onExit = { navController.popBackStack() },
            )
        }

        composable(
            route = LaTaverneRoutes.PROMPT,
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

        composable(LaTaverneRoutes.RECAP) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("recap") }
            RecapScreen(
                players = players,
                onReplay = { navController.popBackStack(LaTaverneRoutes.HUB, inclusive = false) },
                onBackToHub = { navController.popBackStack(LaTaverneRoutes.HUB, inclusive = false) },
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
