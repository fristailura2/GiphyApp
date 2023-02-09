package com.test.giphyapp.presentation.components.images.strateges

import androidx.compose.runtime.Composable
import coil.request.ImageRequest


abstract class PlaceholderAsyncImageOnErrorStrategy {

    protected abstract fun getRequest(error:Boolean, model: ImageRequest): ImageRequest
    @Composable
    abstract fun ApplyError(error:Boolean, model: ImageRequest,ImageView:@Composable (ImageRequest)->Unit)

    @Composable
    protected fun ImageViewInternal(error: Boolean, model: ImageRequest,ImageView:@Composable (ImageRequest) -> Unit){
        ImageView(getRequest(error,model))
    }
}