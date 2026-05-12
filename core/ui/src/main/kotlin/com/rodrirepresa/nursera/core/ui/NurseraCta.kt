package com.rodrirepresa.nursera.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NurseraCta(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = Color(0xFFFF6B6B),
) {
    val effectiveColor = if (enabled) backgroundColor else Color(0xFFCCCCCC)
    NeoBrutalistCard(
        modifier = modifier.height(56.dp),
        backgroundColor = effectiveColor,
        forcePressed = isSaving,
        onClick = { if (!isSaving && enabled) onClick() },
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSaving) {
                JumpingDots()
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun JumpingDots() {
    val transition = rememberInfiniteTransition(label = "jumpingDots")

    val offsets =
        (0..2).map { index ->
            val offset by transition.animateFloat(
                initialValue = 0f,
                targetValue = -6f,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = 300,
                                delayMillis = index * 100,
                                easing = LinearEasing,
                            ),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "dot$index",
            )
            offset
        }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        offsets.forEach { offset ->
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .offset(y = offset.dp)
                        .padding(horizontal = 2.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseraCtaPreview() {
    NurseraCta(
        label = "Guardar cambios",
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraCtaSavingPreview() {
    NurseraCta(
        label = "Guardar cambios",
        onClick = {},
        isSaving = true,
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraCtaDisabledPreview() {
    NurseraCta(
        label = "Guardar cambios",
        onClick = {},
        enabled = false,
    )
}
