package com.test.giphyapp.converters

import com.test.giphyapp.data.db.model.DbGif
import com.test.giphyapp.data.images.ImageCache
import com.test.giphyapp.data.images.getCacheKeyFromUrl
import com.test.giphyapp.presentation.screens.details.GifFullModel
import com.test.giphyapp.presentation.screens.main.GifPreviewModel

fun DbGif.toGifFullModel(): GifFullModel {
    return GifFullModel(
        id,
        mainWidth,
        mainHeight,
        mainImage,
        preview,
        preview.getCacheKeyFromUrl(ImageCache.ImageType.FULL),
        preview.getCacheKeyFromUrl(ImageCache.ImageType.PREVIEW)
    )
}

fun DbGif.toGifPreviewModel(): GifPreviewModel {
    return GifPreviewModel(
        id,
        previewWidth,
        previewHeight,
        preview,
        preview.getCacheKeyFromUrl(ImageCache.ImageType.PREVIEW)
    )
}