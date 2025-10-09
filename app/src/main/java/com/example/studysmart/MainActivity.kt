package com.example.studysmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.studysmart.presentation.planner.TaskCreateEditScreen
import com.example.studysmart.presentation.planner.TaskUiState

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    // 直接显示“创建任务”界面
                    TaskCreateEditScreen(
                        onDone = { ui: TaskUiState ->
                            println("✅ Save clicked: $ui")
                        },
                        onCancel = {
                            println("❌ Cancel clicked")
                        }
                    )
                }
            }
        }
    }
}
