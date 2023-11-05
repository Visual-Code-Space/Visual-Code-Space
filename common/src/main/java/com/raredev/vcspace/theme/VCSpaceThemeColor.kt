package com.raredev.vcspce.theme

import com.raredev.vcspace.utils.Utils

class VCSpaceTheme {
  enum class ThemeColor {
    PRIMARY,
    ON_PRIMARY,
    PRIMARY_CONTAINER,
    ON_PRIMARY_CONTAINER,
    SECONDARY,
    ON_SECONDARY,
    SECONDARY_CONTAINER,
    ON_SECONDARY_CONTAINER,
    TERTIARY,
    ON_TERTIARY,
    TERTIARY_CONTAINER,
    ON_TERTIARY_CONTAINER,
    ERROR,
    ERROR_CONTAINER,
    ON_ERROR,
    ON_ERROR_CONTAINER,
    BACKGROUND,
    ON_BACKGROUND,
    SURFACE,
    ON_SURFACE,
    SURFACE_VARIANT,
    ON_SURFACE_VARIANT,
    OUTLINE,
    INVERSE_ON_SURFACE,
    INVERSE_SURFACE,
    INVERSE_PRIMARY,
    SHADOW,
    SURFACE_TINT,
    OUTLINE_VARIANT,
    SCRIM
  }

  companion object {
    fun getColor(color: ThemeColor): Int {
      return if (Utils.isDarkMode()) {
        when (color) {
          ThemeColor.PRIMARY -> 0xFF4FD8EB.toInt()
          ThemeColor.ON_PRIMARY -> 0xFF00363D.toInt()
          ThemeColor.PRIMARY_CONTAINER -> 0xFF004F58.toInt()
          ThemeColor.ON_PRIMARY_CONTAINER -> 0xFF97F0FF.toInt()
          ThemeColor.SECONDARY -> 0xFFB1CBD0.toInt()
          ThemeColor.ON_SECONDARY -> 0xFF1C3438.toInt()
          ThemeColor.SECONDARY_CONTAINER -> 0xFF334B4F.toInt()
          ThemeColor.ON_SECONDARY_CONTAINER -> 0xFFCDE7EC.toInt()
          ThemeColor.TERTIARY -> 0xFFCDE7EC.toInt()
          ThemeColor.ON_TERTIARY -> 0xFF24304D.toInt()
          ThemeColor.TERTIARY_CONTAINER -> 0xFF3B4664.toInt()
          ThemeColor.ON_TERTIARY_CONTAINER -> 0xFFDAE2FF.toInt()
          ThemeColor.ERROR -> 0xFFFFB4AB.toInt()
          ThemeColor.ERROR_CONTAINER -> 0xFF93000A.toInt()
          ThemeColor.ON_ERROR -> 0xFF690005.toInt()
          ThemeColor.ON_ERROR_CONTAINER -> 0xFFFFDAD6.toInt()
          ThemeColor.BACKGROUND -> 0xFF191C1D.toInt()
          ThemeColor.ON_BACKGROUND -> 0xFFE1E3E3.toInt()
          ThemeColor.SURFACE -> 0xFF191C1D.toInt()
          ThemeColor.ON_SURFACE -> 0xFFE1E3E3.toInt()
          ThemeColor.SURFACE_VARIANT -> 0xFF3F484A.toInt()
          ThemeColor.ON_SURFACE_VARIANT -> 0xFFBFC8CA.toInt()
          ThemeColor.OUTLINE -> 0xFF899294.toInt()
          ThemeColor.INVERSE_ON_SURFACE -> 0xFF191C1D.toInt()
          ThemeColor.INVERSE_SURFACE -> 0xFFE1E3E3.toInt()
          ThemeColor.INVERSE_PRIMARY -> 0xFF006874.toInt()
          ThemeColor.SHADOW -> 0xFF000000.toInt()
          ThemeColor.SURFACE_TINT -> 0xFF4FD8EB.toInt()
          ThemeColor.OUTLINE_VARIANT -> 0xFF3F484A.toInt()
          ThemeColor.SCRIM -> 0xFF000000.toInt()
        }
      } else {
        when (color) {
          ThemeColor.PRIMARY -> 0xFF006874.toInt()
          ThemeColor.ON_PRIMARY -> 0xFFFFFFFF.toInt()
          ThemeColor.PRIMARY_CONTAINER -> 0xFF97F0FF.toInt()
          ThemeColor.ON_PRIMARY_CONTAINER -> 0xFF001F24.toInt()
          ThemeColor.SECONDARY -> 0xFF4A6267.toInt()
          ThemeColor.ON_SECONDARY -> 0xFFFFFFFF.toInt()
          ThemeColor.SECONDARY_CONTAINER -> 0xFFCDE7EC.toInt()
          ThemeColor.ON_SECONDARY_CONTAINER -> 0xFF051F23.toInt()
          ThemeColor.TERTIARY -> 0xFF525E7D.toInt()
          ThemeColor.ON_TERTIARY -> 0xFFFFFFFF.toInt()
          ThemeColor.TERTIARY_CONTAINER -> 0xFFDAE2FF.toInt()
          ThemeColor.ON_TERTIARY_CONTAINER -> 0xFF0E1B37.toInt()
          ThemeColor.ERROR -> 0xFFBA1A1A.toInt()
          ThemeColor.ERROR_CONTAINER -> 0xFFFFDAD6.toInt()
          ThemeColor.ON_ERROR -> 0xFFFFFFFF.toInt()
          ThemeColor.ON_ERROR_CONTAINER -> 0xFF410002.toInt()
          ThemeColor.BACKGROUND -> 0xFFFAFDFD.toInt()
          ThemeColor.ON_BACKGROUND -> 0xFF191C1D.toInt()
          ThemeColor.SURFACE -> 0xFFFAFDFD.toInt()
          ThemeColor.ON_SURFACE -> 0xFF191C1D.toInt()
          ThemeColor.SURFACE_VARIANT -> 0xFFDBE4E6.toInt()
          ThemeColor.ON_SURFACE_VARIANT -> 0xFF3F484A.toInt()
          ThemeColor.OUTLINE -> 0xFF6F797A.toInt()
          ThemeColor.INVERSE_ON_SURFACE -> 0xFFEFF1F1.toInt()
          ThemeColor.INVERSE_SURFACE -> 0xFF2E3132.toInt()
          ThemeColor.INVERSE_PRIMARY -> 0xFF4FD8EB.toInt()
          ThemeColor.SHADOW -> 0xFF000000.toInt()
          ThemeColor.SURFACE_TINT -> 0xFF006874.toInt()
          ThemeColor.OUTLINE_VARIANT -> 0xFFBFC8CA.toInt()
          ThemeColor.SCRIM -> 0xFF000000.toInt()
        }
      }
    }
  }
}
