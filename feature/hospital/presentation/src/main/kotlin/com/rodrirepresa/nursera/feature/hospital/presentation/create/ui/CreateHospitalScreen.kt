package com.rodrirepresa.nursera.feature.hospital.presentation.create.ui

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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraCta
import com.rodrirepresa.nursera.core.ui.NurseraErrorView
import com.rodrirepresa.nursera.core.ui.NurseraIconButton
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.core.ui.NurseraTextField
import com.rodrirepresa.nursera.core.ui.NurseraToolbar
import com.rodrirepresa.nursera.feature.hospital.presentation.R
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalViewModel
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateShiftItem
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun CreateHospitalScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateHospitalViewModel = hiltViewModel(),
) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is CreateHospitalSideEffect.NavigateBack -> onNavigateBack()
            }
        },
    ) { state ->
        when (state) {
            is CreateHospitalState.Loading -> NurseraLoadingView()
            is CreateHospitalState.Error ->
                NurseraErrorView(
                    message = stringResource(R.string.create_hospital_error_message),
                )
            is CreateHospitalState.Loaded ->
                CreateHospitalLoadedContent(
                    state = state,
                    executeIntent = viewModel::execute,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateHospitalLoadedContent(
    state: CreateHospitalState.Loaded,
    executeIntent: (CreateHospitalIntent) -> Unit,
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
            title = stringResource(R.string.create_hospital_title),
            onNavigateBack = { executeIntent(CreateHospitalIntent.NavigateBack) },
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
                    value = state.name,
                    onValueChange = { executeIntent(CreateHospitalIntent.UpdateName(it)) },
                    label = stringResource(R.string.create_hospital_name_label),
                    placeholder = stringResource(R.string.create_hospital_name_placeholder),
                    isError = state.nameError != null,
                    errorMessage = state.nameError?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shadowOffset = 4.dp,
                )
            }

            item {
                NurseraTextField(
                    value = state.irpf,
                    onValueChange = { raw ->
                        val filtered =
                            raw.filter { it.isDigit() || it == '.' || it == ',' }
                                .let { if (it.count { c -> c == '.' || c == ',' } > 1) state.irpf else it }
                        executeIntent(CreateHospitalIntent.UpdateIrpf(filtered))
                    },
                    label = stringResource(R.string.edit_hospital_irpf_label),
                    placeholder = stringResource(R.string.create_hospital_irpf_placeholder),
                    isError = state.irpfError != null,
                    errorMessage = state.irpfError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth(),
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
                        text = stringResource(R.string.create_hospital_shifts_section_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnimatedVisibility(
                            visible = state.shifts.any { it.isSelected },
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = fadeOut(animationSpec = tween(200)),
                        ) {
                            NurseraIconButton(
                                onClick = { executeIntent(CreateHospitalIntent.DeleteSelectedShifts) },
                                backgroundColor = Color(0xFFFF6B6B),
                                shadowOffset = 3.dp,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription =
                                        stringResource(
                                            R.string.create_hospital_delete_selected_content_description,
                                        ),
                                    modifier = Modifier.padding(0.dp),
                                )
                            }
                        }
                        NurseraIconButton(
                            onClick = { executeIntent(CreateHospitalIntent.OpenShiftSheet) },
                            shadowOffset = 3.dp,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription =
                                    stringResource(
                                        R.string.create_hospital_add_shift_content_description,
                                    ),
                                modifier = Modifier.padding(0.dp),
                            )
                        }
                    }
                }
            }

            items(state.shifts, key = { it.listKey }) { item ->
                ConfirmedShiftRow(
                    shift = item,
                    backgroundColor = Color(state.hospitalColor),
                    onClick = { executeIntent(CreateHospitalIntent.ToggleShiftSelection(item.id)) },
                    modifier =
                        Modifier.animateItem(
                            fadeInSpec = tween(500),
                            fadeOutSpec = tween(500),
                        ),
                )
            }

            item(key = "cta") {
                val ctaColor by animateColorAsState(
                    targetValue = if (state.canSave) Color(0xFFFF6B6B) else Color(state.hospitalColor),
                    animationSpec = tween(durationMillis = 300),
                    label = "ctaColor",
                )
                NurseraCta(
                    label = stringResource(R.string.create_hospital_save_button),
                    onClick = { executeIntent(CreateHospitalIntent.Save) },
                    isSaving = state.isSaving,
                    enabled = state.canSave,
                    backgroundColor = ctaColor,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp, end = 4.dp)
                            .animateItem(),
                )
                Spacer(modifier = Modifier.height(24.dp))
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

