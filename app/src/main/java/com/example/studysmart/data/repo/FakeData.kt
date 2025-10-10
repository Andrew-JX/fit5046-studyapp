package com.example.studysmart.data.repo

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.model.Session
import com.example.studysmart.util.Priority
import java.util.Date

// ---------------------- SUBJECTS 假数据 ----------------------
val subjects: List<Subject> = listOf(
    Subject(
        name = "Mathematics",
        goalHours = 15f,
        colors = listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC)),
        subjectId = 1
    ),
    Subject(
        name = "History",
        goalHours = 10f,
        colors = listOf(Color(0xFF1E88E5), Color(0xFF90CAF9)),
        subjectId = 2
    )
)

// ---------------------- TASKS 假数据 ----------------------
val tasks: List<Task> = listOf(
    Task(
        id = 1,
        subjectId = 1L,
        title = "Finish Algebra Homework",
        description = "Complete exercises from Chapter 3",
        dueDateMillis = Date().time + 1 * 24 * 60 * 60 * 1000L, // 明天
        priority = Priority.HIGH,
        isCompleted = false
    ),
    Task(
        id = 2,
        subjectId = 2L,
        title = "Read WWII Summary",
        description = "Prepare notes for the quiz",
        dueDateMillis = Date().time + 2 * 24 * 60 * 60 * 1000L, // 后天
        priority = Priority.MEDIUM,
        isCompleted = true
    )
)

// ---------------------- SESSIONS 假数据 ----------------------
val sessions: List<Session> = listOf(
    Session(
        sessionSubjectId = 1,
        relatedToSubject = "Mathematics",
        date = Date().time - 60 * 60 * 1000L,   // 1 小时前
        duration = 90L,                         // 分钟
        sessionId = 1
    ),
    Session(
        sessionSubjectId = 2,
        relatedToSubject = "History",
        date = Date().time - 2 * 60 * 60 * 1000L, // 2 小时前
        duration = 60L,
        sessionId = 2
    )
)
