package com.syyamnoor.mytimestopstories.data.retrofit

import com.syyamnoor.mytimestopstories.data.retrofit.models.TopStories
import com.syyamnoor.mytimestopstories.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NyTimesApi {

    /**
     * @param newsType: The type of news to filter with i.e. arts, books etc.
     * @param apiKey: The apiKey
     */
    @GET("/svc/topstories/v2/{section}.json")
    suspend fun getTopStories(
        @Path("section") newsType: String = "home",
        @Query("api-key") apiKey: String = BuildConfig.NY_TIMES_API_KEY
    ): Response<TopStories?>
}