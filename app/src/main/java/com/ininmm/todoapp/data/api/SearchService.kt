package com.ininmm.todoapp.data.api

import com.ininmm.todoapp.data.model.SearchRepo
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("search/repositories")
    suspend fun searchRepo(
        @Query("q", encoded = true) repo: String = "coil+org:coil-kt"
    ): SearchRepo
}
