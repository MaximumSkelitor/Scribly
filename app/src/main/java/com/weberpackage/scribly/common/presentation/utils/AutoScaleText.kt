package com.weberpackage.scribly.common.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoScaleText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null,
    minFontSize: TextUnit = 12.sp,
    scaleFactor: Float = 0.9f
) {
    // Key on both text and style so changes to either reset the size
    var resizedTextStyle by remember(text, style) { mutableStateOf(style) }
    var readyToDraw by remember(text, style) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                val currentSize = resizedTextStyle.fontSize
                if (currentSize.isSp && currentSize.value > minFontSize.value) {
                    // Scale down
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = (currentSize.value * scaleFactor).sp
                    )
                } else {
                    // We hit the minimum size, draw anyway
                    readyToDraw = true
                }
            } else {
                // Text fits, we are safe to draw
                readyToDraw = true
            }
        },
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}
