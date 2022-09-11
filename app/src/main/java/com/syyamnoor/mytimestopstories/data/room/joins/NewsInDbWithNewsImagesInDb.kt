package com.syyamnoor.mytimestopstories.data.room.joins

import androidx.room.Embedded
import androidx.room.Relation
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb

data class NewsInDbWithNewsImagesInDb(

    @Embedded
    val newsInDb: NewsInDb,
    @Relation(parentColumn = "id", entityColumn = "news_id")
    val newsImageInDb: List<NewsImageInDb>

)