package com.rodrirepresa.nursera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rodrirepresa.nursera.ui.NurseraApp
import com.rodrirepresa.nursera.ui.theme.NurseraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NurseraTheme {
                NurseraApp()
            }
        }
    }
}
