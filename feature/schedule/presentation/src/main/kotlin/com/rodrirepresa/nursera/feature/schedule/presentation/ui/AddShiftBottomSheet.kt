package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.feature.schedule.presentation.R
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.AddShiftSheetUiState
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.HospitalPickerItem
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleIntent
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ShiftPickerItem
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private val HOUR_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddShiftBottomSheet(
    sheet: AddShiftSheetUiState,
    sheetState: SheetState,
    executeIntent: (ScheduleIntent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { executeIntent(ScheduleIntent.DismissAddShiftSheet) },
        sheetState = sheetState,
        containerColor = CalendarBackground,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (sheet) {
                is AddShiftSheetUiState.HospitalList -> {
                    Text(
                        text = stringResource(R.string.schedule_add_shift_pick_hospital),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (sheet.hospitals.isEmpty()) {
                        Text(
                            text = stringResource(R.string.schedule_add_shift_no_hospitals),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OutOfMonthText,
                        )
                    } else {
                        sheet.hospitals.forEach { hospital ->
                            HospitalPickerRow(
                                hospital = hospital,
                                onClick = { executeIntent(ScheduleIntent.SelectHospital(hospital.id)) },
                            )
                        }
                    }
                }

                is AddShiftSheetUiState.ShiftList -> {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { executeIntent(ScheduleIntent.OpenAddShiftSheet) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.schedule_add_shift_back_to_hospitals),
                        )
                        Text(
                            text = stringResource(R.string.schedule_add_shift_back_to_hospitals),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Text(
                        text = stringResource(R.string.schedule_add_shift_pick_shift),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    sheet.shifts.forEach { shift ->
                        ShiftPickerRow(
                            hospital = sheet.hospital,
                            shift = shift,
                            onClick = {
                                executeIntent(
                                    ScheduleIntent.AddShift(
                                        hospitalId = sheet.hospital.id,
                                        hospitalName = sheet.hospital.name,
                                        hospitalColor = sheet.hospital.color,
                                        shiftId = shift.id,
                                        shiftName = shift.name,
                                        startTime = LocalTime.parse(shift.startTime, HOUR_MINUTE_FORMATTER),
                                    ),
                                )
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AddShiftBottomSheetHospitalListPreview() {
    AddShiftBottomSheet(
        sheet =
            AddShiftSheetUiState.HospitalList(
                hospitals =
                    persistentListOf(
                        HospitalPickerItem(id = UUID.randomUUID(), name = "Hospital La Paz", color = 0xFFDAF5F0.toInt()),
                        HospitalPickerItem(id = UUID.randomUUID(), name = "Hospital Quirón", color = 0xFFFFCBA4.toInt()),
                    ),
            ),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        executeIntent = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AddShiftBottomSheetShiftListPreview() {
    val hospital = HospitalPickerItem(id = UUID.randomUUID(), name = "Hospital La Paz", color = 0xFFDAF5F0.toInt())
    AddShiftBottomSheet(
        sheet =
            AddShiftSheetUiState.ShiftList(
                hospital = hospital,
                shifts =
                    persistentListOf(
                        ShiftPickerItem(id = UUID.randomUUID(), name = "Morning", startTime = "08:00", endTime = "15:00"),
                    ),
            ),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        executeIntent = {},
    )
}
