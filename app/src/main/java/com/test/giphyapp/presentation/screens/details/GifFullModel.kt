package com.test.giphyapp.presentation.screens.details

data class GifFullModel(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String,
    val previewUrl: String,
    val key: String,
    val previewKey: String
)