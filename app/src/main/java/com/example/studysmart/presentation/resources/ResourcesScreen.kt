// app/src/main/java/com/example/studysmart/presentation/resources/ResourcesScreen.kt
package com.example.studysmart.presentation.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    onNavigateBack: () -> Unit = {}
) {
    val demo = listOf(
        "Open Library: Learning How to Learn",
        "Khan Academy: Calculus Roadmap",
        "OpenStax: Physics Fundamentals"
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resources") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(demo) {
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Text(it, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
