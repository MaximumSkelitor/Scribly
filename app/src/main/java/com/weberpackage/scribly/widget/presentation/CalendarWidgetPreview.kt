package com.weberpackage.scribly.widget.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true, widthDp = 320, heightDp = 400)
@Composable
fun CalendarWidgetPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF232533),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "August",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text("<  >", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    color = Color(0xFFACC7FF),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Weekdays
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // The Grid
            val gridLineColor = Color.White.copy(alpha = 0.1f)
            Column(modifier = Modifier.fillMaxWidth().background(gridLineColor)) {
                repeat(6) {
                    Row(modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 1.dp)) {
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(end = 1.dp)
                                    .background(Color(0xFF232533)),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "16",
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    if (it == 3) { // Mock badge
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp, vertical = 1.dp)
                                                .background(Color(0xFF5D5FEF))
                                                .fillMaxWidth()
                                                .height(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Netflix", fontSize = 6.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
