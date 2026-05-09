package com.rodrirepresa.nursera.feature.hospital.presentation.create.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalViewModel
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.ShiftTypeFormData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateHospitalScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateHospitalViewModel = hiltViewModel(),
) {
    var name by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("1565C0") }
    var irpf by remember { mutableStateOf("") }
    var shifts by remember { mutableStateOf(listOf(ShiftTypeFormData())) }

    val isSaveEnabled =
        name.isNotBlank() &&
            irpf.toFloatOrNull() != null &&
            colorHex.length == 6 &&
            shifts.all {
                it.name.isNotBlank() &&
                    it.hourlyRate.toDoubleOrNull() != null &&
                    isValidTime(it.startTime) &&
                    isValidTime(it.endTime)
            }

    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is CreateHospitalSideEffect.NavigateBack -> onNavigateBack()
            }
        },
    ) { state ->
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopAppBar(
                    title = { Text("New Hospital") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
        ) { innerPadding ->
            when (state) {
                is CreateHospitalState.Saving -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CreateHospitalState.Idle -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Hospital name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = colorHex,
                                onValueChange = { if (it.length <= 6) colorHex = it },
                                label = { Text("Color (hex)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                prefix = { Text("#") },
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = irpf,
                                onValueChange = { irpf = it },
                                label = { Text("IRPF %") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Shift types", style = MaterialTheme.typography.labelLarge)
                            IconButton(onClick = { shifts = shifts + ShiftTypeFormData() }) {
                                Icon(Icons.Default.Add, contentDescription = "Add shift")
                            }
                        }

                        shifts.forEachIndexed { index, shift ->
                            ShiftFormRow(
                                shift = shift,
                                onUpdate = { updated ->
                                    shifts = shifts.toMutableList().also { it[index] = updated }
                                },
                                onDelete =
                                    if (shifts.size > 1) {
                                        { shifts = shifts.toMutableList().also { it.removeAt(index) } }
                                    } else {
                                        null
                                    },
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val color = colorHex.toLong(16).toInt() or 0xFF000000.toInt()
                                viewModel.execute(
                                    CreateHospitalIntent.Save(
                                        name = name,
                                        color = color,
                                        irpf = irpf.toFloat(),
                                        shifts = shifts,
                                    ),
                                )
                            },
                            enabled = isSaveEnabled,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShiftFormRow(
    shift: ShiftTypeFormData,
    onUpdate: (ShiftTypeFormData) -> Unit,
    onDelete: (() -> Unit)?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = shift.name,
                onValueChange = { onUpdate(shift.copy(name = it)) },
                label = { Text("Shift name") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove shift",
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
                label = { Text("Start (HH:mm)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = shift.startTime.isNotEmpty() && !isValidTime(shift.startTime),
            )
            OutlinedTextField(
                value = shift.endTime,
                onValueChange = { onUpdate(shift.copy(endTime = it)) },
                label = { Text("End (HH:mm)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = shift.endTime.isNotEmpty() && !isValidTime(shift.endTime),
            )
            OutlinedTextField(
                value = shift.hourlyRate,
                onValueChange = { onUpdate(shift.copy(hourlyRate = it)) },
                label = { Text("€/h") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
    }
}

private fun isValidTime(value: String): Boolean = Regex("""^([01]\d|2[0-3]):[0-5]\d$""").matches(value)
