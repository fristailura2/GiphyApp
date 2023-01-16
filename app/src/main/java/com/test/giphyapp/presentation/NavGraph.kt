package com.test.giphyapp.presentation

import androidx.navigation.NavType
import com.test.giphyapp.presentation.base.NavigationEntity
import com.test.giphyapp.presentation.base.NavigationParam

object NavGraph {
    object Main: NavigationEntity(
        name = "main"
    ) {
        override val params: List<NavigationParam<*>> = listOf()
    }

    object Details: NavigationEntity(
        "details"
    ) {
        val indexParam = NavigationParam(
            "index",
            false,
            NavType.IntType
        )
        val searchTextParam = NavigationParam(
            "searchText",
            false,
            NavType.StringType
        )
        override val params = listOf(indexParam, searchTextParam)
    }
}