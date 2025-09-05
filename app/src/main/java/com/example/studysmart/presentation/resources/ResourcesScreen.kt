// app/src/main/java/com/example/studysmart/presentation/resources/ResourcesScreen.kt
package com.example.studysmart.presentation.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResourcesScreen() {
    val demo = listOf(
        "Open Library: Learning How to Learn",
        "Khan Academy: Calculus Roadmap",
        "OpenStax: Physics Fundamentals"
    )
    Column(Modifier.fillMaxSize()) {
        Text("Resources", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(demo) {
                ElevatedCard(Modifier.fillMaxWidth()) { Text(it, modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}
