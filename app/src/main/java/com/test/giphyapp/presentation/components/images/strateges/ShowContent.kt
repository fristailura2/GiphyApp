package com.test.giphyapp.presentation.components.images.strateges

import androidx.compose.runtime.Composable
import coil.request.ImageRequest


class ShowContent(private val content: @Composable () -> Unit) : PlaceholderAsyncImageOnErrorStrategy() {
    override fun getRequest(error:Boolean, model: ImageRequest): ImageRequest {
        return model
    }

    @Composable
    override fun ApplyError(
        error: Boolean,
        model: ImageRequest,
        ImageView:@Composable (ImageRequest) -> Unit
    ) {
        if(error)
            content()
        else
            ImageViewInternal(error,model,ImageView)
    }


}