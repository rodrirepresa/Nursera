package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.ObserveMonthScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
internal class ProfileViewModel
    @Inject
    constructor(
        dispatcherProvider: DispatcherProvider,
        private val observeMonthScheduleUseCase: ObserveMonthScheduleUseCase,
        private val observeHospitalsUseCase: ObserveHospitalsUseCase,
    ) : ViewModel(), MviHost<ProfileIntent, State<ProfileState, ProfileSideEffect>> {
        private val reducer: Reducer<ProfileIntent, State<ProfileState, ProfileSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = ProfileState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<ProfileState, ProfileSideEffect>> = reducer.state

        init {
            execute(ProfileIntent.Load)
        }

        override fun execute(intent: ProfileIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(intent: ProfileIntent): Flow<StateTransform<State<ProfileState, ProfileSideEffect>>> =
            when (intent) {
                is ProfileIntent.Load -> executeLoad()
                is ProfileIntent.SetDisplayedMonth -> executeSetDisplayedMonth(intent.month)
            }

        private fun executeLoad(): Flow<StateTransform<State<ProfileState, ProfileSideEffect>>> =
            flow {
                val todayMonth = YearMonth.now()
                emit(ProfileTransform.InitLoaded(todayMonth))
                emitAllObserveMonth(todayMonth)
            }

        private fun executeSetDisplayedMonth(month: YearMonth): Flow<StateTransform<State<ProfileState, ProfileSideEffect>>> =
            flow {
                emit(ProfileTransform.SetDisplayedMonth(month))
                val loaded = state.value.view as? ProfileState.Loaded ?: return@flow
                if (loaded.summariesByMonth.containsKey(month)) return@flow
                emitAllObserveMonth(month)
            }

        private suspend fun kotlinx.coroutines.flow.FlowCollector<StateTransform<State<ProfileState, ProfileSideEffect>>>.emitAllObserveMonth(
            month: YearMonth,
        ) {
            observeProfileMonth(month).collect { emit(it) }
        }

        private fun observeProfileMonth(month: YearMonth): Flow<StateTransform<State<ProfileState, ProfileSideEffect>>> =
            combine(
                observeMonthScheduleUseCase(month),
                observeHospitalsUseCase(),
            ) { shifts, hospitals ->
                buildMonthSummary(
                    month = month,
                    shifts = shifts,
                    hospitals = hospitals,
                )
            }.map { summary ->
                ProfileTransform.UpsertMonthSummary(month = month, summary = summary) as StateTransform<State<ProfileState, ProfileSideEffect>>
            }.catch { emit(ProfileTransform.ShowError) }
    }

private data class ShiftDescriptor(
    val hospital: Hospital,
    val shiftType: ShiftType,
)

private fun buildMonthSummary(
    month: YearMonth,
    shifts: List<ScheduledShift>,
    hospitals: List<Hospital>,
): ProfileMonthSummaryUiModel {
    val grouped =
        shifts.groupBy { shift ->
            val descriptor = resolveShiftDescriptor(shift, hospitals)
            descriptor?.hospital?.name ?: shift.hospitalName
        }

    val hospitalSummaries =
        grouped.mapNotNull { (hospitalName, hospitalShifts) ->
            val resolved = hospitalShifts.mapNotNull { shift -> resolveShiftDescriptor(shift, hospitals) }
            if (resolved.isEmpty()) return@mapNotNull null

            val grossAmount =
                resolved.sumOf { descriptor ->
                    val hours = computeShiftHours(descriptor.shiftType.startTime, descriptor.shiftType.endTime)
                    hours * descriptor.shiftType.hourlyRate
                }
            val hospital = resolved.first().hospital
            val irpf = hospital.irpf
            val netAmount = grossAmount * (1.0 - (irpf / 100f))
            HospitalEarningsUiModel(
                hospitalName = hospitalName,
                hospitalColor = hospital.color,
                grossAmount = grossAmount,
                netAmount = netAmount,
                irpf = irpf,
                percentage = 0f,
                shiftsCount = hospitalShifts.size,
            )
        }

    val totalGrossAmount = hospitalSummaries.sumOf { it.grossAmount }
    val totalNetAmount = hospitalSummaries.sumOf { it.netAmount }
    val withPercentages =
        hospitalSummaries
            .map { summary ->
                val percentage =
                    if (totalNetAmount <= 0.0) {
                        0f
                    } else {
                        ((summary.netAmount / totalNetAmount) * 100.0).toFloat()
                    }
                summary.copy(percentage = percentage)
            }.sortedByDescending { it.netAmount }
            .toPersistentList()

    return ProfileMonthSummaryUiModel(
        month = month,
        totalGrossAmount = totalGrossAmount,
        totalNetAmount = totalNetAmount,
        totalShifts = shifts.size,
        hospitals = withPercentages,
    )
}

private fun resolveShiftDescriptor(
    shift: ScheduledShift,
    hospitals: List<Hospital>,
): ShiftDescriptor? {
    val hospital =
        hospitals.firstOrNull { it.id == shift.hospitalId }
            ?: hospitals.firstOrNull { it.color == shift.hospitalColor }
            ?: hospitals.firstOrNull { normalizeName(it.name) == normalizeName(shift.hospitalName) }
            ?: return null

    val shiftType =
        hospital.shifts.firstOrNull { normalizeName(it.name) == normalizeName(shift.shiftName) && it.startTime == shift.startTime }
            ?: hospital.shifts.firstOrNull { normalizeName(it.name) == normalizeName(shift.shiftName) }
            ?: return null

    return ShiftDescriptor(hospital = hospital, shiftType = shiftType)
}

private fun normalizeName(value: String): String =
    value
        .lowercase()
        .replace(".", "")
        .replace("clínica", "clinica")
        .replace("hospital", "h")
        .replace(" ", "")

private fun computeShiftHours(
    start: LocalTime,
    end: LocalTime,
): Double {
    val startMinutes = start.hour * 60 + start.minute
    val endMinutes = end.hour * 60 + end.minute
    val duration = if (endMinutes > startMinutes) endMinutes - startMinutes else (24 * 60 - startMinutes) + endMinutes
    return max(duration, 0) / 60.0
}
