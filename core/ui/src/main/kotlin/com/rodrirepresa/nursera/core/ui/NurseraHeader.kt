package com.rodrirepresa.nursera.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
public fun NurseraHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    draDivider: Boolean = false,
    icon: ImageVector? = null,
    onIconClick: (() -> Unit)? = null,
    iconContentDescription: String? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (draDivider) {
                        Modifier.drawBehind {
                            drawLine(
                                color = Color(0xFF1A1A1A),
                                start = Offset(x = 0f, y = size.height - 1.5f),
                                end = Offset(x = size.width, y = size.height - 1.5f),
                                strokeWidth = 1.5.dp.toPx(),
                            )
                        }
                    } else {
                        Modifier
                    },
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
            )
            Box(
                modifier =
                    Modifier
                        .border(2.dp, Color(0xFF1A1A1A), RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFE566), RoundedCornerShape(6.dp))
                        .offset(x = 2.dp, y = 2.dp)
                        .shadow(0.dp)
                        .then(
                            Modifier.drawBehind {
                                drawRoundRect(
                                    color = Color(0xFF1A1A1A),
                                    topLeft = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 2.dp.toPx()),
                                    size = size,
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                                )
                            },
                        ),
            ) {
                Box(
                    modifier =
                        Modifier
                            .offset(x = (-2).dp, y = (-2).dp)
                            .border(2.dp, Color(0xFF1A1A1A), RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFE566), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1,
                    )
                }
            }
        }
        if (icon != null && onIconClick != null) {
            NurseraIconButton(
                onClick = onIconClick,
                shadowOffset = 3.dp,
                backgroundColor = Color.White,
                modifier = Modifier.align(Alignment.Bottom),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseraHeaderPreview() {
    MaterialTheme {
        NurseraHeader(
            title = "Hospitales",
            subtitle = "Gestiona tus centros de trabajo",
            iconContentDescription = "",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseraHeaderWithIconPreview() {
    MaterialTheme {
        NurseraHeader(
            title = "Hospitales",
            subtitle = "Gestiona tus centros de trabajo",
            iconContentDescription = "Ajustes",
            icon = Icons.Default.Settings,
            onIconClick = {},
        )
    }
}
