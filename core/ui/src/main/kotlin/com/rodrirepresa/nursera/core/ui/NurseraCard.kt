package com.rodrirepresa.nursera.core.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NurseraCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    shadowOffset: Dp = 4.dp,
    backgroundColor: Color,
    forcePressed: Boolean = false,
    content: @Composable () -> Unit,
) {
    val shadowColor = backgroundColor.darken()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed || forcePressed) shadowOffset else 0.dp,
        label = "cardPressOffset",
    )

    // padding(all) instead of just (end, bottom) so the card can move into top/start space
    // without exceeding the item's measured bounds (which animateItem clips to)
    Box(modifier = modifier.padding(all = shadowOffset)) {
        // Shadow — fixed, always offset down-right
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, RoundedCornerShape(12.dp)),
        )
        // Card — moves up-left on press, giving a lift-off-shadow effect
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset(x = -animatedOffset, y = -animatedOffset)
                    .border(2.5.dp, shadowColor, RoundedCornerShape(12.dp))
                    .background(backgroundColor, RoundedCornerShape(12.dp))
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

@Preview(showBackground = true)
@Composable
private fun NurseraCardPreview() {
    NurseraCard(backgroundColor = Color(0xFFDAF5F0)) {
        Text("Morning shift", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseraCardPressedPreview() {
    NurseraCard(backgroundColor = Color(0xFFDAF5F0), forcePressed = true) {
        Text("Morning shift", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseraCardCustomColorPreview() {
    NurseraCard(backgroundColor = Color(0xFFFF6B6B)) {
        Text("Selected shift", style = MaterialTheme.typography.bodyMedium)
    }
}
