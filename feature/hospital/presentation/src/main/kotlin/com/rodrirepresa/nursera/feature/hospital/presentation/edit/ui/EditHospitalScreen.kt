package com.rodrirepresa.nursera.feature.hospital.presentation.edit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.NurseraCta
import com.rodrirepresa.nursera.core.ui.NurseraErrorView
import com.rodrirepresa.nursera.core.ui.NurseraIconButton
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.core.ui.NurseraTextField
import com.rodrirepresa.nursera.core.ui.NurseraToolbar
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.hospital.presentation.R
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalViewModel
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.ShiftItem
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.ShiftUiModel
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Composable
internal fun EditHospitalScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditHospitalViewModel = hiltViewModel(),
) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is EditHospitalSideEffect.NavigateBack -> onNavigateBack()
            }
        },
    ) { state ->
        when (state) {
            is EditHospitalState.Loading -> NurseraLoadingView()
            is EditHospitalState.Error ->
                NurseraErrorView(
                    message = stringResource(R.string.edit_hospital_error_message),
                )

            is EditHospitalState.Loaded ->
                EditHospitalLoadedContent(
                    state = state,
                    executeIntent = viewModel::execute,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditHospitalLoadedContent(
    state: EditHospitalState.Loaded,
    executeIntent: (EditHospitalIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier =
            modifier
                .background(Color(0xFFFFF8F0))
                .fillMaxSize(),
    ) {
        NurseraToolbar(
            title = state.hospitalName,
            onNavigateBack = { executeIntent(EditHospitalIntent.NavigateBack) },
            applyStatusBarPadding = false,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val listState = rememberLazyListState()

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                NurseraTextField(
                    value = state.irpf,
                    onValueChange = { raw ->
                        val filtered =
                            raw.filter { it.isDigit() || it == '.' || it == ',' }
                                .let { if (it.count { c -> c == '.' || c == ',' } > 1) state.irpf else it }
                        executeIntent(EditHospitalIntent.UpdateIrpf(filtered))
                    },
                    label = stringResource(R.string.edit_hospital_irpf_label),
                    placeholder = stringResource(R.string.edit_hospital_irpf_placeholder),
                    isError = state.irpfError != null,
                    errorMessage = state.irpfError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shadowOffset = 4.dp,
                    suffix = {
                        Text(
                            text = stringResource(R.string.edit_hospital_irpf_suffix),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.LightGray,
                        )
                    },
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.edit_hospital_shifts_section_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnimatedVisibility(
                            visible = state.shifts.any { it is ShiftItem.Existing && it.isSelected },
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = fadeOut(animationSpec = tween(200)),
                        ) {
                            NurseraIconButton(
                                onClick = { executeIntent(EditHospitalIntent.DeleteSelectedShifts) },
                                backgroundColor = Color(0xFFFF6B6B),
                                shadowOffset = 3.dp,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription =
                                        stringResource(
                                            R.string.edit_hospital_delete_selected_content_description,
                                        ),
                                    modifier = Modifier.padding(0.dp),
                                )
                            }
                        }
                        NurseraIconButton(
                            onClick = { executeIntent(EditHospitalIntent.OpenShiftSheet) },
                            shadowOffset = 3.dp,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription =
                                    stringResource(
                                        R.string.edit_hospital_add_shift_content_description,
                                    ),
                                modifier = Modifier.padding(0.dp),
                            )
                        }
                    }
                }
            }

            items(state.shifts, key = { it.listKey }) { item ->
                when (item) {
                    is ShiftItem.Existing ->
                        ExistingShiftRow(
                            shift = item.model,
                            backgroundColor = Color(state.hospitalColor),
                            isSelected = item.isSelected,
                            onClick = { executeIntent(EditHospitalIntent.ToggleExistingShiftSelection(item.model.id)) },
                            modifier =
                                Modifier.animateItem(
                                    fadeInSpec = tween(500),
                                    fadeOutSpec = tween(500),
                                ),
                        )
                }
            }
        }
    }

    if (state.shiftForm != null) {
        AddShiftBottomSheet(
            form = state.shiftForm,
            isSaving = state.isShiftFormSaving,
            canSave = state.canSaveShiftForm,
            sheetState = sheetState,
            executeIntent = executeIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddShiftBottomSheet(
    form: ShiftFormUiState,
    isSaving: Boolean,
    canSave: Boolean,
    sheetState: SheetState,
    executeIntent: (EditHospitalIntent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { executeIntent(EditHospitalIntent.DismissShiftSheet) },
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
                onValueChange = { executeIntent(EditHospitalIntent.UpdateNewShift(form.copy(name = it.take(60)))) },
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
                    onValueChange = { executeIntent(EditHospitalIntent.UpdateNewShift(form.copy(startTime = it))) },
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
                                            EditHospitalIntent.UpdateNewShift(form.copy(startTime = autofilled)),
                                        )
                                    }
                                }
                            },
                )
                NurseraTextField(
                    value = form.endTime,
                    onValueChange = { executeIntent(EditHospitalIntent.UpdateNewShift(form.copy(endTime = it))) },
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
                                            EditHospitalIntent.UpdateNewShift(form.copy(endTime = autofilled)),
                                        )
                                    }
                                }
                            },
                )
            }

            NurseraTextField(
                value = form.hourlyRate,
                onValueChange = { executeIntent(EditHospitalIntent.UpdateNewShift(form.copy(hourlyRate = it))) },
                label = stringResource(R.string.edit_hospital_hourly_rate_label),
                isError = form.hourlyRateError != null,
                errorMessage = form.hourlyRateError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.edit_hospital_hourly_rate_placeholder),
                shadowOffset = 4.dp,
            )

            NurseraCta(
                label = stringResource(R.string.edit_hospital_save_shift_button),
                onClick = { executeIntent(EditHospitalIntent.SaveShift) },
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

@Composable
private fun ExistingShiftRow(
    shift: ShiftUiModel,
    backgroundColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF6B6B) else backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "shiftSelectionColor",
    )
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp),
        backgroundColor = animatedColor,
        forcePressed = isSelected,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    shift.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = backgroundColor.darken(),
                )
                Text(
                    shift.schedule,
                    style = MaterialTheme.typography.bodySmall,
                    color = backgroundColor.darken(),
                )
            }
            Text(
                shift.hourlyRate,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = backgroundColor.darken(),
            )
        }
    }
}

