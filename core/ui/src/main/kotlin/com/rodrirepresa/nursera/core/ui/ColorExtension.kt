package com.rodrirepresa.nursera.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

public fun Color.darken(factor: Float = 0.55f): Color = lerp(this, Color.Black, factor)
