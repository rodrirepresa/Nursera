package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.feature.hospital.presentation.NeoBrutalistCard
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalViewModel

@Composable
internal fun HospitalScreen(
    onNavigateToCreate: () -> Unit,
    viewModel: HospitalViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.execute(HospitalIntent.Load)
    }

    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect -> consumeSideEffects(sideEffect) },
    ) { state ->
        Scaffold(
            floatingActionButton = {
                if (state is HospitalState.Loaded) {
                    FloatingActionButton(onClick = onNavigateToCreate) {
                        Icon(Icons.Default.Add, contentDescription = "Add hospital")
                    }
                }
            },
        ) { innerPadding ->
            when (state) {
                is HospitalState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HospitalState.Loaded -> {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(state.hospitals, key = { it.id }) { hospital ->
                            Box(modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)) {
                                HospitalCard(
                                    hospital = hospital,
                                    onDeleteClick = { viewModel.execute(HospitalIntent.DeleteHospital(hospital.id)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HospitalCard(
    hospital: HospitalUiModel,
    onDeleteClick: () -> Unit,
) {
    NeoBrutalistCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color(hospital.color),
                    content = {},
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${hospital.name}",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "IRPF: ${hospital.irpf}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (hospital.shifts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                hospital.shifts.forEach { shift -> ShiftRow(shift) }
            }
        }
    }
}

@Composable
private fun ShiftRow(shift: ShiftTypeUiModel) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = shift.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "${shift.schedule}  ·  ${shift.hourlyRate}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun consumeSideEffects(sideEffect: HospitalSideEffect) {}
