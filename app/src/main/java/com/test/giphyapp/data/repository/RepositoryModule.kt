package com.test.giphyapp.data.repository

import com.test.giphyapp.data.api.GiphyPublicApi
import com.test.giphyapp.data.db.GifAppDb
import com.test.giphyapp.data.images.ImageCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideGifRepository(
        database: GifAppDb,
        imageCache: ImageCache,
        remoteMediator: GifRemoteMediator
    ): GifRepository {
        return GifRepository(
            database,
            imageCache,
            remoteMediator
        )
    }

    @Provides
    @Singleton
    fun provideGifMediator(api: GiphyPublicApi, database: GifAppDb): GifRemoteMediator {
        return GifRemoteMediator(
            api,
            database,
        )
    }
}