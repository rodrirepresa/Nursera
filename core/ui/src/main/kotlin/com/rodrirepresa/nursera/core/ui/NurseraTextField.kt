package com.rodrirepresa.nursera.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NurseraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    suffix: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    backgroundColor: Color = Color.White,
    shadowColor: Color = Color(0xFF1A1A1A),
    shadowOffset: Dp = 0.dp,
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        Box(
            modifier =
                if (shadowOffset > 0.dp) {
                    Modifier.padding(bottom = shadowOffset, end = shadowOffset)
                } else {
                    Modifier
                },
        ) {
            val borderColor = if (isError) Color(0xFFB00020) else shadowColor
            if (shadowOffset > 0.dp) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .offset(x = shadowOffset, y = shadowOffset)
                            .border(2.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(borderColor, RoundedCornerShape(12.dp)),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.replace("\n", "")) },
                singleLine = true,
                keyboardOptions = keyboardOptions,
                textStyle =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = shadowColor,
                        fontWeight = FontWeight.Medium,
                    ),
                cursorBrush = SolidColor(shadowColor),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.5.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .background(backgroundColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9E9E9E),
                                )
                            }
                            innerTextField()
                        }
                        if (suffix != null) {
                            suffix()
                        }
                    }
                },
            )
        }
        Text(
            text = if (isError && errorMessage != null) errorMessage else "",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB00020),
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
        )
    }
}

private val PercentSuffix: @Composable () -> Unit = {
    Text(
        text = "%",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF9E9E9E),
        modifier = Modifier.padding(start = 8.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraTextFieldEmptyPreview() {
    NurseraTextField(
        value = "",
        onValueChange = {},
        label = "IRPF",
        placeholder = "0 – 100",
        suffix = PercentSuffix,
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraTextFieldWithValuePreview() {
    NurseraTextField(
        value = "15",
        onValueChange = {},
        label = "IRPF",
        suffix = PercentSuffix,
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraTextFieldWithShadowPreview() {
    NurseraTextField(
        value = "15",
        onValueChange = {},
        label = "IRPF",
        shadowOffset = 4.dp,
        backgroundColor = Color(0xFFF4D738),
        suffix = PercentSuffix,
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraTextFieldErrorPreview() {
    NurseraTextField(
        value = "150",
        onValueChange = {},
        label = "IRPF",
        isError = true,
        errorMessage = "El IRPF debe estar entre 0 y 100",
        suffix = PercentSuffix,
    )
}

@Preview(showBackground = true)
@Composable
private fun NurseraTextFieldErrorWithShadowPreview() {
    NurseraTextField(
        value = "150",
        onValueChange = {},
        label = "IRPF",
        isError = true,
        errorMessage = "El IRPF debe estar entre 0 y 100",
        shadowOffset = 4.dp,
        backgroundColor = Color(0xFFF4D738),
        suffix = PercentSuffix,
    )
}
