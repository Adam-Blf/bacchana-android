package com.beloucif.lataverne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.beloucif.lataverne.ui.LaTaverneApp
import com.beloucif.lataverne.ui.theme.LaTaverneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LaTaverneApplication
        setContent {
            LaTaverneTheme {
                LaTaverneApp(
                    packRepository = app.packRepository,
                    playerStore = app.playerStore,
                    entitlementRepository = app.entitlementRepository,
                    analyticsTracker = app.analyticsTracker,
                )
            }
        }
    }
}
