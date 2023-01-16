package com.test.giphyapp.data.images

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ImageModule {
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        val diskCache = DiskCache.Builder()
            .maxSizePercent(0.8)
            .directory(context.cacheDir.apply { mkdirs() }.resolve("image_cache"))
            .build()
        val memoryCache = MemoryCache.Builder(context)
            .maxSizePercent(0.4)
            .build()
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .respectCacheHeaders(false)
            .memoryCache(memoryCache)
            .diskCache(diskCache)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideImageCache(
        imageLoader: ImageLoader,
    ): ImageCache {
        return ImageCache(
            imageLoader,
        )
    }


}
