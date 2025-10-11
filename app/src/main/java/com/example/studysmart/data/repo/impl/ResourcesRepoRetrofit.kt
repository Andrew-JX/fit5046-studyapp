// data/repo/impl/ResourcesRepoRetrofit.kt
package com.example.studysmart.data.repo.impl
import com.example.studysmart.data.remote.api.ResourcesService
import com.example.studysmart.data.repo.ResourcesRepo
import com.example.studysmart.domain.model.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourcesRepoRetrofit @Inject constructor(
    private val api: ResourcesService
) : ResourcesRepo {
    override suspend fun search(query: String, page: Int): List<Resource> =
        api.search(query, page).docs.map {
            Resource(
                title = it.title ?: "(Untitled)",
                url = "",
                author = it.author_name?.firstOrNull(),
                coverUrl = it.cover_i?.let { id -> "https://covers.openlibrary.org/b/id/$id-M.jpg" }
            )
        }
}