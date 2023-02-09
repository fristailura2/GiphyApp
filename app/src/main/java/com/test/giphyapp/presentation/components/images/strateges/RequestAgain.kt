package com.test.giphyapp.presentation.components.images.strateges

import androidx.compose.runtime.Composable
import coil.request.ImageRequest


class RequestAgain(
    private val errorModel: ImageRequest,
    private val maxRepeat: Int = 1,
    private val repeatDelayMiles: Long = 0L
) : PlaceholderAsyncImageOnErrorStrategy() {
    private var repeat = 0
    override fun getRequest(error:Boolean, model: ImageRequest): ImageRequest {
        return if (error) {
            repeatIfLimitNotReached(model)
        } else {
            clear(model)
        }
    }

    @Composable
    override fun ApplyError(
        error: Boolean,
        model: ImageRequest,
        ImageView:@Composable (ImageRequest) -> Unit
    ) {
        ImageViewInternal(error,model,ImageView)
    }

    private fun clear(model: ImageRequest):ImageRequest{
        repeat = 0
        return model
    }
    private fun repeatIfLimitNotReached(model: ImageRequest): ImageRequest{
        return if (repeat < maxRepeat) {
            repeat++
            model
        } else {
            errorModel
        }
    }
}