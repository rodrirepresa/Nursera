package com.rodrirepresa.nursera.core.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DayCardShape = RoundedCornerShape(8.dp)

/**
 * Neobrutalist card variant for fixed-size cells (e.g. calendar day cells).
 * Unlike [NurseraCard] this has no content padding and uses a smaller corner radius.
 * Shadow and border color are derived from [backgroundColor] via [darken], matching [NurseraCard].
 */
@Composable
fun NeoBrutalistDayCard(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    shadowOffset: Dp = 3.dp,
    forcePressed: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val shadowColor = backgroundColor.darken()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed || forcePressed) shadowOffset else 0.dp,
        label = "dayCardPressOffset",
    )

    Box(modifier = modifier.padding(all = shadowOffset)) {
        // Shadow — fixed, always offset down-right
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, DayCardShape),
        )
        // Card — moves up-left on press, giving a lift-off-shadow effect
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .offset(x = -animatedOffset, y = -animatedOffset)
                    .border(2.dp, shadowColor, DayCardShape)
                    .clip(DayCardShape)
                    .background(backgroundColor)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    ),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistDayCardPreview() {
    NeoBrutalistDayCard(
        backgroundColor = Color(0xFFDAF5F0),
        modifier = Modifier.size(72.dp),
    ) {
        Text("15", style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistDayCardPressedPreview() {
    NeoBrutalistDayCard(
        backgroundColor = Color(0xFFDAF5F0),
        modifier = Modifier.size(72.dp),
        forcePressed = true,
    ) {
        Text("15", style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistDayCardSelectedPreview() {
    NeoBrutalistDayCard(
        backgroundColor = Color(0xFFFFE082),
        modifier = Modifier.size(72.dp),
    ) {
        Text("15", style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBrutalistDayCardWithShiftPreview() {
    NeoBrutalistDayCard(
        backgroundColor = Color(0xFFDAF5F0),
        modifier = Modifier.size(72.dp),
    ) {
        Text("5", style = MaterialTheme.typography.labelMedium)
    }
}
