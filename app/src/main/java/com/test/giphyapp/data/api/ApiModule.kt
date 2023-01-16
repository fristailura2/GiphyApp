package com.test.giphyapp.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.test.giphyapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,gson: Gson,enumConverterFactory: EnumConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .addConverterFactory(enumConverterFactory)
            .build()
    }
    @Provides
    @Singleton
    fun provideEnumConverterFactory():EnumConverterFactory{
        return EnumConverterFactory()
    }
    @Provides
    @Singleton
    fun provideOkHttp():OkHttpClient{
        return OkHttpClient.Builder()
            .connectTimeout(20L,TimeUnit.SECONDS)
            .readTimeout(20L,TimeUnit.SECONDS)
            .writeTimeout(20L,TimeUnit.SECONDS)
            .build()
    }
    @Provides
    @Singleton
    fun provideGson():Gson{
        return GsonBuilder()
            .setPrettyPrinting()
            .create()
    }
    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): GiphyPublicApi {
        return retrofit.create(GiphyPublicApi::class.java)
    }
}