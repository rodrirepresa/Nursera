package com.rodrirepresa.nursera.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodrirepresa.nursera.TopLevelDestination
import com.rodrirepresa.nursera.core.ui.NurseraPalette
import com.rodrirepresa.nursera.ui.theme.NurseraTheme

private val TopBorderWidth = 2.dp

@Composable
internal fun NurseraNavigationBar(
    destinations: List<TopLevelDestination>,
    isSelected: (TopLevelDestination) -> Boolean,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier =
            modifier.drawBehind {
                drawLine(
                    color = NurseraPalette.Ink,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = TopBorderWidth.toPx(),
                )
            },
        containerColor = NurseraPalette.Paper,
        tonalElevation = 0.dp,
    ) {
        destinations.forEach { destination ->
            val selected = isSelected(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        // The label below already names the destination, so describing
                        // the icon too would make TalkBack announce it twice.
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.labelRes),
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                    )
                },
                alwaysShowLabel = true,
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = NurseraPalette.Ink,
                        selectedTextColor = NurseraPalette.Ink,
                        indicatorColor = NurseraPalette.Yellow,
                        unselectedIconColor = NurseraPalette.Muted,
                        unselectedTextColor = NurseraPalette.Muted,
                    ),
            )
        }
    }
}

@Preview(name = "Hospitals selected")
@Composable
private fun NurseraNavigationBarHospitalPreview() {
    NurseraNavigationBarPreview(TopLevelDestination.HOSPITAL)
}

@Preview(name = "Schedule selected")
@Composable
private fun NurseraNavigationBarSchedulePreview() {
    NurseraNavigationBarPreview(TopLevelDestination.SCHEDULE)
}

@Preview(name = "Profile selected")
@Composable
private fun NurseraNavigationBarProfilePreview() {
    NurseraNavigationBarPreview(TopLevelDestination.PROFILE)
}

@Composable
private fun NurseraNavigationBarPreview(selected: TopLevelDestination) {
    NurseraTheme {
        NurseraNavigationBar(
            destinations = TopLevelDestination.entries,
            isSelected = { it == selected },
            onDestinationClick = {},
        )
    }
}
