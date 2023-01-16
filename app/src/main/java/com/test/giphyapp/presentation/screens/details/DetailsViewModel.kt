package com.test.giphyapp.presentation.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.test.giphyapp.converters.toGifFullModel
import com.test.giphyapp.data.repository.GifRepository
import com.test.giphyapp.presentation.NavGraph
import com.test.giphyapp.presentation.base.Event
import com.test.giphyapp.presentation.base.NavigationRoute
import com.test.giphyapp.presentation.base.NavigationViewModel
import com.test.giphyapp.presentation.base.State
import com.test.giphyapp.presentation.utils.NetworkStatus
import com.test.giphyapp.presentation.utils.NetworkStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: GifRepository,
    private val savedStateHandle: SavedStateHandle,
    private val networkStatusTracker: NetworkStatusTracker,
) : NavigationViewModel<DetailsState, DetailEvent, DetailsViewModel.Route>(DetailsState()) {

    private val startIndex: Int
        get() = NavGraph.Details.getIntArg(savedStateHandle, NavGraph.Details.indexParam)
    private val searchText: String
        get() = NavGraph.Details.getArg(savedStateHandle, NavGraph.Details.searchTextParam)

    override val state: Flow<DetailsState> =
        internalState

    init {
        viewModelScope.launch {
            setState(currentState.copy(currentPage = startIndex))
            collectGifsWithInternetState().onEach {
                setState(
                    currentState.copy(
                        hasInternetConnection = it.second,
                        page = it.first
                    )
                )
            }.collect()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectGifsWithInternetState(): Flow<Pair<PagingData<GifFullModel>, Boolean>> {
        return networkStatusTracker.networkStatus.flatMapLatest { networkState ->
            val hasInternetConnection = networkState == NetworkStatus.Available

            repository.getPagingGifs(searchText, !hasInternetConnection)
                .cachedIn(viewModelScope)
                .map { it ->
                    it.map {
                        it.toGifFullModel()
                    } to hasInternetConnection
                }
        }
    }

    override fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.CloseEvent -> navigate(Route.Back)
            is DetailEvent.PagedEvent -> setState(currentState.copy(currentPage = event.page))
        }
    }

    sealed class Route : NavigationRoute {
        object Back : Route()
    }

}

data class DetailsState(
    val currentPage: Int = 0,
    val hasInternetConnection: Boolean = true,
    val page: PagingData<GifFullModel> = PagingData.empty()
) : State

sealed class DetailEvent : Event {
    object CloseEvent : DetailEvent()
    class PagedEvent(val page: Int) : DetailEvent()
}
