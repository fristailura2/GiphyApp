package com.test.giphyapp.presentation.components.images

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.request.ImageRequest
import com.test.giphyapp.R
import com.test.giphyapp.presentation.components.LoadingAnimation
import com.test.giphyapp.presentation.components.images.strateges.ShowContent


@Composable
fun DefaultPlaceholderAsyncImage(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    model: ImageRequest,
) {
    PlaceholderAsyncImage(modifier, contentScale, model, placeholder = {
        AnimatedVisibility(it) {
            Box(
                modifier = modifier,
            ) {
                LoadingAnimation(modifier = Modifier.align(Alignment.Center))
            }
        }
    }, errorStrategy = ShowContent() {
        Box(
            modifier = modifier,
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.not_loaded)
            )
        }
    })
}