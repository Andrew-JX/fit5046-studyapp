package com.example.studysmart.data.mapper


import com.example.studysmart.data.local.entity.SubjectEntity
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.theme.gradient1

fun SubjectEntity.toDomain(): Subject {
    val colors = Subject.subjectCardColors.getOrNull(colorKey) ?: Subject.subjectCardColors.first()
    return Subject(
        name = this.name,
        goalHours = this.goalHours,
        colors = colors,
        subjectId = this.id
    )
}

fun Subject.toEntity(): SubjectEntity {
    val colorKey = Subject.subjectCardColors.indexOf(colors.firstOrNull() ?: gradient1)
    return SubjectEntity(
        id = this.subjectId ?: 0L, //Pass 0L to Room for Auto Generated ID
        name = this.name,
        goalHours = this.goalHours,
        colorKey = colorKey
    )
}