package com.test.giphyapp.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): GifAppDb {
        return Room.databaseBuilder(context, GifAppDb::class.java, "main")
            .build()
    }

    @Provides
    @Singleton
    fun provideGifDAO(db: GifAppDb): GifDao {
        return db.getGifDao()
    }

    @Provides
    @Singleton
    fun provideSearchPageDAO(db: GifAppDb): SearchPageDao {
        return db.getSearchPageDao()
    }

    @Provides
    @Singleton
    fun provideSearchResultsDAO(db: GifAppDb): SearchResultDao {
        return db.getSearchResultDao()
    }
}