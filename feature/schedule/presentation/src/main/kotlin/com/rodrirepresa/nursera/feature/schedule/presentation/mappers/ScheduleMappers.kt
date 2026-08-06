package com.rodrirepresa.nursera.feature.schedule.presentation.mappers

import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.DayShiftUiModel

internal fun ScheduledShift.toDayShiftUiModel(): DayShiftUiModel =
    DayShiftUiModel(
        id = id.toString(),
        hospitalName = hospitalName,
        hospitalColor = hospitalColor,
        shiftName = shiftName,
        startTime = startTime,
    )
