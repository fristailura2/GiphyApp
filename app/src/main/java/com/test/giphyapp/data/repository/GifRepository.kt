package com.test.giphyapp.data.repository

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.test.giphyapp.data.db.GifAppDb
import com.test.giphyapp.data.db.GifDao
import com.test.giphyapp.data.db.model.DbGif
import com.test.giphyapp.data.db.model.DbSearchResults
import com.test.giphyapp.data.images.ImageCache
import kotlinx.coroutines.flow.*

class GifRepository(
    private val database: GifAppDb,
    private val imageCache: ImageCache,
    private val remoteMediator: GifRemoteMediator,
) {

    private val gifDao: GifDao by lazy { database.getGifDao() }
    private val searchResultDao by lazy { database.getSearchResultDao() }

    @OptIn(ExperimentalPagingApi::class)
    fun getPagingGifs(
        searchText: String,
        useCached: Boolean,
    ): Flow<PagingData<DbGif>> {
        return Pager(
            config = PagingConfig(
                pageSize = MAX_RESULTS,
                enablePlaceholders = true,
                initialLoadSize = INITIAL_LOAD,
                prefetchDistance = PREFETCH,
            ),
            pagingSourceFactory = {
                if (useCached)
                    gifDao.getGifsLocal("%$searchText%")
                else
                    gifDao.getGifs(searchText)
            },
            remoteMediator = if (useCached)
                null
            else
                remoteMediator

        ).flow
            .onEach {
                Log.d("MainViewModel2", "$it")
            }
            .mapCaching(useCached)
            .updateSearchOnStart(searchText, useCached)
    }


    private fun Flow<PagingData<DbGif>>.updateSearchOnStart(
        searchText: String,
        useCached: Boolean
    ): Flow<PagingData<DbGif>> {
        return if (useCached)
            this
        else {
            onStart {
                database.withTransaction {
                    val res = searchResultDao.get(searchText)
                    if (res == null) {
                        searchResultDao.deleteAll()
                        searchResultDao.insert(DbSearchResults(searchQuery = searchText))
                    }
                }
            }
        }
    }

    private fun Flow<PagingData<DbGif>>.mapCaching(useCached: Boolean): Flow<PagingData<DbGif>> {
        return map { it ->
            if (useCached)
                it.filter {
                    imageCache.isInCache(
                        it.preview,
                        type = ImageCache.ImageType.PREVIEW
                    )
                }
            else {
                it
            }
        }
    }

    suspend fun removeGif(gifId: String) {
        database.withTransaction {
            val gifToRemove = gifDao.getGif(gifId)
            gifDao.update(gifToRemove.copy(isDeleted = true))
            gifToRemove
        }.let {
            imageCache.removeFromCache(it.mainImage)
            imageCache.removeFromCache(it.preview)
        }
    }

    companion object {
        private const val PREFETCH_COEF = 0.7f
        private const val MAX_RESULTS = 25
        private const val INITIAL_LOAD = MAX_RESULTS * 2
        private const val PREFETCH = (MAX_RESULTS * PREFETCH_COEF).toInt()
    }

}