@Preview(showBackground = true)
@Composable
private fun CreateHospitalLoadingPreview() {
    NurseraLoadingView()
}

@Preview(showBackground = true)
@Composable
private fun CreateHospitalErrorPreview() {
    NurseraErrorView(message = "No se pudo abrir el formulario.")
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CreateHospitalLoadedContentPreview() {
    CreateHospitalLoadedContent(
        executeIntent = {},
        state =
            CreateHospitalState.Loaded(
                hospitalColor = 0xFFDAF5F0.toInt(),
                name = "Hospital La Paz",
                irpf = "15",
                shifts =
                    persistentListOf(
                        CreateShiftItem(
                            form =
                                ShiftFormUiState(
                                    name = "Morning",
                                    startTime = "08:00",
                                    endTime = "15:00",
                                    hourlyRate = "18.5",
                                ),
                            id = "1",
                        ),
                        CreateShiftItem(
                            form =
                                ShiftFormUiState(
                                    name = "Afternoon",
                                    startTime = "15:00",
                                    endTime = "22:00",
                                    hourlyRate = "19.0",
                                ),
                            id = "2",
                        ),
                    ),
                canSave = true,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CreateHospitalSheetPreview() {
    CreateHospitalLoadedContent(
        executeIntent = {},
        state =
            CreateHospitalState.Loaded(
                hospitalColor = 0xFFDAF5F0.toInt(),
                name = "Hospital La Paz",
                irpf = "15",
                shifts =
                    persistentListOf(
                        CreateShiftItem(
                            form =
                                ShiftFormUiState(
                                    name = "Morning",
                                    startTime = "08:00",
                                    endTime = "15:00",
                                    hourlyRate = "18.5",
                                ),
                            id = "1",
                        ),
                    ),
                shiftForm = ShiftFormUiState(name = "Night", startTime = "22:00", endTime = "08:00"),
                canSaveShiftForm = false,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CreateHospitalWithSelectionPreview() {
    CreateHospitalLoadedContent(
        executeIntent = {},
        state =
            CreateHospitalState.Loaded(
                hospitalColor = 0xFFDAF5F0.toInt(),
                name = "Hospital La Paz",
                irpf = "15",
                shifts =
                    persistentListOf(
                        CreateShiftItem(
                            form =
                                ShiftFormUiState(
                                    name = "Morning",
                                    startTime = "08:00",
                                    endTime = "15:00",
                                    hourlyRate = "18.5",
                                ),
                            isSelected = true,
                            id = "1",
                        ),
                        CreateShiftItem(
                            form =
                                ShiftFormUiState(
                                    name = "Afternoon",
                                    startTime = "15:00",
                                    endTime = "22:00",
                                    hourlyRate = "19.0",
                                ),
                            id = "2",
                        ),
                    ),
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CreateHospitalSavingPreview() {
    CreateHospitalLoadedContent(
        executeIntent = {},
        state =
            CreateHospitalState.Loaded(
                hospitalColor = 0xFFDAF5F0.toInt(),
                name = "Hospital La Paz",
                irpf = "15",
                shifts =
                    persistentListOf(
                        CreateShiftItem(
                            form =
                                ShiftFormUiState(
                                    name = "Morning",
                                    startTime = "08:00",
                                    endTime = "15:00",
                                    hourlyRate = "18.5",
                                ),
                            id = "1",
                        ),
                    ),
                canSave = true,
                isSaving = true,
            ),
    )
}
