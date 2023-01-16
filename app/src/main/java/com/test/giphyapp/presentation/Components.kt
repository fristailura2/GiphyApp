package com.test.giphyapp.presentation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.test.giphyapp.R
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    circleSize: Dp = 36.dp,
    animationDelay: Int = 400,
    initialAlpha: Float = 0.3f
) {
    val circles = listOf(
        remember {
            Animatable(initialValue = initialAlpha)
        },
        remember {
            Animatable(initialValue = initialAlpha)
        },
        remember {
            Animatable(initialValue = initialAlpha)
        }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = (animationDelay / circles.size).toLong() * index)

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }


    Row(
        modifier = modifier
    ) {

        circles.forEachIndexed { index, animatable ->

            if (index != 0) {
                Spacer(modifier = Modifier.width(width = 6.dp))
            }

            Box(
                modifier = Modifier
                    .size(size = circleSize)
                    .clip(shape = CircleShape)
                    .background(
                        color = circleColor
                            .copy(alpha = animatable.value)
                    )
            ) {
            }
        }
    }
}

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
        val hadError = remember {
            mutableStateOf(false)
        }

        if(errorStrategy is PlaceholderAsyncImageOnErrorStrategy.RequestAgain&&
            state.value is AsyncImagePainter.State.Error
        )
            hadError.value=true

        val request = if (
            errorStrategy is PlaceholderAsyncImageOnErrorStrategy.RequestAgain &&
            hadError.value
        ) {
            errorStrategy.errorModel
        }else
            model

        AsyncImage(
            contentScale = contentScale,
            onState = {
                state.value = it
            },
            model = request,
            imageLoader = LocalImageLoader.current,
            modifier = modifier,
            contentDescription = null
        )

        if (state.value !is AsyncImagePainter.State.Error)
            placeholder(state.value is AsyncImagePainter.State.Loading)
        else
            if (errorStrategy is PlaceholderAsyncImageOnErrorStrategy.ShowContent)
                errorStrategy.content()

    }
}


sealed class PlaceholderAsyncImageOnErrorStrategy {
    class RequestAgain(val errorModel: ImageRequest) : PlaceholderAsyncImageOnErrorStrategy()
    class ShowContent(val content: @Composable () -> Unit) : PlaceholderAsyncImageOnErrorStrategy()
}


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
    }, errorStrategy = PlaceholderAsyncImageOnErrorStrategy.ShowContent {
        Box(
            modifier = modifier,
        ) {
            Text(modifier = Modifier.align(Alignment.Center), text = stringResource(id = R.string.not_loaded))
        }
    })
}
