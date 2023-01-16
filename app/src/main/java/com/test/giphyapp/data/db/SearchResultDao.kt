package com.test.giphyapp.data.db


import androidx.room.*
import com.test.giphyapp.data.db.model.DbSearchResults

@Dao
interface SearchResultDao {
    @Query("Select * From searchResults Limit 1")
    suspend fun findFirstSearchResults(): DbSearchResults

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(searchResults: DbSearchResults): Long

    @Update
    suspend fun update(searchResults: DbSearchResults)

    @Query("Select * From searchResults Where searchQuery=:searchQuery Limit 1")
    suspend fun get(searchQuery: String): DbSearchResults?

    @Query("Delete From searchResults")
    suspend fun deleteAll()
}