package com.beloucif.blackout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.beloucif.blackout.ui.BlackOutApp
import com.beloucif.blackout.ui.theme.BlackOutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as BlackOutApplication
        setContent {
            BlackOutTheme {
                BlackOutApp(
                    packRepository = app.packRepository,
                    playerStore = app.playerStore,
                    entitlementRepository = app.entitlementRepository,
                    analyticsTracker = app.analyticsTracker,
                )
            }
        }
    }
}
