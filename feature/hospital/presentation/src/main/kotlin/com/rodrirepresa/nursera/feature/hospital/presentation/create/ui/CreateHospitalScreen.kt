package com.rodrirepresa.nursera.feature.hospital.presentation.create.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NeoBrutalistCard
import com.rodrirepresa.nursera.core.ui.NeoBrutalistIconButton
import com.rodrirepresa.nursera.core.ui.NurseraCta
import com.rodrirepresa.nursera.core.ui.NurseraTextField
import com.rodrirepresa.nursera.core.ui.NurseraToolbar
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalViewModel
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
            is CreateHospitalState.Loaded ->
                CreateHospitalLoadedContent(
                    state = state,
                    executeIntent = viewModel::execute,
                )
        }
    }
}

@Composable
private fun CreateHospitalLoadedContent(
    state: CreateHospitalState.Loaded,
    executeIntent: (CreateHospitalIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NurseraToolbar(
            title = "Nuevo Hospital",
            onNavigateBack = { executeIntent(CreateHospitalIntent.NavigateBack) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                NurseraTextField(
                    value = state.name,
                    onValueChange = { executeIntent(CreateHospitalIntent.UpdateName(it)) },
                    label = "Nombre del hospital",
                    placeholder = "Máximo 30 caracteres",
                    isError = state.nameError != null,
                    errorMessage = state.nameError,
                )
            }

            item {
                NurseraTextField(
                    value = state.irpf,
                    onValueChange = { executeIntent(CreateHospitalIntent.UpdateIrpf(it)) },
                    label = "IRPF",
                    placeholder = "0 – 100",
                    isError = state.irpfError != null,
                    errorMessage = state.irpfError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    backgroundColor = Color(0xFFF4D738),
                    shadowOffset = 4.dp,
                    suffix = {
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(start = 8.dp),
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
                        text = "Tipos de turno",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    NeoBrutalistIconButton(
                        onClick = { executeIntent(CreateHospitalIntent.AddShift) },
                        backgroundColor = Color.White,
                        shadowOffset = 3.dp,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir turno",
                            modifier = Modifier.padding(0.dp),
                        )
                    }
                }
            }

            itemsIndexed(state.shifts, key = { index, _ -> index }) { index, shift ->
                ShiftCard(
                    shift = shift,
                    canDelete = state.shifts.size > 1,
                    onUpdate = { executeIntent(CreateHospitalIntent.UpdateShiftAt(index, it)) },
                    onDelete = { executeIntent(CreateHospitalIntent.RemoveShift(index)) },
                )
            }

            item {
                NurseraCta(
                    label = "Guardar hospital",
                    onClick = { executeIntent(CreateHospitalIntent.Save) },
                    isSaving = state.isSaving,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp, end = 4.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ShiftCard(
    shift: ShiftFormUiState,
    canDelete: Boolean,
    onUpdate: (ShiftFormUiState) -> Unit,
    onDelete: () -> Unit,
) {
    NeoBrutalistCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp),
        backgroundColor = Color(0xFFF4D738),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NurseraTextField(
                    value = shift.name,
                    onValueChange = { onUpdate(shift.copy(name = it.take(30))) },
                    label = "Nombre del turno",
                    isError = shift.nameError != null,
                    errorMessage = shift.nameError,
                    modifier = Modifier.weight(1f),
                )
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar turno",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NurseraTextField(
                    value = shift.startTime,
                    onValueChange = { onUpdate(shift.copy(startTime = it)) },
                    label = "Inicio",
                    placeholder = "HH:mm",
                    isError = shift.startTimeError != null,
                    errorMessage = shift.startTimeError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                NurseraTextField(
                    value = shift.endTime,
                    onValueChange = { onUpdate(shift.copy(endTime = it)) },
                    label = "Fin",
                    placeholder = "HH:mm",
                    isError = shift.endTimeError != null,
                    errorMessage = shift.endTimeError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }

            NurseraTextField(
                value = shift.hourlyRate,
                onValueChange = { onUpdate(shift.copy(hourlyRate = it)) },
                label = "€/hora",
                isError = shift.hourlyRateError != null,
                errorMessage = shift.hourlyRateError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateHospitalLoadedContentPreview() {
    CreateHospitalLoadedContent(
        executeIntent = {},
        state =
            CreateHospitalState.Loaded(
                name = "Hospital La Paz",
                irpf = "15",
                shifts =
                    persistentListOf(
                        ShiftFormUiState(name = "Mañana", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                    ),
                canSave = true,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateHospitalLoadedContentSavingPreview() {
    CreateHospitalLoadedContent(
        executeIntent = {},
        state =
            CreateHospitalState.Loaded(
                name = "Hospital La Paz",
                irpf = "15",
                shifts =
                    persistentListOf(
                        ShiftFormUiState(name = "Mañana", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                    ),
                canSave = true,
                isSaving = true,
            ),
    )
}
