package com.dj.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dj.core.domain.run.RunRepository
import com.dj.core.domain.run.SyncRunScheduler
import com.dj.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())

    init {
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(interval = 30.minutes)
            )
        }
        runRepository.getRuns().onEach { runs ->
            val runUi = runs.map {
                it.toRunUi()
            }
            state = state.copy(runs = runUi)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            // sync
            runRepository.syncPendingRuns()
            // and the fetch
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnLogoutClick -> Unit
            RunOverviewAction.OnStartClick -> Unit
            RunOverviewAction.OnAnalyticsClick -> Unit
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
        }
    }
}