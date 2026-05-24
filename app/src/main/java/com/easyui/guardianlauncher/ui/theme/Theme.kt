package com.easyui.guardianlauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.easyui.guardianlauncher.data.Mode

@Composable
fun GuardianLauncherTheme(
    mode: Mode,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (mode) {
        Mode.HOME, Mode.TRAVEL -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = HomePrimary,
                    secondary = HomeSecondary,
                    tertiary = HomeTertiary,
                    background = HomeBackgroundDark,
                    surface = CardBackgroundDark,
                    onPrimary = CardBackgroundLight,
                    onBackground = LightGrey,
                    onSurface = LightGrey
                )
            } else {
                lightColorScheme(
                    primary = HomePrimary,
                    secondary = HomeSecondary,
                    tertiary = HomeTertiary,
                    background = HomeBackgroundLight,
                    surface = CardBackgroundLight,
                    onPrimary = CardBackgroundLight,
                    onBackground = DarkGrey,
                    onSurface = DarkGrey
                )
            }
        }
        Mode.SCHOOL, Mode.EXAM -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = SchoolPrimary,
                    secondary = SchoolSecondary,
                    tertiary = SchoolTertiary,
                    background = SchoolBackgroundDark,
                    surface = CardBackgroundDark,
                    onPrimary = CardBackgroundLight,
                    onBackground = LightGrey,
                    onSurface = LightGrey
                )
            } else {
                lightColorScheme(
                    primary = SchoolPrimary,
                    secondary = SchoolSecondary,
                    tertiary = SchoolTertiary,
                    background = SchoolBackgroundLight,
                    surface = CardBackgroundLight,
                    onPrimary = CardBackgroundLight,
                    onBackground = DarkGrey,
                    onSurface = DarkGrey
                )
            }
        }
        Mode.SLEEP, Mode.BEDTIME -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = SleepPrimary,
                    secondary = SleepSecondary,
                    tertiary = SleepTertiary,
                    background = SleepBackgroundDark,
                    surface = CardBackgroundDark,
                    onPrimary = CardBackgroundLight,
                    onBackground = LightGrey,
                    onSurface = LightGrey
                )
            } else {
                lightColorScheme(
                    primary = SleepPrimary,
                    secondary = SleepSecondary,
                    tertiary = SleepTertiary,
                    background = SleepBackgroundLight,
                    surface = CardBackgroundLight,
                    onPrimary = CardBackgroundLight,
                    onBackground = DarkGrey,
                    onSurface = DarkGrey
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
