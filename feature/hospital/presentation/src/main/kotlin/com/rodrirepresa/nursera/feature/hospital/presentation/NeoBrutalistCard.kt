package com.rodrirepresa.nursera.feature.hospital.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun NeoBrutalistCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    shadowColor: Color = Color(0xFF1A1A1A),
    shadowOffset: Dp = 4.dp,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) shadowOffset else 0.dp,
        label = "cardPressOffset",
    )

    Box(modifier = modifier) {
        // Shadow — fixed, always offset down-right
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .border(2.5.dp, shadowColor, RoundedCornerShape(12.dp))
                    .background(shadowColor, RoundedCornerShape(12.dp)),
        )
        // Card — moves up-left on press, giving a lift-off-shadow effect
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset(x = -animatedOffset, y = -animatedOffset)
                    .border(2.5.dp, shadowColor, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(16.dp),
        ) {
            content()
        }
    }
}
