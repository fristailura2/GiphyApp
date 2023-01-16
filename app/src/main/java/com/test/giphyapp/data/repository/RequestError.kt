package com.test.giphyapp.data.repository

class RequestError(
    val code: Int
) : Throwable("Server return request code $code")