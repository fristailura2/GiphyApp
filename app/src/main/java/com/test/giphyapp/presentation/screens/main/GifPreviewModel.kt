package com.test.giphyapp.presentation.screens.main

data class GifPreviewModel(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String,
    val key: String,
) {
    val aspectRatio = width.toFloat() / height
}