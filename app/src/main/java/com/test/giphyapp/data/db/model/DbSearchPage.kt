package com.test.giphyapp.data.db.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "searchPage",
    foreignKeys = [
        ForeignKey(
            entity = DbSearchResults::class,
            parentColumns = ["id"],
            childColumns = ["searchResultsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["searchResultsId"])
    ]
)
data class DbSearchPage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val searchResultsId: Long,
    val page: Int,
)