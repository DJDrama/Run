@file:OptIn(ExperimentalMaterial3Api::class)

package com.dj.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dj.core.presentation.designsystem.AnalyticsIcon
import com.dj.core.presentation.designsystem.LogoIcon
import com.dj.core.presentation.designsystem.LogoutIcon
import com.dj.core.presentation.designsystem.RunIcon
import com.dj.core.presentation.designsystem.RunTheme
import com.dj.core.presentation.designsystem.components.RunrunFloatingActionButton
import com.dj.core.presentation.designsystem.components.RunrunScaffold
import com.dj.core.presentation.designsystem.components.RunrunToolbar
import com.dj.core.presentation.designsystem.components.util.DropDownItem
import com.dj.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen() {

    }
}

@Composable
private fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    RunrunScaffold(
        topAppBar = {
            RunrunToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runrun),
                scrollBehavior = scrollBehavior,
                startContent = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                menuItems = listOf(
                    DropDownItem(
                        icon = AnalyticsIcon,
                        title = stringResource(id = R.string.analytics)
                    ),
                    DropDownItem(
                        icon = LogoutIcon,
                        title = stringResource(id = R.string.logout)
                    ),
                ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }
                }
            )
        },
        floatingActionButton = {
            RunrunFloatingActionButton(icon = RunIcon) {
                onAction(RunOverviewAction.OnStartClick)
            }
        }
    ) { padding ->

    }
}

@Composable
@Preview
private fun RunOverviewScreenPreview(
) {
    RunTheme {
        RunOverviewScreen() {

        }
    }
}