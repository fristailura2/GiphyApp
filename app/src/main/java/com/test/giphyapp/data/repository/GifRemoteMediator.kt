package com.test.giphyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.test.giphyapp.BuildConfig
import com.test.giphyapp.data.api.*
import com.test.giphyapp.data.db.GifAppDb
import com.test.giphyapp.data.db.SearchPageDao
import com.test.giphyapp.data.db.SearchResultDao
import com.test.giphyapp.data.db.model.DbGif
import com.test.giphyapp.data.toDbGif
import com.test.giphyapp.data.toDbSearchPage
import com.test.giphyapp.data.toDbSearchResults
import kotlinx.coroutines.*

@OptIn(ExperimentalPagingApi::class)
class GifRemoteMediator(
    private val api: GiphyPublicApi,
    private val dataBase: GifAppDb
) : RemoteMediator<Int, DbGif>() {

    private val searchPageDao: SearchPageDao by lazy {
        dataBase.getSearchPageDao()
    }
    private val searchResultDao: SearchResultDao by lazy {
        dataBase.getSearchResultDao()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbGif>
    ): MediatorResult {

        val searchRes = searchResultDao.findFirstSearchResults()
        val query = searchRes.searchQuery

        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val currentPage = state.anchorPosition?.div(state.config.pageSize) ?: 0
                if (searchRes.totalPages == null)
                    currentPage
                else {
                    return MediatorResult.Success(endOfPaginationReached = false)
                }
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val nextPos = searchPageDao.getLastPage()?.page?.plus(1)
                    ?: return MediatorResult.Success(endOfPaginationReached = false)

                val pages = searchRes.totalPages

                if (pages != null && nextPos > pages)
                    return MediatorResult.Success(true)

                nextPos
            }
        }


        return loadData(
            query,
            state.config.pageSize,
            page,
            success = {
                dataBase.withTransaction {
                    saveToDb(
                        query,
                        state.config.pageSize,
                    )
                }
                MediatorResult.Success(endOfPaginationReached = pagination.count == 0)
            },
            error = {
                it.printStackTrace()
                MediatorResult.Error(it)
            }
        )
    }

    private suspend inline fun <T> loadData(
        query: String,
        pageSize: Int,
        page: Int,
        success: DataResponse<GifItem>.() -> T,
        error: (Throwable) -> T
    ): T {
        return try {
            val result = api.getGifs(
                BuildConfig.API_KEY,
                query,
                pageSize,
                page * pageSize,
                GifRating.G,
                "en",
                Bundle.StickerLayering
            )
            return if (result.isSuccessful)
                success(result.body()!!)
            else
                error(RequestError(result.code()))
        } catch (e: Exception) {
            error(e)
        }
    }

    private suspend fun DataResponse<GifItem>.saveToDb(
        query: String,
        pageSize: Int,
    ) {
        val searchResultsId = dataBase.getSearchResultDao().get(query)?.id!!

        dataBase.getSearchResultDao().update(
            pagination.toDbSearchResults(query, pageSize).copy(id = searchResultsId)
        )

        val page = toDbSearchPage(pageSize, searchResultsId)
        val inBasePage = dataBase.getSearchPageDao().findSearchPage(page = page.page)
        val currentPageId = if (inBasePage == null) {
            dataBase.getSearchPageDao().insert(
                toDbSearchPage(pageSize, searchResultsId)
            )
        } else {
            dataBase.getSearchPageDao().update(page.copy(id = inBasePage.id))
            inBasePage.id
        }

        dataBase.getGifDao().insertOrUpdateAll(data.map {
            it.toDbGif(currentPageId)
        })

    }
}

