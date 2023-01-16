package com.test.giphyapp.data.images

import android.util.Base64

fun String.getCacheKeyFromUrl(type: ImageCache.ImageType): String {
    if (type == ImageCache.ImageType.ANY)
        throw IllegalArgumentException("type shouldn't be ANY")

    return Base64.encodeToString(("$this${type.ordinal}").encodeToByteArray(), Base64.DEFAULT)
}