package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.HospitalEarningsUiModel
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun DonutChart(
    hospitals: List<HospitalEarningsUiModel>,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val borderWidth = 2.5.dp.toPx()
        val strokeWidth = size.minDimension * 0.26f
        val diameter = size.minDimension - strokeWidth - borderWidth * 2f
        val ringTopLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)

        val chartSlices =
            hospitals
                .filter { it.percentage > 0f }
                .ifEmpty {
                    listOf(HospitalEarningsUiModel("", 0xFFE5DCD2.toInt(), 0.0, 0.0, 0f, 100f, 0))
                }

        // No gap between slices — a fully continuous ring instead of separated segments.
        val availableAngle = 360f
        var startAngle = -90f
        chartSlices.forEach { hospital ->
            val targetSweep = (hospital.percentage / 100f) * availableAngle
            val animatedSweep = targetSweep * progress
            if (animatedSweep > 0f) {
                drawNeoBrutalistSlice(
                    color = Color(hospital.hospitalColor),
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    topLeft = ringTopLeft,
                    diameter = diameter,
                    strokeWidth = strokeWidth,
                    borderWidth = borderWidth,
                )
            }
            // Advance by the full target sweep (not the animated one) so slices grow in place
            // instead of chasing each other while the animation plays.
            startAngle += targetSweep
        }
    }
}

private fun DrawScope.drawNeoBrutalistSlice(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    topLeft: Offset,
    diameter: Float,
    strokeWidth: Float,
    borderWidth: Float,
) {
    // Flat neo-brutalist look: a hard black outline plus the flat fill color, no drop shadow.
    drawArc(
        color = BorderColor,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = Size(diameter, diameter),
        style = Stroke(width = strokeWidth + borderWidth * 2f, cap = StrokeCap.Butt),
    )
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = Size(diameter, diameter),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
    )
}

@Preview(showBackground = true)
@Composable
private fun DonutChartPreview() {
    DonutChart(
        hospitals =
            persistentListOf(
                HospitalEarningsUiModel("Hospital La Paz", 0xFFDAF5F0.toInt(), 980.5, 833.43, 15f, 44.37f, 5),
                HospitalEarningsUiModel("Hospital Quirón", 0xFFFFCBA4.toInt(), 792.0, 633.6, 20f, 33.73f, 4),
                HospitalEarningsUiModel("Hospital La Fe", 0xFFFF8FAB.toInt(), 500.0, 411.15, 17.77f, 21.9f, 3),
            ),
        progress = 1f,
    )
}
