package com.test.giphyapp.presentation.screens.details.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.test.giphyapp.R
import com.test.giphyapp.presentation.components.LoadingAnimation
import com.test.giphyapp.presentation.components.images.PlaceholderAsyncImage
import com.test.giphyapp.presentation.components.images.strateges.RequestAgain
import com.test.giphyapp.presentation.components.images.strateges.ShowContent
import com.test.giphyapp.presentation.screens.details.GifFullModel

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
        }, errorStrategy = if (online) ShowContent {
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
        } else RequestAgain(
            offlineRequest
        )
    )
}
