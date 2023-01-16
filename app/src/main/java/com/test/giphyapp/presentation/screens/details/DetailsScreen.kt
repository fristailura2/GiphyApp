package com.test.giphyapp.presentation.screens.details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.test.giphyapp.R
import com.test.giphyapp.presentation.LoadingAnimation
import com.test.giphyapp.presentation.PlaceholderAsyncImage
import com.test.giphyapp.presentation.PlaceholderAsyncImageOnErrorStrategy
import com.test.giphyapp.presentation.base.DoWithPagingState
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

@Composable
fun FullGifElement(
    online: Boolean,
    modifier: Modifier = Modifier,
    model: GifFullModel,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val request = remember {
        ImageRequest.Builder(context)
            .data(model.url)
            .placeholderMemoryCacheKey(model.previewKey)
            .diskCacheKey(model.key)
            .crossfade(true)
            .crossfade(200)
            .build()
    }

    val offlineRequest = remember {
        ImageRequest.Builder(context)
            .data(model.previewUrl)
            .networkCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.READ_ONLY)
            .memoryCachePolicy(CachePolicy.READ_ONLY)
            .diskCacheKey(model.previewKey)
            .build()
    }

    PlaceholderAsyncImage(
        Modifier
            .fillMaxSize()
            .clickable { onClick() },
        ContentScale.Fit,
        request,
        placeholder = {
            AnimatedVisibility(it) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .clickable { onClick() },
                ) {
                    LoadingAnimation(modifier = Modifier.align(Alignment.Center))
                }
            }
        }, errorStrategy = if (online) PlaceholderAsyncImageOnErrorStrategy.ShowContent {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .clickable { onClick() },
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(id = R.string.not_loaded)
                )
            }
        } else PlaceholderAsyncImageOnErrorStrategy.RequestAgain(
            offlineRequest
        )
    )

}

