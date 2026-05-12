package com.rodrirepresa.nursera.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NeoBrutalistChip(
    modifier: Modifier = Modifier,
    shadowColor: Color = Color(0xFF1A1A1A),
    backgroundColor: Color,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .border(1.dp, shadowColor, RoundedCornerShape(8.dp))
                    .background(shadowColor, RoundedCornerShape(8.dp)),
        )
        Box(
            modifier =
                Modifier
                    .wrapContentSize()
                    .border(1.dp, shadowColor, RoundedCornerShape(8.dp))
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 5.dp),
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistChipPreview() {
    NeoBrutalistChip(backgroundColor = Color(0xFFDAF5F0)) {
        Text("Mañana", style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistChipAccentPreview() {
    NeoBrutalistChip(backgroundColor = Color(0xFFFF6B6B)) {
        Text("Noche", style = MaterialTheme.typography.labelSmall)
    }
}
