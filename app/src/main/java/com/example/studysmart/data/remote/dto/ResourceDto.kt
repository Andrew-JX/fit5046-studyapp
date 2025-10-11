// data/remote/dto/SearchResponseDto.kt
package com.example.studysmart.data.remote.dto

data class SearchResponseDto(val docs: List<DocDto>)
data class DocDto(val title: String?, val author_name: List<String>?, val cover_i: Int?)