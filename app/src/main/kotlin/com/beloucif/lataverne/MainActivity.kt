package com.beloucif.lataverne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.beloucif.lataverne.ui.LaTaverneApp
import com.beloucif.lataverne.ui.theme.LaTaverneTheme
import com.beloucif.lataverne.ui.theme.ThemePreference
import com.beloucif.lataverne.ui.theme.resolve
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LaTaverneApplication
        setContent {
            val themePreference by app.themeStore.preference.collectAsState(initial = ThemePreference.SYSTEM)
            val scope = rememberCoroutineScope()
            // Resolved in composition (ThemePreference.resolve() is @Composable, can't be called
            // from the plain onToggleTheme click lambda below).
            val isDarkNow = themePreference.resolve()

            LaTaverneTheme(themePreference = themePreference) {
                LaTaverneApp(
                    packRepository = app.packRepository,
                    playerStore = app.playerStore,
                    consentStore = app.consentStore,
                    entitlementRepository = app.entitlementRepository,
                    analyticsTracker = app.analyticsTracker,
                    themePreference = themePreference,
                    onToggleTheme = {
                        scope.launch {
                            // Simple bascule depuis le theme resolu courant, comme le web
                            // (themeStore.toggle()) - ne fige jamais sur "system" apres un
                            // premier tap explicite.
                            app.themeStore.setPreference(
                                if (isDarkNow) ThemePreference.LIGHT else ThemePreference.DARK,
                            )
                        }
                    },
                )
            }
        }
    }
}
