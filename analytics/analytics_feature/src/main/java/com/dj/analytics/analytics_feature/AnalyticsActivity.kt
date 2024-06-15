package com.dj.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dj.analytics.data.di.analyticsDataModule
import com.dj.analytics.presentation.AnalyticsDashboardScreenRoot
import com.dj.analytics.presentation.di.analyticsPresentationModule
import com.dj.core.presentation.designsystem.RunTheme
import com.google.android.play.core.splitcompat.SplitCompat
import org.koin.core.context.loadKoinModules

class AnalyticsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(
            listOf(analyticsDataModule, analyticsPresentationModule)
        )
        SplitCompat.installActivity(this)

        setContent {
            RunTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "analytics_dashboard") {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(onBackClick = { finish() })
                    }
                }
            }
        }
    }
}