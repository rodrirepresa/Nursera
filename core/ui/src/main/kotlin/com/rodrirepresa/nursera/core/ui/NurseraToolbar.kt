package com.rodrirepresa.nursera.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NurseraToolbar(
    title: String,
    onNavigateBack: () -> Unit,
    iconColor: Color = Color.White,
    modifier: Modifier = Modifier,
    applyStatusBarPadding: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (applyStatusBarPadding) Modifier.statusBarsPadding() else Modifier)
                .height(56.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        NeoBrutalistIconButton(
            onClick = onNavigateBack,
            backgroundColor = iconColor,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        var fontSize by remember { mutableStateOf(16.sp) }
        val minFontSize = 12.sp

        Text(
            text = title,
            fontSize = fontSize,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                if (result.hasVisualOverflow && fontSize > minFontSize) {
                    fontSize = (fontSize.value * 0.95f).coerceAtLeast(minFontSize.value).sp
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseraToolbarPreview() {
    NurseraToolbar(
        title = "Hospital La Paz",
        onNavigateBack = {},
        iconColor = Color.White,
    )
}
