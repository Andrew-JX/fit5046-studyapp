package com.example.studysmart.data.remote.api


import com.example.studysmart.domain.model.FamousQuote
import retrofit2.http.GET

class FamousQuoteApi {
    interface QuoteApi {
        @GET("/random")
        suspend fun getRandomQuote(): FamousQuote
    }
}