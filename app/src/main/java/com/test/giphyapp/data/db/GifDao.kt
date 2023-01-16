package com.test.giphyapp.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.test.giphyapp.data.db.model.DbGif

@Dao
interface GifDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<DbGif>): List<Long>

    @Transaction
    suspend fun insertOrUpdateAll(items: List<DbGif>) {
        val insertionRes = insertAll(items)
        insertionRes.mapIndexed { index, id -> items[index] to id }.filter { it.second == -1L }
            .forEach {
                val gif = getGif(it.first.id)
                val new = it.first.copy(id = gif.id, isDeleted = gif.isDeleted)
                if (gif != new)
                    update(it.first.copy(id = gif.id))
            }
    }

    @RewriteQueriesToDropUnusedColumns
    @Query("Select * From gif Join searchResults as search ON searchResultsId=search.id Join searchPage as page On page.id=gif.pageId Where search.searchQuery=:search And gif.isDeleted=0 Order By page.page")
    fun getGifs(search: String): PagingSource<Int, DbGif>

    @Query("Select * From gif Where title Like :search And gif.isDeleted=0")
    fun getGifsLocal(search: String): PagingSource<Int, DbGif>

    @Query("Select * From gif Where id == :id")
    fun getGif(id: String): DbGif

    @Update
    suspend fun update(item: DbGif)
}