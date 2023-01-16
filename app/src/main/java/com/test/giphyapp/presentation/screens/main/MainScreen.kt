package com.test.giphyapp.presentation.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil.request.ImageRequest
import com.test.giphyapp.presentation.DefaultPlaceholderAsyncImage
import com.test.giphyapp.presentation.LoadingAnimation
import com.test.giphyapp.presentation.base.DoWithPagingState
import kotlinx.coroutines.flow.*

@Composable
fun MainScreen(
    viewModel: MainViewModel,

    ) {
    //val state = viewModel.state.collectAsState(null).value
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GifGrid(
    modifier: Modifier = Modifier,
    minColumnSize: Dp,
    space: Dp = 10.dp,
    itemsPadding: PaddingValues = PaddingValues(
        start = 12.dp,
        top = 16.dp,
        end = 12.dp,
        bottom = 16.dp
    ),
    items: LazyPagingItems<GifPreviewModel>,
    onClick: (model: GifPreviewModel, index: Int) -> Unit,
    onRemove: (model: GifPreviewModel, index: Int) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space),
        horizontalArrangement = Arrangement.spacedBy(space),
        contentPadding = itemsPadding,
        columns = StaggeredGridCells.Adaptive(minColumnSize), content = {
            items(
                items.itemCount,
                key = {
                    items.peek(it)?.id ?: it
                },
            ) {
                val item = items[it]
                if (item != null)
                    GifPreviewElement(
                        model = item,
                        onClick = {
                            onClick(item, it)

                        },
                        onRemove = {
                            onRemove(item, it)
                        }
                    )
                else {
                    LoadingAnimation()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    internetIndicatorSize: Dp = 24.dp,
    internetIndicatorAvailableColor: Color = Color.Green,
    internetIndicatorUnAvailableColor: Color = Color.Red,
    searchText: String,
    hasInternetConnection: Boolean,
    listener: (String) -> Unit
) {
    TextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = modifier,
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(internetIndicatorSize)
                    .background(
                        color = if (hasInternetConnection)
                            internetIndicatorAvailableColor
                        else
                            internetIndicatorUnAvailableColor,
                        shape = CircleShape
                    )
            )
        },
        value = searchText,
        onValueChange = listener
    )
}

@Composable
fun GifPreviewElement(
    model: GifPreviewModel,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current
    val request = remember {
        ImageRequest.Builder(context)
            .data(model.url)
            .memoryCacheKey(model.key)
            .diskCacheKey(model.key)
            .build()
    }

    Box {
        DefaultPlaceholderAsyncImage(
            modifier = Modifier
                .aspectRatio(model.aspectRatio)
                .fillMaxSize()
                .clickable { onClick() },
            model = request
        )

        IconButton(onClick = onRemove, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(
                modifier = Modifier
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                            )
                        ), shape = CircleShape
                    )
                    .padding(4.dp),
                imageVector = Icons.Filled.Close,
                tint = Color.White,
                contentDescription = null
            )
        }
    }

}