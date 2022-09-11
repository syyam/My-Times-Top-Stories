package com.syyamnoor.mytimestopstories.domain.models

import java.util.*

data class News(
    val id: Long,
    val title: String,
    val section: String,
    val newsAbstract: String,
    val publishDate: Date,
    val category: String,
    val author: String,
    val url: String,
    val images: List<NewsImage>
)