package com.test.giphyapp.data.api

import com.google.gson.annotations.SerializedName

enum class GifRating {
    @SerializedName("g")
    G,
    @SerializedName("pg")
    PG,
    @SerializedName("pg-13")
    PG13,
    @SerializedName("r")
    R
}