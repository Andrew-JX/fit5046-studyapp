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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


@Composable

fun GraphScreen() {
    var selectedMode by remember { mutableStateOf("Pie Chart") }
    val modes = listOf("Pie Chart", "Bar Chart")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
            "Pie Chart" -> PieChartScreen()
            "Bar Chart" -> BarChartScreen()
        }
    }
    }
}






@Composable
fun BarChartScreen() {
    val barEntries = listOf(
        BarEntry(0f, 1070f),
        BarEntry(1f, 4050f),
        BarEntry(2f, 3890f),
        BarEntry(3f, 5599f),
        BarEntry(4f, 2300f),
        BarEntry(5f, 4055f)
    )
    val barDataSet = BarDataSet(barEntries, "Steps")
    barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
    val barData = BarData(barDataSet)
    barData.barWidth = 1.0f

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
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.valueFormatter = IndexAxisValueFormatter(
                        listOf("Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat")
                    )
                    animateY(1000)
                }
            }
        )
    }
}



@Composable
fun PieChartScreen() {
    val pieEntries = listOf(
        PieEntry(35f, "Bills"),
        PieEntry(50f, "Mortgage"),
        PieEntry(5f, "Petrol"),
        PieEntry(10f, "Food")
    )
    val pieDataSet = PieDataSet(pieEntries, "Pie Data Set")
    pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
    val pieData = PieData(pieDataSet)
    pieDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    pieDataSet.valueFormatter = PercentValueFormatter()
    pieDataSet.valueTextSize = 14f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PieChart(context).apply {
                    data = pieData
                    description.isEnabled = false
                    centerText = "Expenses"
                    setDrawCenterText(true)
                    setEntryLabelTextSize(14f)
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

@Preview
@Composable
fun GraphScreenPreview() {
    GraphScreen()
}