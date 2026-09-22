package com.rodrirepresa.nursera.feature.hospital.presentation.localization

import android.app.Application
import com.rodrirepresa.nursera.feature.hospital.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class HospitalLocalizationTest {
    private val context: Application get() = RuntimeEnvironment.getApplication()

    @Test
    @Config(qualifiers = "en")
    fun `english locale resolves default strings`() {
        assertEquals("My Hospitals", context.getString(R.string.hospital_list_title))
        assertEquals("New Hospital", context.getString(R.string.create_hospital_title))
    }

    @Test
    @Config(qualifiers = "es")
    fun `spanish locale resolves translated strings`() {
        assertEquals("Mis Hospitales", context.getString(R.string.hospital_list_title))
        assertEquals("Nuevo Hospital", context.getString(R.string.create_hospital_title))
    }

    @Test
    @Config(qualifiers = "fr")
    fun `unsupported locale falls back to english`() {
        assertEquals("My Hospitals", context.getString(R.string.hospital_list_title))
    }

    @Test
    @Config(qualifiers = "es")
    fun `spanish plurals resolve for both quantities`() {
        val resources = context.resources
        assertEquals("Un centro de trabajo", resources.getQuantityString(R.plurals.hospital_list_count_subtitle, 1, 1))
        assertEquals("3 centros de trabajo", resources.getQuantityString(R.plurals.hospital_list_count_subtitle, 3, 3))
    }
}
