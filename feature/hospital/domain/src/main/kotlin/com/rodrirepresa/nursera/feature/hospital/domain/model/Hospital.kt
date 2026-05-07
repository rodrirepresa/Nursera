package com.rodrirepresa.nursera.feature.hospital.domain.model

import java.util.UUID

data class Hospital(
    val id: UUID,
    val name: String,
    val color: Int,
    val irpf: Float,
    val shifts: List<ShiftType>,
)
