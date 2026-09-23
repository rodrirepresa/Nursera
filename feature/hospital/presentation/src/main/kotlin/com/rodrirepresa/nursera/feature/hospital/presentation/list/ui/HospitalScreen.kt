package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraChip
import com.rodrirepresa.nursera.core.ui.NurseraCta
import com.rodrirepresa.nursera.core.ui.NurseraHeader
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.core.ui.NurseraPalette
import com.rodrirepresa.nursera.feature.hospital.presentation.R
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID
import java.util.UUID.randomUUID

@Composable
internal fun HospitalScreen(
    navigateToCreateHospital: () -> Unit,
    navigateToEditHospital: (UUID) -> Unit,
    viewModel: HospitalViewModel = hiltViewModel(),
) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            consumeSideEffects(
                sideEffect = sideEffect,
                navigateToCreateHospital = navigateToCreateHospital,
                navigateToEditHospital = navigateToEditHospital,
            )
        },
    ) { state ->
        when (state) {
            is HospitalState.Loading -> NurseraLoadingView()

            is HospitalState.Loaded -> {
                HospitalScreenLoaded(
                    hospitalList = state.hospitals,
                    executeIntent = viewModel::execute,
                )
            }

            HospitalState.Empty -> {
                HospitalScreenEmpty(executeIntent = viewModel::execute)
            }
        }
    }
}

