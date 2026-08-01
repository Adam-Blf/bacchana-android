package com.beloucif.lataverne.ui

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
import com.beloucif.lataverne.analytics.AnalyticsTracker
import com.beloucif.lataverne.billing.EntitlementRepository
import com.beloucif.lataverne.content.PackRepository
import com.beloucif.lataverne.core.GameMode
import com.beloucif.lataverne.data.PlayerStore
import com.beloucif.lataverne.ui.screens.BorderlandScreen
import com.beloucif.lataverne.ui.screens.HubScreen
import com.beloucif.lataverne.ui.screens.PromptScreen
import com.beloucif.lataverne.ui.screens.RecapScreen
import com.beloucif.lataverne.ui.screens.WelcomeScreen

/** Root composable: owns the NavHost and the shared player session. */
@Composable
fun LaTaverneApp(
    packRepository: PackRepository,
    playerStore: PlayerStore,
    entitlementRepository: EntitlementRepository,
    analyticsTracker: AnalyticsTracker,
) {
    val navController = rememberNavController()
    val playerSessionViewModel: PlayerSessionViewModel =
        viewModel(factory = PlayerSessionViewModel.Factory(playerStore))
    val players by playerSessionViewModel.players.collectAsState()

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
                onSelectBorderland = { navController.navigate(LaTaverneRoutes.BORDERLAND) },
                onSelectPromptMode = { mode -> navController.navigate(LaTaverneRoutes.prompt(mode.name)) },
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
}
