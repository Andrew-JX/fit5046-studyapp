package com.example.studysmart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.studysmart.presentation.session.SessionUi
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Color as COLOR
import androidx.compose.ui.graphics.Color
import com.example.studysmart.util.changeMillisToDateString
import java.util.Calendar


@Composable

fun GraphScreen(sessionList: StateFlow<List<SessionUi>>) {
    var selectedMode by remember { mutableStateOf("Pie Chart") }
    val modes = listOf("Pie Chart", "Bar Chart")

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {Column(

        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        Text(
            "Data Visualisation with Charts", style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            modes.forEach { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { selectedMode = mode },
                    label = {
                        Text(
                            text = mode,
                            color = if (selectedMode == mode) Color.White
                            else Color.Black
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF5A5D9D),
                        containerColor = Color(0xFFE0E0E0)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (selectedMode) {
            "Pie Chart" -> PieChartScreen(sessionList)
            "Bar Chart" -> BarChartScreen(sessionList)
        }
    }
    }
}






@Composable
fun BarChartScreen(sessionList: StateFlow<List<SessionUi>>) {
    val sessions by sessionList.collectAsState()

// Step 1: 按日期字符串分组
    val groupedSessions = sessions
        .groupBy { it.dateMillis.changeMillisToDateString() }
        .mapValues { entry ->
            entry.value.sumOf { it.durationMinutes }
        }

// Step 2: 构造 BarEntry 和 labels
    val labels = groupedSessions.keys.toList()
    val barEntries = groupedSessions.values.mapIndexed { index, totalDuration ->
        BarEntry(index.toFloat(), totalDuration.toFloat())
    }

    val barDataSet = BarDataSet(barEntries, "Studied Time").apply {
        colors = ColorTemplate.COLORFUL_COLORS.toList()
    }

    val barData = BarData(barDataSet).apply {
        barWidth = 0.4f
    }



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                BarChart(context).apply {
                    data = barData
                    description.isEnabled = false
                    setFitBars(true)
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = IndexAxisValueFormatter(labels)
                        granularity = 1f
                        labelRotationAngle = -45f
                    }
                    axisRight.isEnabled = false
                    animateY(1000)
                    invalidate()
                }
            }
        )
    }
}



@Composable
fun PieChartScreen(sessionList: StateFlow<List<SessionUi>>) {
    val sessions by sessionList.collectAsState()

    // 计算每个学科的总学习时长
    val subjectDurations = sessions
        .groupBy { it.subjectName }
        .mapValues { entry -> entry.value.sumOf { it.durationMinutes } }

    // 创建 PieEntry 列表
    val pieEntries = subjectDurations.map { (subject, duration) ->
        PieEntry(duration.toFloat(), subject)
    }

    // 为每个学科分配一致的颜色
    val subjectColorMap = mutableMapOf<String, Int>()
    val availableColors = ColorTemplate.COLORFUL_COLORS.toList()
    var colorIndex = 0
    subjectDurations.keys.forEach { subject ->
        subjectColorMap[subject] = availableColors[colorIndex % availableColors.size]
        colorIndex++
    }

    val pieDataSet = PieDataSet(pieEntries, "Learning Time by Subject")
    pieDataSet.colors = pieEntries.map { entry ->
        val label = entry.label ?: "Unknown"
        subjectColorMap[label] ?: COLOR.GRAY
    }

    pieDataSet.valueFormatter = PercentFormatter()
    pieDataSet.valueTextSize = 14f
    pieDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

    val pieData = PieData(pieDataSet)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PieChart(context).apply {
                    data = pieData
                    description.isEnabled = false
                    centerText = "Learning Time By Subjects"
                    setDrawCenterText(true)
                    setEntryLabelTextSize(14f)
                    setUsePercentValues(true)
                    animateY(1000)
                }
            }
        )
    }
}
//we used this class for formatting value (adding % sign)
class PercentValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        //you can create your own formatting style below
        return "${value.toInt()}%"
    }
}

//@Preview
//@Composable
//fun GraphScreenPreview() {
//    GraphScreen(sessionList = )
//}