package com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators

import androidx.annotation.StringRes
import com.rodrirepresa.nursera.feature.hospital.presentation.R

@StringRes
internal fun validateIrpf(value: String): Int? {
    if (value.isBlank()) return null
    val parsed = value.toFloatOrNull() ?: return R.string.validation_enter_valid_number
    if (parsed < 0 || parsed > 100) return R.string.validation_irpf_out_of_range
    return null
}
