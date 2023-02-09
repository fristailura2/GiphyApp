package com.test.giphyapp.presentation.screens.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.test.giphyapp.presentation.components.LoadingAnimation
import com.test.giphyapp.presentation.screens.main.GifPreviewModel
import kotlinx.coroutines.flow.*

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