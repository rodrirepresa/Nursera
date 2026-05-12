package com.rodrirepresa.nursera.core.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NeoBrutalistIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shadowColor: Color = Color(0xFF1A1A1A),
    shadowOffset: Dp = 4.dp,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) shadowOffset else 0.dp,
        label = "iconButtonPressOffset",
    )

    Box(
        modifier =
            modifier
                .wrapContentSize()
                .padding(all = shadowOffset),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, RoundedCornerShape(12.dp)),
        )
        Box(
            modifier =
                Modifier
                    .wrapContentSize()
                    .offset(x = -animatedOffset, y = -animatedOffset)
                    .border(2.5.dp, shadowColor, RoundedCornerShape(12.dp))
                    .background(backgroundColor, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(12.dp),
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistIconButtonPreview() {
    NeoBrutalistIconButton(onClick = {}) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistIconButtonPressedPreview() {
    NeoBrutalistIconButton(onClick = {}, shadowOffset = 3.dp, backgroundColor = Color(0xFFFF6B6B)) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
    }
}
