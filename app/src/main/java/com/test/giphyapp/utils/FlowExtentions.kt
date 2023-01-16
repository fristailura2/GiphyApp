package com.test.giphyapp.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan


fun <T>Flow<T>.windowed(initial:T?=null,size:Int): Flow<List<T>> {

    return scan(
        if(initial==null)
            listOf()
        else
            listOf(initial)
    ){ oldItems, newItem ->
        val res=when(oldItems.size){
            0->listOf(newItem)
            in 1 until size->oldItems + listOf(newItem)
            size->oldItems.drop(1) + listOf(newItem)
            else ->throw IllegalStateException()
        }
        res
    }
}

fun <T>Flow<T>.withPrevious(initial:T): Flow<WithPreviousModel<T>> {
    return windowed(initial=initial,size = 2).map {
        WithPreviousModel(
            it.last(),
            if(it.size==1) null else it.first()
        )
    }
}

class WithPreviousModel<T>(
    val current:T,
    val previous:T?
)