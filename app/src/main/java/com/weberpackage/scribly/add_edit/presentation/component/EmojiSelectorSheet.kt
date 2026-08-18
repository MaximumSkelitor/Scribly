package com.weberpackage.scribly.add_edit.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberpackage.scribly.R

val popularEmojis = listOf(
    // Finance & Cards
    "💳", "💰", "💸", "🏦", "📈", "📉", "🪙", "💎",
    // Media & Tech
    "🎬", "🎵", "🎧", "📺", "🎮", "🕹️", "📱", "💻", "⌚", "📷", "📽️", "🎞️",
    // Productivity & Office
    "🎨", "🖌️", "🖋️", "📝", "📚", "📖", "📰", "💡", "🛠️", "💼", "🏢", "📧",
    // Cloud & Network
    "☁️", "🌐", "📡", "⚡", "🔒", "🔑", "🛡️", "🔗",
    // Lifestyle & Home
    "🏠", "🛋️", "☕", "🍕", "🍔", "🍿", "🍎", "🍷", "🍳", "🧺", "🧼", "🪴",
    // Health & Sport
    "🏋️", "🏃", "🧘", "🧘‍♂️", "💊", "🩹", "🏥", "🦷", "⚽", "🏀", "🎾", "⛳",
    // Travel & Transport
    "🚗", "🚲", "✈️", "🚀", "🗺️", "⛽", "🎟️", "🎫",
    // Other
    "🎁", "🎉", "🔥", "📅", "🔔", "📣", "📦", "🐾"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiSelectorSheet(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.select_an_emoji),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(max = 400.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(popularEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clickable {
                                onEmojiSelected(emoji)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}