@Composable
private fun HospitalScreenEmpty(executeIntent: (HospitalIntent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        NurseraHeader(
            title = stringResource(R.string.hospital_list_title),
            subtitle = stringResource(R.string.hospital_list_empty_subtitle),
            onIconClick = { executeIntent(HospitalIntent.OpenCreateHospital) },
            icon = Icons.Filled.Add,
            iconContentDescription = stringResource(R.string.hospital_list_add_content_description),
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HospitalEmptyIllustration()

            Spacer(modifier = Modifier.height(28.dp))

            NurseraChip(backgroundColor = NurseraPalette.Yellow) {
                Text(
                    text = stringResource(R.string.hospital_list_empty_eyebrow),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NurseraPalette.Ink,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.hospital_list_empty_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = NurseraPalette.Ink,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.hospital_list_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = NurseraPalette.Muted,
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Spacer(modifier = Modifier.height(28.dp))

            NurseraCta(
                label = stringResource(R.string.hospital_list_empty_cta),
                onClick = { executeIntent(HospitalIntent.OpenCreateHospital) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(28.dp))

            HospitalEmptyOnboardingSteps(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HospitalEmptyIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(148.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Back sticker — rotated, gives the sticker/collage depth typical of the app's neo-brutalist style
        Box(
            modifier =
                Modifier
                    .size(120.dp)
                    .rotate(-9f)
                    .border(2.5.dp, NurseraPalette.Ink, RoundedCornerShape(28.dp))
                    .background(NurseraPalette.Yellow, RoundedCornerShape(28.dp)),
        )

        // Front sticker holding the medical cross glyph
        Box(
            modifier =
                Modifier
                    .size(120.dp)
                    .rotate(5f)
                    .border(2.5.dp, NurseraPalette.Ink, RoundedCornerShape(28.dp))
                    .background(NurseraPalette.Paper, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center,
        ) {
            MedicalCrossGlyph()
        }

        // Floating accent dots — small collage details anchored to the illustration bounds
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-2).dp)
                    .size(26.dp)
                    .border(2.dp, NurseraPalette.Ink, CircleShape)
                    .background(Color(0xFFFF6B6B), CircleShape),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(18.dp)
                    .border(2.dp, NurseraPalette.Ink, CircleShape)
                    .background(Color(0xFFDAF5F0), CircleShape),
        )
    }
}

@Composable
private fun MedicalCrossGlyph(modifier: Modifier = Modifier) {
    val crossColor = Color(0xFFFF6B6B)
    Box(modifier = modifier.size(52.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .border(2.dp, NurseraPalette.Ink, RoundedCornerShape(5.dp))
                    .background(crossColor, RoundedCornerShape(5.dp)),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(18.dp)
                    .border(2.dp, NurseraPalette.Ink, RoundedCornerShape(5.dp))
                    .background(crossColor, RoundedCornerShape(5.dp)),
        )
    }
}

@Composable
private fun HospitalEmptyOnboardingSteps(modifier: Modifier = Modifier) {
    val steps =
        listOf(
            1 to stringResource(R.string.hospital_list_empty_step_1),
            2 to stringResource(R.string.hospital_list_empty_step_2),
            3 to stringResource(R.string.hospital_list_empty_step_3),
        )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        steps.forEach { (number, label) ->
            HospitalEmptyOnboardingStep(
                number = number,
                label = label,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HospitalEmptyOnboardingStep(
    number: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(28.dp)
                    .border(2.dp, NurseraPalette.Ink, CircleShape)
                    .background(NurseraPalette.Yellow, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = NurseraPalette.Ink,
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = NurseraPalette.Muted,
            maxLines = 2,
        )
    }
}

@Composable
private fun HospitalScreenLoaded(
    hospitalList: ImmutableList<HospitalUiModel>,
    executeIntent: (HospitalIntent) -> Unit,
) {
    Column {
        val label =
            pluralStringResource(
                R.plurals.hospital_list_count_subtitle,
                hospitalList.size,
                hospitalList.size,
            )

        NurseraHeader(
            title = stringResource(R.string.hospital_list_title),
            subtitle = label,
            onIconClick = { executeIntent(HospitalIntent.OpenCreateHospital) },
            icon = Icons.Filled.Add,
            iconContentDescription = stringResource(R.string.hospital_list_add_content_description),
        )

        LazyColumn(
            modifier =
                Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        ) {
            items(items = hospitalList, key = { it.id }) { hospital ->
                Box(modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)) {
                    HospitalCard(
                        hospital = hospital,
                        onHospitalClick = { executeIntent(HospitalIntent.OpenHospitalDetail(hospital.id)) },
                    )
                }
            }
        }
    }
}

private fun consumeSideEffects(
    sideEffect: HospitalSideEffect,
    navigateToCreateHospital: () -> Unit,
    navigateToEditHospital: (UUID) -> Unit,
) {
    when (sideEffect) {
        is HospitalSideEffect.OpenCreateHospital -> navigateToCreateHospital()
        is HospitalSideEffect.OpenHospitalDetail -> navigateToEditHospital(sideEffect.id)
    }
}

private fun previewHospitals(): ImmutableList<HospitalUiModel> =
    persistentListOf(
        HospitalUiModel(
            id = randomUUID(),
            name = "Hospital La Fe",
            color = 0xFFDAF5F0.toInt(),
            irpf = "15%",
            shifts =
                listOf(
                    ShiftTypeUiModel(name = "Morning", schedule = "08:00 - 15:00", hourlyRate = "12€"),
                    ShiftTypeUiModel(name = "Night", schedule = "22:00 - 08:00", hourlyRate = "18€"),
                ),
        ),
        HospitalUiModel(
            id = randomUUID(),
            name = "Hospital Clínico",
            color = 0xFFFFE566.toInt(),
            irpf = "19%",
            shifts = listOf(ShiftTypeUiModel(name = "Afternoon", schedule = "15:00 - 22:00", hourlyRate = "14€")),
        ),
    )

@Preview(showBackground = true)
@Composable
private fun HospitalScreenLoadingPreview() {
    NurseraLoadingView()
}

@Preview(showBackground = true)
@Composable
private fun HospitalScreenLoadedPreview() {
    HospitalScreenLoaded(
        hospitalList = previewHospitals(),
        executeIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun HospitalScreenEmptyPreview() {
    HospitalScreenEmpty(executeIntent = {})
}
