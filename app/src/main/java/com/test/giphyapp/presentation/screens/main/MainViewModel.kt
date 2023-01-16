package com.test.giphyapp.presentation.screens.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.test.giphyapp.converters.toGifPreviewModel
import com.test.giphyapp.data.repository.GifRepository
import com.test.giphyapp.presentation.base.Event
import com.test.giphyapp.presentation.base.NavigationRoute
import com.test.giphyapp.presentation.base.NavigationViewModel
import com.test.giphyapp.presentation.base.State
import com.test.giphyapp.presentation.utils.NetworkStatus
import com.test.giphyapp.presentation.utils.NetworkStatusTracker
import com.test.giphyapp.utils.withPrevious
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GifRepository,
    private val networkStatusTracker: NetworkStatusTracker
) : NavigationViewModel<MainState, MainEvent, MainViewModel.Route>(MainState()) {

    init {
        viewModelScope.launch {
            networkStatusTracker.networkStatus.onEach {
                setState(currentState.copy(hasInternetConnection = it == NetworkStatus.Available))
            }.collect()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagingData: Flow<MainState> = internalState.withPrevious(currentState).filter {
        val textChanged = it.current.requestText != it.previous?.requestText
        val internetStatusChanged =
            it.current.hasInternetConnection != it.previous?.hasInternetConnection

        textChanged || internetStatusChanged
    }.debounce(500.milliseconds).map {
        it.current
    }.flatMapLatest { state ->

        val pagingFlow = if (state.requestText.isEmpty())
            flowOf(PagingData.empty())
        else
            repository.getPagingGifs(state.requestText, !state.hasInternetConnection)
                .map { it ->
                    it.map {
                        it.toGifPreviewModel()
                    }
                }

        pagingFlow.cachedIn(viewModelScope).map { currentState.copy(page = it) }
    }.flowOn(Dispatchers.IO)

    override val state: Flow<MainState> =
        merge(
            pagingData,
            internalState
        ).distinctUntilChanged().shareIn(viewModelScope, SharingStarted.Lazily, 1)

    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.InputEvent -> {
                setState(currentState.copy(requestText = event.text))
            }
            is MainEvent.SelectEvent -> {
                navigate(Route.Details(event.index, currentState.requestText))
            }
            is MainEvent.RemoveEvent -> {
                viewModelScope.launch {
                    repository.removeGif(event.item.id)
                }
            }
        }
    }

    sealed class Route : NavigationRoute {
        class Details(
            val itemIndex: Int,
            val requestText: String
        ) : Route()
    }
}

data class MainState(
    val hasInternetConnection: Boolean = true,
    val requestText: String = "",
    val page: PagingData<GifPreviewModel> = PagingData.empty()
) : State

sealed class MainEvent : Event {
    class InputEvent(val text: String) : MainEvent()
    class SelectEvent(val index: Int) : MainEvent()
    class RemoveEvent(val item: GifPreviewModel) : MainEvent()
}