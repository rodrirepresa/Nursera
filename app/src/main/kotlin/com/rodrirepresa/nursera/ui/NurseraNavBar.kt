package com.rodrirepresa.nursera.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodrirepresa.nursera.TopLevelDestination

// ─── OPCIÓN 1 — Pill flotante negro, activo amarillo ───────────────────────

private val Ink = Color(0xFF1A1A1A)
private val Paper = Color(0xFFFFF8F0)
private val Yellow = Color(0xFFFFE566)

@Composable
internal fun NurseraNavBar1(
    destinations: List<TopLevelDestination>,
    isSelected: (TopLevelDestination) -> Boolean,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            //.height(56.dp)
            .fillMaxWidth()
            .background(Paper)
            .padding(
                start = 16.dp,
                top = 5.dp,
                end = 16.dp,
                bottom = 1.dp,
            )
            .navigationBarsPadding(),
    ) {
        // Sombra del pill
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF555555)),
        )
        // Pill negro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Ink)
                .border(2.5.dp, Ink, RoundedCornerShape(20.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            destinations.forEach { destination ->
                val selected = isSelected(destination)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val pressOffset by animateDpAsState(
                    targetValue = if (isPressed) 1.dp else 0.dp,
                    label = "press",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = pressOffset, y = pressOffset)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) Yellow else Color.Transparent)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onDestinationClick(destination) },
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = if (selected) Ink else Color(0xFF888888),
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = destination.label.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (selected) Ink else Color(0xFF888888),
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }
    }
}

// ─── OPCIÓN 2 — Borde superior 3dp, activo caja negra ──────────────────────

@Composable
internal fun NurseraNavBar2(
    destinations: List<TopLevelDestination>,
    isSelected: (TopLevelDestination) -> Boolean,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Paper)
            .drawBehind {
                drawLine(
                    color = Ink,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 3.dp.toPx(),
                )
            }
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        destinations.forEach { destination ->
            val selected = isSelected(destination)
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val pressOffset by animateDpAsState(
                targetValue = if (isPressed) 2.dp else 0.dp,
                label = "press",
            )
            val bgColor by animateColorAsState(
                targetValue = if (selected) Ink else Paper,
                animationSpec = tween(150),
                label = "bg",
            )
            val contentColor by animateColorAsState(
                targetValue = if (selected) Yellow else Color(0xFFBBBBBB),
                animationSpec = tween(150),
                label = "content",
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .offset(x = pressOffset, y = pressOffset)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .then(
                        if (selected) Modifier.border(2.5.dp, Ink, RoundedCornerShape(10.dp))
                        else Modifier,
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onDestinationClick(destination) },
                    )
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.label,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = destination.label.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = contentColor,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

// ─── OPCIÓN 4 — Solo iconos, dot rojo en activo ────────────────────────────

@Composable
internal fun NurseraNavBar4(
    destinations: List<TopLevelDestination>,
    isSelected: (TopLevelDestination) -> Boolean,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Paper)
            .drawBehind {
                drawLine(
                    color = Ink,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5.dp.toPx(),
                )
            }
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        destinations.forEach { destination ->
            val selected = isSelected(destination)
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val pressOffset by animateDpAsState(
                targetValue = if (isPressed) 2.dp else 0.dp,
                label = "press",
            )
            val iconColor by animateColorAsState(
                targetValue = if (selected) Ink else Color(0xFFBBBBBB),
                animationSpec = tween(150),
                label = "iconColor",
            )

            Column(
                modifier = Modifier
                    .offset(x = pressOffset, y = pressOffset)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onDestinationClick(destination) },
                    )
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp),
                )
                // Dot — animado con scale
                val dotScale by animateFloatAsState(
                    targetValue = if (selected) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                    label = "dotScale",
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dotScale)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6B6B))
                        .border(1.5.dp, Ink, CircleShape),
                )
            }
        }
    }
}
