package com.test.giphyapp.presentation.screens.details

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.test.giphyapp.presentation.base.DoWithPagingState
import com.test.giphyapp.presentation.components.LoadingAnimation
import com.test.giphyapp.presentation.screens.details.components.FullGifElement
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
) {
    viewModel.DoWithPagingState({ it.page }) { state, items ->
        Box(
            modifier = Modifier.fillMaxSize(),
            content = {
                val pagerState = rememberPagerState()

                if (pagerState.currentPage != state.currentPage) {
                    LaunchedEffect(key1 = state, block = {
                        pagerState.scrollToPage(state.currentPage)
                    })
                }

                LaunchedEffect(viewModel) {
                    snapshotFlow { pagerState.currentPage }
                        .distinctUntilChanged()
                        .collect {
                            Log.d("LaunchedEffect", "paged to $it")
                            viewModel.onEvent(DetailEvent.PagedEvent(it))
                        }
                }

                VerticalPager(
                    state = pagerState,
                    count = items.itemCount,
                    userScrollEnabled = true,
                    itemSpacing = 10.dp
                ) {
                    val item = items[it]
                    if (item != null)
                        FullGifElement(
                            model = item,
                            online = state.hasInternetConnection
                        )
                    else
                        LoadingAnimation()
                }
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { viewModel.onEvent(DetailEvent.CloseEvent) }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null
                    )
                }
            })
    }
}


