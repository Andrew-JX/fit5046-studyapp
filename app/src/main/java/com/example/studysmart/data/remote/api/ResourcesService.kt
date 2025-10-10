// data/remote/api/ResourcesService.kt
package com.example.studysmart.data.remote.api

import com.example.studysmart.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query


interface ResourcesService {
    @GET("search.json")
    suspend fun search(@Query("q") query: String, @Query("page") page: Int = 1): SearchResponseDto
}
