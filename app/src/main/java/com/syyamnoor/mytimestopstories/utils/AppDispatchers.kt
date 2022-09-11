package com.syyamnoor.mytimestopstories.utils

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {
    fun main(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}