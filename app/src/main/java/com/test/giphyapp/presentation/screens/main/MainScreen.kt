package com.test.giphyapp.presentation.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.giphyapp.presentation.base.DoWithPagingState
import com.test.giphyapp.presentation.screens.main.components.GifGrid
import com.test.giphyapp.presentation.screens.main.components.SearchTextField

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    viewModel.DoWithPagingState({ it.page }) { state, items ->
        Column {
            SearchTextField(
                modifier = Modifier.fillMaxWidth(),
                searchText = state.requestText,
                hasInternetConnection = state.hasInternetConnection,
            ) {
                viewModel.onEvent(MainEvent.InputEvent(it))
            }
            GifGrid(
                minColumnSize = 150.dp,
                items = items,
                onClick = { _, index ->
                    viewModel.onEvent(MainEvent.SelectEvent(index))
                },
                onRemove = { model, _ ->
                    viewModel.onEvent(MainEvent.RemoveEvent(model))
                }
            )
        }
    }
}




