// app/src/main/java/com/example/studysmart/presentation/dashboard/DashboardEvent.kt
package com.example.studysmart.presentation.dashboard

sealed interface DashboardEvent {
    data class SubjectSaved(val id: Long) : DashboardEvent
    data class SessionDeleted(val id: Long) : DashboardEvent
    data class Error(val message: String) : DashboardEvent
}
