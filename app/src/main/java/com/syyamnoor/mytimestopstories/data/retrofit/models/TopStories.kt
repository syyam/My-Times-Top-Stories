package com.syyamnoor.mytimestopstories.data.retrofit.models

import java.util.*

private var increment: Long = 0

data class TopStories(
    val copyright: String,
    val last_updated: String,
    val num_results: Int,
    val results: List<NewsItemResult>,
    val section: String,
    val status: String
)

data class NewsItemResult(
    var id: Long = ++ increment,
    val abstract: String,
    val byline: String,
    val created_date: String,
    val des_facet: List<String>,
    val geo_facet: List<String>,
    val item_type: String,
    val kicker: String,
    val material_type_facet: String,
    val multimedia: List<Multimedia>,
    val org_facet: List<String>,
    val per_facet: List<String>,
    val published_date: Date,
    val section: String,
    val short_url: String,
    val subsection: String,
    val title: String,
    val updated_date: String,
    val uri: String,
    val url: String
)

data class Multimedia(
    val caption: String,
    val copyright: String,
    val format: String,
    val height: Int,
    val subtype: String,
    val type: String,
    val url: String,
    val width: Int
)