package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.Intent
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.ShiftTypeFormData

sealed interface CreateHospitalIntent : Intent {
    data class Save(
        val name: String,
        val color: Int,
        val irpf: Float,
        val shifts: List<ShiftTypeFormData>,
    ) : CreateHospitalIntent
}
