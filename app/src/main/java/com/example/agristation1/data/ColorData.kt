package com.example.agristation1.data

import androidx.compose.ui.graphics.Color

data class ColorShades(
    val c100: Color,
    val c200: Color,
    val c600: Color,
    val c800: Color
)

object AppColors {

    val green = ColorShades(
        c100 = Color(0xFFDCFCE7),
        c200 = Color(0xFFBBF7D0),
        c600 = Color(0xFF16A34A),
        c800 = Color(0xFF166534)
    )

    val yellow = ColorShades(
        c100 = Color(0xFFFEF9C3),
        c200 = Color(0xFFFDE68A),
        c600 = Color(0xFFCA8A04),
        c800 = Color(0xFF854D0E)
    )

    val red = ColorShades(
        c100 = Color(0xFFFEE2E2),
        c200 = Color(0xFFFECACA),
        c600 = Color(0xFFDC2626),
        c800 = Color(0xFF991B1B)
    )

    val blue = ColorShades(
        c100 = Color(0xFFDBEAFE),
        c200 = Color(0xFFBFDBFE),
        c600 = Color(0xFF2563EB),
        c800 = Color(0xFF1E40AF)
    )

    val purple = ColorShades(
        c100 = Color(0xFFF3E8FF),
        c200 = Color(0xFFE9D5FF),
        c600 = Color(0xFF9333EA),
        c800 = Color(0xFF6B21A8)
    )

    val orange = ColorShades(
        c100 = Color(0xFFFFEDD5),
        c200 = Color(0xFFFED7AA),
        c600 = Color(0xFFEA580C),
        c800 = Color(0xFF9A3412)
    )

    val gray = ColorShades(
        c100 = Color(0xFFF3F4F6),
        c200 = Color(0xFFE5E7EB),
        c600 = Color(0xFF4B5563),
        c800 = Color(0xFF1F2937)
    )
}