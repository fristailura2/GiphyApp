package com.test.giphyapp.data.api

data class DataResponse<T>(
    val data: List<T>,
    val pagination:Pagination
)