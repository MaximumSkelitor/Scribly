package com.weberpackage.scribly.statistics.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.add_edit.presentation.model.getCategoryIcon
import com.weberpackage.scribly.common.presentation.components.ScriblyCard
import com.weberpackage.scribly.common.presentation.components.SectionHeader
import com.weberpackage.scribly.statistics.presentation.component.DonutChart
import com.weberpackage.scribly.statistics.presentation.component.DonutChartData
import com.weberpackage.scribly.statistics.presentation.viewmodel.StatisticsViewModel
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    contentPadding: PaddingValues
) {
    val state = viewModel.viewState.value

    val categoryBreakdown = state.subscriptions
        .groupBy { it.category }
        .mapValues { it.value.sumOf { s -> s.price } }

    val chartColors = listOf(
        Color(0xFF5D5FEF), Color(0xFF9E7AFF), Color(0xFF56CCF2),
        Color(0xFFEB5757), Color(0xFFF2994A), Color(0xFF27AE60)
    )

    val chartData = categoryBreakdown.entries.mapIndexed { index, entry ->
        DonutChartData(
            amount = entry.value,
            color = chartColors[index % chartColors.size],
            label = entry.key
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DonutChart(
                    data = chartData,
                    totalLabel = stringResource(R.string.monthly_total),
                    totalValue = stringResource(R.string.price_format, String.format(Locale.US, "%.2f", state.monthlyTotal))
                )
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.spend_insights))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InsightBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.most_expensive),
                    value = state.subscriptions.maxByOrNull { it.price }?.name ?: stringResource(R.string.none),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconColor = Color(0xFFEB5757)
                )
                InsightBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.top_category),
                    value = categoryBreakdown.maxByOrNull { it.value }?.key ?: stringResource(R.string.none),
                    icon = Icons.Default.Info,
                    iconColor = Color(0xFF5D5FEF)
                )
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.category_breakdown))
            CategoryBreakdownList(categoryBreakdown, chartData)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun InsightBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    ScriblyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun CategoryBreakdownList(
    breakdown: Map<String, Double>,
    chartData: List<DonutChartData>
) {
    val total = breakdown.values.sum()
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        breakdown.entries.forEachIndexed { index, entry ->
            val color = chartData.getOrNull(index)?.color ?: Color.Gray
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, MaterialTheme.shapes.small)
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = getCategoryIcon(entry.key),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(entry.key, style = MaterialTheme.typography.bodyMedium)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.price_format, String.format(Locale.US, "%.2f", entry.value)),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.percentage_format, ((entry.value / (if (total > 0) total else 1.0)) * 100).toInt()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { (entry.value / (if (total > 0) total else 1.0)).toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                color = color,
                strokeCap = StrokeCap.Round
            )
        }
    }
}
