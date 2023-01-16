package com.test.giphyapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.giphyapp.data.db.model.DbGif
import com.test.giphyapp.data.db.model.DbSearchPage
import com.test.giphyapp.data.db.model.DbSearchResults

@Database(entities = [DbGif::class, DbSearchPage::class, DbSearchResults::class], version = 1)
abstract class GifAppDb : RoomDatabase() {
    abstract fun getGifDao(): GifDao
    abstract fun getSearchPageDao(): SearchPageDao
    abstract fun getSearchResultDao(): SearchResultDao
}