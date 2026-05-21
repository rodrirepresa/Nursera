package com.rodrirepresa.nursera.feature.hospital.presentation.edit.mappers

import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.timeFormatter
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.ShiftUiModel
import java.time.LocalTime

internal fun ShiftType.toShiftUiModel(): ShiftUiModel =
    ShiftUiModel(
        id = id,
        name = name,
        schedule = "${startTime.format(timeFormatter)}–${endTime.format(timeFormatter)}",
        hourlyRate = "${"%.2f".format(hourlyRate)}€/h",
    )

internal fun Float.toFormattedIrpf(): String = if (this == this.toLong().toFloat()) this.toLong().toString() else this.toString()

internal fun String.toLocalTime(): LocalTime = LocalTime.parse(this, timeFormatter)
