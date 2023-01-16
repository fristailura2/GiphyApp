package com.test.giphyapp.data.db

import androidx.room.*
import com.test.giphyapp.data.db.model.DbSearchPage

@Dao
interface SearchPageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DbSearchPage): Long

    @Update
    suspend fun update(item: DbSearchPage)

    @Query("Select * From searchPage Where page=:page Limit 1")
    suspend fun findSearchPage(page: Int): DbSearchPage?

    @Query("Select * From searchPage Order by page Desc Limit 1")
    suspend fun getLastPage(): DbSearchPage?

}