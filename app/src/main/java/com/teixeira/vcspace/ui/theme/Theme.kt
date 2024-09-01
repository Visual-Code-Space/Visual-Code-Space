/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val LightColorScheme = lightColorScheme(
  primary = light_primary,
  onPrimary = light_onPrimary,
  primaryContainer = light_primaryContainer,
  onPrimaryContainer = light_onPrimaryContainer,
  secondary = light_secondary,
  onSecondary = light_onSecondary,
  secondaryContainer = light_secondaryContainer,
  onSecondaryContainer = light_onSecondaryContainer,
  tertiary = light_tertiary,
  onTertiary = light_onTertiary,
  tertiaryContainer = light_tertiaryContainer,
  onTertiaryContainer = light_onTertiaryContainer,
  error = light_error,
  errorContainer = light_errorContainer,
  onError = light_onError,
  onErrorContainer = light_onErrorContainer,
  background = light_background,
  onBackground = light_onBackground,
  surface = light_surface,
  onSurface = light_onSurface,
  surfaceVariant = light_surfaceVariant,
  onSurfaceVariant = light_onSurfaceVariant,
  outline = light_outline,
  inverseOnSurface = light_inverseOnSurface,
  inverseSurface = light_inverseSurface,
  inversePrimary = light_inversePrimary,
  surfaceTint = light_surfaceTint,
  outlineVariant = light_outlineVariant,
  scrim = light_scrim
)

val DarkColorScheme = darkColorScheme(
  primary = dark_primary,
  onPrimary = dark_onPrimary,
  primaryContainer = dark_primaryContainer,
  onPrimaryContainer = dark_onPrimaryContainer,
  secondary = dark_secondary,
  onSecondary = dark_onSecondary,
  secondaryContainer = dark_secondaryContainer,
  onSecondaryContainer = dark_onSecondaryContainer,
  tertiary = dark_tertiary,
  onTertiary = dark_onTertiary,
  tertiaryContainer = dark_tertiaryContainer,
  onTertiaryContainer = dark_onTertiaryContainer,
  error = dark_error,
  errorContainer = dark_errorContainer,
  onError = dark_onError,
  onErrorContainer = dark_onErrorContainer,
  background = dark_background,
  onBackground = dark_onBackground,
  surface = dark_surface,
  onSurface = dark_onSurface,
  surfaceVariant = dark_surfaceVariant,
  onSurfaceVariant = dark_onSurfaceVariant,
  outline = dark_outline,
  inverseOnSurface = dark_inverseOnSurface,
  inverseSurface = dark_inverseSurface,
  inversePrimary = dark_inversePrimary,
  surfaceTint = dark_surfaceTint,
  outlineVariant = dark_outlineVariant,
  scrim = dark_scrim
)

@Composable
fun VCSpaceTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}