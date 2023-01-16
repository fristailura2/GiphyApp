package com.test.giphyapp.data.api

import com.google.gson.annotations.SerializedName

enum class Bundle {
    @SerializedName("clips_grid_picker")
    ClipsGridPicker,
    @SerializedName("sticker_layering")
    StickerLayering
}