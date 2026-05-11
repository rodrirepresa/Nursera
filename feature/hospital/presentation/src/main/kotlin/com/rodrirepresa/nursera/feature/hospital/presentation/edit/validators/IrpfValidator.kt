package com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators

internal fun validateIrpf(value: String): String? {
    if (value.isBlank()) return null
    val f = value.toFloatOrNull() ?: return "Introduce un número válido"
    if (f < 0 || f > 100) return "El IRPF debe estar entre 0 y 100"
    return null
}
