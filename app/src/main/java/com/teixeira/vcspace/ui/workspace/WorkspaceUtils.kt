package com.teixeira.vcspace.ui.workspace

import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.teixeira.vcspace.resources.R

fun NavigationView.configureNavigationViewBackground() {
  val shapeDrawable = background as? MaterialShapeDrawable ?: return
  val corner = context.resources.getDimension(R.dimen.nav_corners)
  shapeDrawable.setShapeAppearanceModel(
    shapeDrawable
      .getShapeAppearanceModel()
      .toBuilder()
      .setTopRightCorner(CornerFamily.ROUNDED, corner)
      .setBottomRightCorner(CornerFamily.ROUNDED, corner)
      .build()
  )
}
