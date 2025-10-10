package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.Resource

interface ResourcesRepo {
    suspend fun search(query: String, page: Int = 1): List<Resource>
}
