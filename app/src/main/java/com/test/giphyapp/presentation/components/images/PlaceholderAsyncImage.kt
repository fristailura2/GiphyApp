package com.test.giphyapp.presentation.components.images

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.test.giphyapp.presentation.LocalImageLoader
import com.test.giphyapp.presentation.components.images.strateges.PlaceholderAsyncImageOnErrorStrategy

@Composable
fun PlaceholderAsyncImage(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    model: ImageRequest,
    placeholder: @Composable (isLoading: Boolean) -> Unit,
    errorStrategy: PlaceholderAsyncImageOnErrorStrategy
) {
    Box {
        val state: MutableState<AsyncImagePainter.State> = remember {
            mutableStateOf(AsyncImagePainter.State.Empty)
        }

        if(state.value is AsyncImagePainter.State.Loading)
            placeholder(true)

        errorStrategy.ApplyError(
            error = state.value is AsyncImagePainter.State.Error,
            model = model
        ) {
            AsyncImage(
                contentScale = contentScale,
                onState = {
                    state.value = it
                },
                model = it,
                imageLoader = LocalImageLoader.current,
                modifier = modifier,
                contentDescription = null
            )
        }
    }
}