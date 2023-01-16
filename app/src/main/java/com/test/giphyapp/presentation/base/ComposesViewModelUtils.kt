package com.test.giphyapp.presentation.base

import androidx.compose.runtime.*
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.map

@Composable
fun <ST : State, EV : Event, R : Any> BaseViewModel<ST, EV>.DoWithPagingState(
    mapper: (ST) -> PagingData<R>,
    action: @Composable (ST, LazyPagingItems<R>) -> Unit
) {
    val composeState = state.collectAsState(initial = null)
    val items = remember {
        state.map {
            mapper(it)
        }
    }

    composeState.value?.let {
        action(it, items.collectAsLazyPagingItems())
    }

}