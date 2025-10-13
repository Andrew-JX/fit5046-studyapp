package com.example.studysmart.presentation.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.ResourceRepo
import com.example.studysmart.domain.model.Resource
import com.example.studysmart.domain.model.ResourceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResourceFilter(
    val subjectId: Long? = null,
    val type: ResourceType? = null,
    val query: String? = null
)

@HiltViewModel
class ResourceViewModel @Inject constructor(
    private val repo: ResourceRepo
) : ViewModel() {

    private val _filter = MutableStateFlow(ResourceFilter())
    val filter: StateFlow<ResourceFilter> = _filter.asStateFlow()

    val resources: StateFlow<List<Resource>> = _filter
        .flatMapLatest { f -> repo.observeResources(f.subjectId, f.type, f.query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun setSubject(subjectId: Long?) { _filter.update { it.copy(subjectId = subjectId) } }
    fun setType(type: ResourceType?) { _filter.update { it.copy(type = type) } }
    fun setQuery(q: String?) { _filter.update { it.copy(query = q) } }

    fun addResource(
        title: String,
        url: String,
        type: ResourceType,
        subjectId: Long?,
        provider: String?,
        note: String?,
        tags: List<String>
    ) = viewModelScope.launch {
        try {
            require(title.isNotBlank()) { "Title is required" }
            require(url.startsWith("http")) { "URL must start with http/https" }
            repo.upsert(
                Resource(
                    title = title.trim(),
                    url = url.trim(),
                    type = type,
                    subjectId = subjectId,
                    provider = provider?.trim(),
                    note = note?.trim(),
                    tags = tags.map { it.trim() }.filter { it.isNotEmpty() }
                )
            )
            _events.send("Saved")
        } catch (e: Exception) {
            _events.send(e.message ?: "Save failed")
        }
    }

    fun delete(id: Long) = viewModelScope.launch {
        repo.delete(id)
        _events.send("Deleted")
    }

    fun toggleFavorite(id: Long) = viewModelScope.launch {
        repo.toggleFavorite(id)
    }
}
