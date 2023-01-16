package com.test.giphyapp.data.api

import com.google.gson.annotations.SerializedName

data class Images(
    @SerializedName("downsized_medium")
    val downsizedMedium: Image,
    @SerializedName("fixed_width")
    val fixedWidth: Image
)