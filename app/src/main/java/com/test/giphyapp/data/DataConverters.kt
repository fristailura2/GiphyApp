package com.test.giphyapp.data

import com.test.giphyapp.data.api.DataResponse
import com.test.giphyapp.data.api.GifItem
import com.test.giphyapp.data.api.Pagination
import com.test.giphyapp.data.db.model.DbGif
import com.test.giphyapp.data.db.model.DbSearchPage
import com.test.giphyapp.data.db.model.DbSearchResults
import kotlin.math.ceil

fun GifItem.toDbGif(pageId: Long): DbGif {
    return DbGif(
        id = id,
        mainImage = images.downsizedMedium.url,
        mainHeight = images.downsizedMedium.height.toInt(),
        mainWidth = images.downsizedMedium.width.toInt(),
        preview = images.fixedWidth.url,
        previewHeight = images.fixedWidth.height.toInt(),
        previewWidth = images.fixedWidth.width.toInt(),
        title = title,
        pageId = pageId
    )
}

fun DataResponse<GifItem>.toDbSearchPage(pageSize: Int, searchResultsId: Long): DbSearchPage {
    return DbSearchPage(
        searchResultsId = searchResultsId,
        page = pagination.offset / pageSize
    )
}

fun Pagination.toDbSearchResults(searchQuery: String, pageSize: Int): DbSearchResults {
    return DbSearchResults(
        searchQuery = searchQuery,
        totalPages = calcPages(pageSize)
    )
}

internal fun Pagination.calcPages(pageSize: Int): Int {
    return ceil(totalCount.toFloat() / pageSize).toInt()
}