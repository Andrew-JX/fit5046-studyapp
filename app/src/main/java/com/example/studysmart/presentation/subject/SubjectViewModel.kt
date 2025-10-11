// app/src/main/java/com/example/studysmart/presentation/subject/SubjectViewModel.kt
package com.example.studysmart.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.data.repo.SubjectRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val repo: SubjectRepo
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeSubjects().collect { _subjects.value = it }
        }
    }

    fun upsert(s: Subject) = viewModelScope.launch { repo.upsertSubject(s) }
    fun delete(id: Long)    = viewModelScope.launch { repo.deleteSubject(id) }
}
