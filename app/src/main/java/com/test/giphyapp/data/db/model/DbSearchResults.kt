package com.test.giphyapp.data.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "searchResults", indices = [
    Index(value = ["searchQuery"], unique = true),
])
data class DbSearchResults(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val searchQuery: String,
    val totalPages: Int? = null
)