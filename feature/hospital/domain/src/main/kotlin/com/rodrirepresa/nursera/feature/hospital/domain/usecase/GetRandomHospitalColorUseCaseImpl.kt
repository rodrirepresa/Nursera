package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import javax.inject.Inject

class GetRandomHospitalColorUseCaseImpl
    @Inject
    constructor() : GetRandomHospitalColorUseCase {
        override fun invoke(): Int = neoBrutalistColors.random()
    }

private val neoBrutalistColors =
    listOf(
        // Yellows
        0xFFFFE566.toInt(),
        0xFFFFB347.toInt(),
        // Reds & pinks
        0xFFFF6B6B.toInt(),
        0xFFFF8FAB.toInt(),
        0xFFFFD6E0.toInt(),
        // Greens
        0xFF56E39F.toInt(),
        0xFFB5EAD7.toInt(),
        0xFFDAF5F0.toInt(),
        // Blues
        0xFF4FC3F7.toInt(),
        0xFF90CAF9.toInt(),
        0xFFC7CEEA.toInt(),
        // Purples
        0xFFC5A3FF.toInt(),
        0xFFE8D5FF.toInt(),
        0xFFFCDFFF.toInt(),
        // Peach & coral
        0xFFFFCBA4.toInt(),
        0xFFFF7F50.toInt(),
    )
