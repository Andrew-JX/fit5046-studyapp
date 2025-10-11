package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.FamousQuote

interface FamousQuotesRepository {
    suspend fun getQuote(): FamousQuote
}