private fun autofillTime(value: String): String {
    if (value.isBlank() || value.contains(":")) return value
    val hour = value.toIntOrNull() ?: return value
    if (hour < 0 || hour > 23) return value
    return "%02d:00".format(hour)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun EditHospitalLoadedContentPreview() {
    EditHospitalLoadedContent(
        executeIntent = {},
        state =
            EditHospitalState.Loaded(
                hospitalId = UUID.randomUUID(),
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                originalIrpf = "15",
                irpf = "18",
                shifts =
                    persistentListOf(
                        ShiftItem.Existing(ShiftUiModel(id = UUID.randomUUID(), "Mañana", "08:00–15:00", "18.50€/h")),
                        ShiftItem.Existing(ShiftUiModel(id = UUID.randomUUID(), "Tarde", "15:00–22:00", "19.00€/h")),
                    ),
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun EditHospitalSheetPreview() {
    EditHospitalLoadedContent(
        executeIntent = {},
        state =
            EditHospitalState.Loaded(
                hospitalId = UUID.randomUUID(),
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                originalIrpf = "15",
                irpf = "18",
                shifts =
                    persistentListOf(
                        ShiftItem.Existing(ShiftUiModel(id = UUID.randomUUID(), "Mañana", "08:00–15:00", "18.50€/h")),
                    ),
                shiftForm = ShiftFormUiState(name = "Noche", startTime = "22:00", endTime = "08:00"),
                canSaveShiftForm = false,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun EditHospitalWithSelectionPreview() {
    EditHospitalLoadedContent(
        executeIntent = {},
        state =
            EditHospitalState.Loaded(
                hospitalId = UUID.randomUUID(),
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                originalIrpf = "15",
                irpf = "18",
                shifts =
                    persistentListOf(
                        ShiftItem.Existing(
                            ShiftUiModel(id = UUID.randomUUID(), "Mañana", "08:00–15:00", "18.50€/h"),
                            isSelected = true,
                        ),
                        ShiftItem.Existing(ShiftUiModel(id = UUID.randomUUID(), "Tarde", "15:00–22:00", "19.00€/h")),
                    ),
            ),
    )
}
