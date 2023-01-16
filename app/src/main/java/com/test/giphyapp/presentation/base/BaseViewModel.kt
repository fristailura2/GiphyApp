package com.test.giphyapp.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<ST : State, EV : Event>(initialState: ST) : ViewModel() {
    private val _state: MutableStateFlow<ST> = MutableStateFlow(initialState)
    protected val internalState: StateFlow<ST>
        get() = _state
    abstract val state: Flow<ST>
    abstract fun onEvent(event: EV)

    protected val currentState
        get() = _state.value

    protected fun setState(state: ST) {
        _state.value = state
    }
}

abstract class NavigationViewModel<ST : State, EV : Event, DST : NavigationRoute>(initialState: ST) :
    BaseViewModel<ST, EV>(initialState) {
    val navigation: SharedFlow<DST>
        get() = _navigation
    private val _navigation: MutableSharedFlow<DST> = MutableSharedFlow()
    protected fun navigate(destination: DST) {
        viewModelScope.launch {
            _navigation.emit(destination)
        }
    }
}


@Composable
fun <T : NavigationRoute> NavigationViewModel<*, *, T>.SetUpNavigation(
    lifecycleOwner: LifecycleOwner,
    navigationLogic: suspend (T) -> Unit
) {
    remember {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigation.onEach {
                    navigationLogic(it)
                }.collect()
            }
        }
    }
}


interface State

interface Event

interface NavigationRoute