package com.example.studysmart.domain.model

enum class ResourceType { VIDEO, ARTICLE, COURSE, BOOK, WEBSITE, OTHER }

data class Resource(
    val id: Long? = null,
    val title: String,
    val url: String,
    val type: ResourceType = ResourceType.OTHER,
    val subjectId: Long? = null,     // Related disciplines (can be empty)
    val provider: String? = null,    // Platform/Source (YouTube, Coursera…)
    val note: String? = null,        // Remark
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
