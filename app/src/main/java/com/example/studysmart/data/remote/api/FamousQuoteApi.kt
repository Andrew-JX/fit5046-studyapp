package com.example.studysmart.data.remote.api


import com.example.studysmart.domain.model.FamousQuote
import retrofit2.http.GET


interface FamousQuoteApi {
    @GET("random")
    suspend fun getRandomQuote(): List<FamousQuote>

}
