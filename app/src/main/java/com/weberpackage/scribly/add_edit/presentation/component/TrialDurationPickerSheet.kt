package com.weberpackage.scribly.add_edit.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberpackage.scribly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialDurationPickerSheet(
    initialDays: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var selectedDays by remember { mutableIntStateOf(initialDays) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A1A),
        contentColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.1f)) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.free_trial_duration),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // The "Temperature" style Vertical Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF262626)),
                contentAlignment = Alignment.Center
            ) {
                // Fixed Blue Markers at center
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(16.dp, 3.dp).background(Color(0xFF5D5FEF)))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(16.dp, 3.dp).background(Color(0xFF5D5FEF)))
                }

                VerticalNumberPicker(
                    range = 1..365,
                    selectedValue = selectedDays,
                    onValueChange = { selectedDays = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder(true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.2f))
                    )
                ) {
                    Text(stringResource(R.string.cancel), color = Color.White)
                }
                
                Button(
                    onClick = { onConfirm(selectedDays) },
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5D5FEF),
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.confirm), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VerticalNumberPicker(
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    val itemHeight = 80.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedValue - 1)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Sync selected value based on the center item
    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf (selectedValue - 1)
            
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            visibleItems.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0
        }
    }

    LaunchedEffect(centerIndex) {
        onValueChange(range.first + centerIndex)
    }

    // Force snap on first layout to prevent offset
    LaunchedEffect(listState) {
        listState.scrollToItem(selectedValue - 1)
    }

    Box(modifier = Modifier.height(itemHeight * 3)) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(range.last - range.first + 1) { index ->
                val value = range.first + index
                val isSelected = index == centerIndex
                
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Using a Box with fixed width for the number to prevent "Days" from jumping
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = value.toString(),
                                style = TextStyle(
                                    fontSize = if (isSelected) 68.sp else 34.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        
                        // Days label only visible when selected, but always taking space for stability
                        Text(
                            text = stringResource(R.string.days),
                            fontSize = 18.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.6f) else Color.Transparent,
                            modifier = Modifier.padding(start = 12.dp, top = 20.dp)
                        )
                    }
                }
            }
        }
    }
}
