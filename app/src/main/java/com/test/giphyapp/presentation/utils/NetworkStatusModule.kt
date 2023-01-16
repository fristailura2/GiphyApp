package com.test.giphyapp.presentation.utils

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkStatusModule {

    @Provides
    @Singleton
    fun provideNetworkStatusTracker(@ApplicationContext context: Context):NetworkStatusTracker{
        return NetworkStatusTrackerImpl(context)
    }
}