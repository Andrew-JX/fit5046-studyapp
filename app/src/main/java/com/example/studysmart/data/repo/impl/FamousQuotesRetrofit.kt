package com.example.studysmart.data.repo.impl


import com.example.studysmart.data.remote.api.FamousQuoteApi
import com.example.studysmart.data.repo.FamousQuotesRepository
import com.example.studysmart.domain.model.FamousQuote
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamousQuotesRetrofit @Inject constructor(
    private val api: FamousQuoteApi
) : FamousQuotesRepository{
    override suspend fun getQuote(): FamousQuote {
        return api.getRandomQuote()
    }
}