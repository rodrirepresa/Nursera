package com.rodrirepresa.nursera.feature.hospital.presentation.edit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.feature.hospital.presentation.NeoBrutalistCard
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalViewModel
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.ExistingShiftUiModel
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditHospitalScreen(
    hospitalId: UUID,
    onNavigateBack: () -> Unit,
    viewModel: EditHospitalViewModel = hiltViewModel(),
) {
    LaunchedEffect(hospitalId) {
        viewModel.execute(EditHospitalIntent.Load(hospitalId))
    }

    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is EditHospitalSideEffect.NavigateBack -> onNavigateBack()
            }
        },
    ) { state ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (state is EditHospitalState.Form) {
                            Text(state.hospitalName, maxLines = 1)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                )
            },
        ) { innerPadding ->
            when (state) {
                is EditHospitalState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is EditHospitalState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se pudo cargar el hospital.")
                    }
                }

                is EditHospitalState.Saving -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is EditHospitalState.Form -> {
                    EditHospitalForm(
                        formState = state,
                        onUpdateIrpf = { viewModel.execute(EditHospitalIntent.UpdateIrpf(it)) },
                        onAddShift = { viewModel.execute(EditHospitalIntent.AddShift) },
                        onRemoveShift = { viewModel.execute(EditHospitalIntent.RemoveShift(it)) },
                        onUpdateShift = { index, updated ->
                            viewModel.execute(EditHospitalIntent.UpdateShiftAt(index, updated))
                        },
                        onSave = { viewModel.execute(EditHospitalIntent.Save) },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun EditHospitalForm(
    formState: EditHospitalState.Form,
    onUpdateIrpf: (String) -> Unit,
    onAddShift: () -> Unit,
    onRemoveShift: (Int) -> Unit,
    onUpdateShift: (Int, ShiftFormUiState) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HospitalColorBadge(
                name = formState.hospitalName,
                color = Color(formState.hospitalColor),
            )
        }

        item {
            OutlinedTextField(
                value = formState.irpf,
                onValueChange = onUpdateIrpf,
                label = { Text("IRPF %") },
                placeholder = { Text("0 – 100") },
                isError = formState.irpfError != null,
                supportingText =
                    formState.irpfError?.let { error ->
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }

        if (formState.existingShifts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Turnos actuales",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(formState.existingShifts.size) { index ->
                ExistingShiftRow(formState.existingShifts[index])
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Añadir nuevos turnos",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onAddShift) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir turno")
                }
            }
        }

        itemsIndexed(formState.newShifts, key = { index, _ -> index }) { index, shift ->
            NewShiftCard(
                shift = shift,
                canDelete = true,
                onUpdate = { onUpdateShift(index, it) },
                onDelete = { onRemoveShift(index) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSave,
                enabled = formState.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Guardar cambios")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HospitalColorBadge(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ExistingShiftRow(shift: ExistingShiftUiModel) {
    NeoBrutalistCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp),
        backgroundColor = Color(0xFFF5F5F0),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(shift.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(shift.schedule, style = MaterialTheme.typography.bodySmall)
            }
            Text(shift.hourlyRate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun NewShiftCard(
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
        backgroundColor = Color(0xFFF5F5F0),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = shift.name,
                    onValueChange = { onUpdate(shift.copy(name = it.take(60))) },
                    label = { Text("Nombre del turno") },
                    isError = shift.nameError != null,
                    supportingText =
                        shift.nameError?.let { error ->
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
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
                OutlinedTextField(
                    value = shift.startTime,
                    onValueChange = { onUpdate(shift.copy(startTime = it)) },
                    label = { Text("Inicio") },
                    placeholder = { Text("HH:mm") },
                    isError = shift.startTimeError != null,
                    supportingText =
                        shift.startTimeError?.let { error ->
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = shift.endTime,
                    onValueChange = { onUpdate(shift.copy(endTime = it)) },
                    label = { Text("Fin") },
                    placeholder = { Text("HH:mm") },
                    isError = shift.endTimeError != null,
                    supportingText =
                        shift.endTimeError?.let { error ->
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            OutlinedTextField(
                value = shift.hourlyRate,
                onValueChange = { onUpdate(shift.copy(hourlyRate = it)) },
                label = { Text("€/hora") },
                isError = shift.hourlyRateError != null,
                supportingText =
                    shift.hourlyRateError?.let { error ->
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditHospitalFormPreview() {
    EditHospitalForm(
        formState =
            EditHospitalState.Form(
                hospitalId = UUID.randomUUID(),
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                originalIrpf = "15",
                irpf = "18",
                existingShifts =
                    persistentListOf(
                        ExistingShiftUiModel("Mañana", "08:00–15:00", "18.50€/h"),
                        ExistingShiftUiModel("Tarde", "15:00–22:00", "19.00€/h"),
                    ),
                newShifts =
                    persistentListOf(
                        ShiftFormUiState(name = "Noche", startTime = "22:00", endTime = "08:00", hourlyRate = "23.0"),
                    ),
                isDirty = true,
                canSave = true,
            ),
        onUpdateIrpf = {},
        onAddShift = {},
        onRemoveShift = {},
        onUpdateShift = { _, _ -> },
        onSave = {},
    )
}
