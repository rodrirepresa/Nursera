package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import javax.inject.Inject

class GetRandomHospitalColorUseCaseImpl
    @Inject
    constructor() : GetRandomHospitalColorUseCase {
        override fun invoke(): Int = neoBrutalistColors.random()
    }

private val neoBrutalistColors =
    listOf(
        // Yellows & golds
        0xFFFFE566.toInt(),
        0xFFFFD700.toInt(),
        0xFFF4D738.toInt(),
        0xFFFFEC6E.toInt(),
        0xFFFFF176.toInt(),
        0xFFFFDB4D.toInt(),
        // Reds, pinks & corals
        0xFFFF6B6B.toInt(),
        0xFFFF4D6D.toInt(),
        0xFFFF8FAB.toInt(),
        0xFFFFD6E0.toInt(),
        0xFFFF9AA2.toInt(),
        0xFFFF6F91.toInt(),
        0xFFFF8C94.toInt(),
        0xFFFFB3C1.toInt(),
        0xFFF72585.toInt(),
        0xFFFF5CA1.toInt(),
        // Oranges & peaches
        0xFFFFB347.toInt(),
        0xFFF8D6B3.toInt(),
        0xFFFFCBA4.toInt(),
        0xFFFF9F68.toInt(),
        0xFFFF7F50.toInt(),
        0xFFFFAB76.toInt(),
        0xFFFFD4A3.toInt(),
        // Greens & teals
        0xFFDAF5F0.toInt(),
        0xFF96F2D7.toInt(),
        0xFF9BE8D8.toInt(),
        0xFF98F5E1.toInt(),
        0xFFB5EAD7.toInt(),
        0xFFE2F0CB.toInt(),
        0xFFD4F5A5.toInt(),
        0xFF9EF5A0.toInt(),
        0xFFA8E6CF.toInt(),
        0xFF77DD77.toInt(),
        0xFFB8F2B8.toInt(),
        0xFF56E39F.toInt(),
        0xFF7FFFD4.toInt(),
        0xFF80FFD4.toInt(),
        // Blues & sky
        0xFF98D4F5.toInt(),
        0xFFC7CEEA.toInt(),
        0xFFBBDEFB.toInt(),
        0xFF90CAF9.toInt(),
        0xFFAECBFA.toInt(),
        0xFFA0C4FF.toInt(),
        0xFF74C0FC.toInt(),
        0xFFB3E5FC.toInt(),
        0xFF4FC3F7.toInt(),
        0xFF81D4FA.toInt(),
        // Purples & lavenders
        0xFFFCDFFF.toInt(),
        0xFFC5A3FF.toInt(),
        0xFFE0AAFF.toInt(),
        0xFFD8B4FE.toInt(),
        0xFFCBA4FF.toInt(),
        0xFFBF9FFF.toInt(),
        0xFFE8D5FF.toInt(),
        0xFFDEC9FF.toInt(),
        0xFFF5D0FE.toInt(),
        0xFFEDD5FF.toInt(),
        // Creams, whites & neutrals
        0xFFFDFDF6.toInt(),
        0xFFF5F5F0.toInt(),
        0xFFF5E6CC.toInt(),
        0xFFFFF8DC.toInt(),
        0xFFFFFACD.toInt(),
        0xFFFAF0E6.toInt(),
        0xFFFFF0E6.toInt(),
        0xFFFFE4B5.toInt(),
        0xFFFDF0D5.toInt(),
        // Mints & aquas
        0xFFACFCD9.toInt(),
        0xFF80FFE8.toInt(),
        0xFFAFFFF0.toInt(),
        0xFF72EFDD.toInt(),
        0xFFC3FBE8.toInt(),
        0xFFCEFAD0.toInt(),
    )
