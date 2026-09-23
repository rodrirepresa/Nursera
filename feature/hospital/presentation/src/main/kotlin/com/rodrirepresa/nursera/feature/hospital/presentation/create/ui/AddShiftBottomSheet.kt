package com.rodrirepresa.nursera.feature.hospital.presentation.create.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.core.ui.NurseraCta
import com.rodrirepresa.nursera.core.ui.NurseraTextField
import com.rodrirepresa.nursera.feature.hospital.presentation.R
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddShiftBottomSheet(
    form: ShiftFormUiState,
    isSaving: Boolean,
    canSave: Boolean,
    sheetState: SheetState,
    executeIntent: (CreateHospitalIntent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { executeIntent(CreateHospitalIntent.DismissShiftSheet) },
        sheetState = sheetState,
        containerColor = Color(0xFFFFF8F0),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.edit_hospital_add_shift_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            NurseraTextField(
                value = form.name,
                onValueChange = { executeIntent(CreateHospitalIntent.UpdateNewShift(form.copy(name = it.take(30)))) },
                label = stringResource(R.string.edit_hospital_shift_name_label),
                isError = form.nameError != null,
                errorMessage = form.nameError?.let { stringResource(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.edit_hospital_shift_name_placeholder),
                shadowOffset = 4.dp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NurseraTextField(
                    value = form.startTime,
                    onValueChange = { executeIntent(CreateHospitalIntent.UpdateNewShift(form.copy(startTime = it))) },
                    label = stringResource(R.string.edit_hospital_shift_start_label),
                    placeholder = stringResource(R.string.edit_hospital_time_placeholder),
                    isError = form.startTimeError != null,
                    errorMessage = form.startTimeError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shadowOffset = 4.dp,
                    modifier =
                        Modifier
                            .weight(1f)
                            .onFocusChanged { focus ->
                                if (!focus.isFocused) {
                                    val autofilled = autofillTime(form.startTime)
                                    if (autofilled != form.startTime) {
                                        executeIntent(
                                            CreateHospitalIntent.UpdateNewShift(form.copy(startTime = autofilled)),
                                        )
                                    }
                                }
                            },
                )
                NurseraTextField(
                    value = form.endTime,
                    onValueChange = { executeIntent(CreateHospitalIntent.UpdateNewShift(form.copy(endTime = it))) },
                    label = stringResource(R.string.edit_hospital_shift_end_label),
                    placeholder = stringResource(R.string.edit_hospital_time_placeholder),
                    isError = form.endTimeError != null,
                    errorMessage = form.endTimeError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shadowOffset = 4.dp,
                    modifier =
                        Modifier
                            .weight(1f)
                            .onFocusChanged { focus ->
                                if (!focus.isFocused) {
                                    val autofilled = autofillTime(form.endTime)
                                    if (autofilled != form.endTime) {
                                        executeIntent(
                                            CreateHospitalIntent.UpdateNewShift(form.copy(endTime = autofilled)),
                                        )
                                    }
                                }
                            },
                )
            }

            NurseraTextField(
                value = form.hourlyRate,
                onValueChange = { executeIntent(CreateHospitalIntent.UpdateNewShift(form.copy(hourlyRate = it))) },
                label = stringResource(R.string.edit_hospital_hourly_rate_label),
                isError = form.hourlyRateError != null,
                errorMessage = form.hourlyRateError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.edit_hospital_hourly_rate_placeholder),
                shadowOffset = 4.dp,
            )

            NurseraCta(
                label = stringResource(R.string.create_hospital_save_shift_button),
                onClick = { executeIntent(CreateHospitalIntent.SaveShift) },
                isSaving = isSaving,
                enabled = canSave,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, end = 4.dp, top = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AddShiftBottomSheetPreview() {
    AddShiftBottomSheet(
        form = ShiftFormUiState(name = "Night", startTime = "22:00", endTime = "08:00"),
        isSaving = false,
        canSave = true,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        executeIntent = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AddShiftBottomSheetSavingPreview() {
    AddShiftBottomSheet(
        form = ShiftFormUiState(name = "Night", startTime = "22:00", endTime = "08:00"),
        isSaving = true,
        canSave = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        executeIntent = {},
    )
}
