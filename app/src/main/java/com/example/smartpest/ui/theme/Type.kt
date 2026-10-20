package com.example.smartpest.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun body(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = FontFamily.SansSerif, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 52.sp, lineHeight = 58.sp, letterSpacing = (-1.5).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 36.sp, lineHeight = 42.sp, letterSpacing = (-0.8).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 29.sp, lineHeight = 35.sp, letterSpacing = (-0.4).sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontSize = 25.sp, lineHeight = 31.sp),
    titleLarge = body(22, 28, FontWeight.SemiBold),
    titleMedium = body(18, 25, FontWeight.SemiBold),
    titleSmall = body(15, 21, FontWeight.SemiBold),
    bodyLarge = body(16, 25), bodyMedium = body(14, 22), bodySmall = body(12, 18),
    labelLarge = body(13, 19, FontWeight.SemiBold).copy(letterSpacing = 0.4.sp),
    labelMedium = body(12, 17, FontWeight.Medium), labelSmall = body(11, 16, FontWeight.Medium)
)
