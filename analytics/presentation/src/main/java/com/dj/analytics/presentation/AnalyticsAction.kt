package com.dj.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}