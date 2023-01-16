package com.test.giphyapp.data.images

import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi


class ImageCache(
    private val imageLoader: ImageLoader,
) {

    @OptIn(ExperimentalCoilApi::class)
    fun removeFromCache(imageUrl:String,type: ImageType=ImageType.ANY) {
        if(type==ImageType.ANY) {
            removeFromCache(imageUrl, ImageType.PREVIEW)
            removeFromCache(imageUrl, ImageType.FULL)
        }else
            imageLoader.diskCache?.remove(imageUrl.getCacheKeyFromUrl(type))
    }

    @OptIn(ExperimentalCoilApi::class)
    fun isInCache(imageUrl:String, type: ImageType=ImageType.ANY): Boolean {
        return if(type==ImageType.ANY)
            isInCache(imageUrl,ImageType.PREVIEW)||
                    isInCache(imageUrl,ImageType.FULL)
        else
            imageLoader.diskCache?.get(imageUrl.getCacheKeyFromUrl(type)) != null
    }

    enum class ImageType{
        FULL,PREVIEW,ANY
    }
}