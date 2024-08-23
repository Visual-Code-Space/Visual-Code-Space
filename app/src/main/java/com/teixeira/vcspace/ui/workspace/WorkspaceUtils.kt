package com.teixeira.vcspace.ui.workspace

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.getAttrColor

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

fun NavigationRailView.configureNavigationRailBackground() {
  val shapeDrawable = background as? MaterialShapeDrawable ?: return
  val corner = context.resources.getDimension(R.dimen.nav_corners)
  shapeDrawable.setColorFilter(
    PorterDuffColorFilter(
      context.getAttrColor(com.google.android.material.R.attr.colorPrimaryContainer),
      PorterDuff.Mode.SRC_ATOP,
    )
  )
  shapeDrawable.setShapeAppearanceModel(
    shapeDrawable
      .getShapeAppearanceModel()
      .toBuilder()
      .setTopRightCorner(CornerFamily.ROUNDED, corner)
      .build()
  )
}
