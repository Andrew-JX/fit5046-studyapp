package com.example.studysmart.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.SubjectRepo
import com.example.studysmart.domain.model.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SubjectEvent {
    data object Saved : SubjectEvent
    data object Deleted : SubjectEvent
    data class Error(val message: String) : SubjectEvent
}

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val repo: SubjectRepo
) : ViewModel() {


    val subjects = repo.observeSubjects()
        .map { list -> list.map { it.asUi() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _events = Channel<SubjectEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    fun upsert(
        id: Long? ,
        name: String,
        goalHours: Float,
        startColorArgb: Int,
        endColorArgb: Int
    ) = viewModelScope.launch {
        try {
            require(name.isNotBlank()) { "Subject name is required" }
            require(goalHours >= 0f) { "Goal hours must be >= 0" }
            repo.upsertSubject(
                Subject(
                    id = id ?: 0L,
                    name = name.trim(),
                    goalHours = goalHours,
                    startColorArgb = startColorArgb,
                    endColorArgb = endColorArgb
                )
            )
            _events.send(SubjectEvent.Saved)
        } catch (e: Exception) {
            _events.send(SubjectEvent.Error(e.message ?: "Save failed"))
        }
    }

    fun delete(id: Long) = viewModelScope.launch {
        try {
            repo.deleteSubject(id)
            _events.send(SubjectEvent.Deleted)
        } catch (e: Exception) {
            _events.send(SubjectEvent.Error(e.message ?: "Delete failed"))
        }
    }
}
