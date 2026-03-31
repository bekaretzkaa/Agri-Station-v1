package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Если у тебя нет кастомных шрифтов — оставляем Default.
private val AppFontFamily = FontFamily.Default

val AppTypography = Typography(
    // Title: "GreenField Farm"
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),

    // Card titles / section headings
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),

    // Primary body
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Small / meta text: dates, "Last updated"
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Labels in bottom nav
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
