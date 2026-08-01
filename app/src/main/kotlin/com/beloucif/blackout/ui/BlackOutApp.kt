package com.beloucif.blackout.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beloucif.blackout.analytics.AnalyticsTracker
import com.beloucif.blackout.billing.EntitlementRepository
import com.beloucif.blackout.content.PackRepository
import com.beloucif.blackout.core.GameMode
import com.beloucif.blackout.data.PlayerStore
import com.beloucif.blackout.ui.screens.BorderlandScreen
import com.beloucif.blackout.ui.screens.HubScreen
import com.beloucif.blackout.ui.screens.PromptScreen
import com.beloucif.blackout.ui.screens.RecapScreen
import com.beloucif.blackout.ui.screens.WelcomeScreen

/** Root composable: owns the NavHost and the shared player session. */
@Composable
fun BlackOutApp(
    packRepository: PackRepository,
    playerStore: PlayerStore,
    entitlementRepository: EntitlementRepository,
    analyticsTracker: AnalyticsTracker,
) {
    val navController = rememberNavController()
    val playerSessionViewModel: PlayerSessionViewModel =
        viewModel(factory = PlayerSessionViewModel.Factory(playerStore))
    val players by playerSessionViewModel.players.collectAsState()

    NavHost(navController = navController, startDestination = BlackOutRoutes.WELCOME) {
        composable(BlackOutRoutes.WELCOME) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("welcome") }
            WelcomeScreen(
                players = players,
                onAddPlayer = playerSessionViewModel::addPlayer,
                onRemovePlayer = playerSessionViewModel::removePlayer,
                onStart = {
                    playerSessionViewModel.reactivateAll()
                    navController.navigate(BlackOutRoutes.HUB)
                },
            )
        }

        composable(BlackOutRoutes.HUB) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("hub") }
            val isPremium by entitlementRepository.isPremium.collectAsState()
            HubScreen(
                playerCount = players.size,
                isPremium = isPremium,
                packRepository = packRepository,
                onSelectBorderland = { navController.navigate(BlackOutRoutes.BORDERLAND) },
                onSelectPromptMode = { mode -> navController.navigate(BlackOutRoutes.prompt(mode.name)) },
            )
        }

        composable(BlackOutRoutes.BORDERLAND) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("borderland") }
            val viewModel: BorderlandViewModel = viewModel(factory = BorderlandViewModel.Factory())
            BorderlandScreen(
                players = players,
                viewModel = viewModel,
                onGameOver = { navController.navigate(BlackOutRoutes.RECAP) },
                onExit = { navController.popBackStack() },
            )
        }

        composable(
            route = BlackOutRoutes.PROMPT,
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

        composable(BlackOutRoutes.RECAP) {
            LaunchedEffect(Unit) { analyticsTracker.trackScreen("recap") }
            RecapScreen(
                players = players,
                onReplay = { navController.popBackStack(BlackOutRoutes.HUB, inclusive = false) },
                onBackToHub = { navController.popBackStack(BlackOutRoutes.HUB, inclusive = false) },
            )
        }
    }
}
