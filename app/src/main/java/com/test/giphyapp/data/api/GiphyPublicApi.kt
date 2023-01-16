package com.test.giphyapp.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyPublicApi {
    @GET("/v1/gifs/search")
    suspend fun getGifs(
        @Query("api_key") key: String,
        @Query("q") query: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("rating") rating: GifRating? = null,
        @Query("lang") language: String? = null,
        @Query("bundle") bundle: Bundle? = null
    ): Response<DataResponse<GifItem>>
}