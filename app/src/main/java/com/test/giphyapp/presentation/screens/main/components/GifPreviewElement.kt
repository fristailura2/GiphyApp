package com.test.giphyapp.presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.test.giphyapp.presentation.components.images.DefaultPlaceholderAsyncImage
import com.test.giphyapp.presentation.screens.main.GifPreviewModel

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