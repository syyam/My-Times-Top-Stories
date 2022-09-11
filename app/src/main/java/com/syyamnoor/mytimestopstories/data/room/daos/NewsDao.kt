package com.syyamnoor.mytimestopstories.data.room.daos

import androidx.room.Dao
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb

@Dao
abstract class NewsDao : BaseDao<NewsInDb>() {

}