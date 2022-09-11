package com.syyamnoor.mytimestopstories.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.syyamnoor.mytimestopstories.data.room.joins.NewsInDbWithNewsImagesInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NewsDao : BaseDao<NewsInDb>() {

    @Transaction
    @Query("SELECT * FROM news")
    abstract fun getAllNews(): Flow<List<NewsInDbWithNewsImagesInDb>>

    @Transaction
    @Query(
        "SELECT * FROM news WHERE title LIKE :query OR news_abstract LIKE :query OR category LIKE :query OR " + " author LIKE :query"
    )
    abstract fun getAllNewsWithQuery(
        query: String,
    ): Flow<List<NewsInDbWithNewsImagesInDb>>

    @Query("DELETE FROM news")
    abstract fun deleteAllNews()

    @Transaction
    @Query("SELECT * FROM news WHERE id=:id")
    abstract fun getNewsById(id: Long): Flow<NewsInDbWithNewsImagesInDb?>

}