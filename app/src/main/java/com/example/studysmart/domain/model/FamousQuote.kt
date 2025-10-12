package com.example.studysmart.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FamousQuote(
    @Json(name = "q") val content: String,
    @Json(name = "a") val author: String
)

