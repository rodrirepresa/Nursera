package com.rodrirepresa.nursera.feature.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.feature.home.presentation.viewmodel.HomeIntent
import com.rodrirepresa.nursera.feature.home.presentation.viewmodel.HomeSideEffect
import com.rodrirepresa.nursera.feature.home.presentation.viewmodel.HomeState
import com.rodrirepresa.nursera.feature.home.presentation.viewmodel.HomeViewModel

@Composable
internal fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.execute(HomeIntent.Load)
    }

    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            consumeSideEffects(sideEffect)
        },
    ) { state ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is HomeState.Loading -> Text("Loading")
                is HomeState.Loaded -> Text("Loaded")
            }
        }
    }
}

private fun consumeSideEffects(sideEffect: HomeSideEffect) {}
