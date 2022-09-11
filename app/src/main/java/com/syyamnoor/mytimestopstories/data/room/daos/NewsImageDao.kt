package com.syyamnoor.mytimestopstories.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb

@Dao
abstract class NewsImageDao : BaseDao<NewsImageInDb>() {

    @Query("DELETE FROM news_image")
    abstract fun deleteAllNewsImages()

}