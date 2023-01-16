package com.test.giphyapp.data.db.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "gif", foreignKeys = [
        ForeignKey(
            entity = DbSearchPage::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.SET_NULL
        )
    ], indices = [
        Index(value = ["pageId"])
    ]
)
data class DbGif(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val mainImage: String,
    val mainHeight: Int,
    val mainWidth: Int,
    val preview: String,
    val previewHeight: Int,
    val previewWidth: Int,
    val title: String,
    val pageId: Long?,
    val isDeleted: Boolean = false,
)