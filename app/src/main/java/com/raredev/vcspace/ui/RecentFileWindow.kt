package com.raredev.vcspace.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.PopupWindow
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import com.raredev.vcspace.databinding.LayoutRecentFilesBinding

class RecentFileWindow(val context: Context) {
  private lateinit var window: PopupWindow

  private val binding = LayoutRecentFilesBinding.inflate(LayoutInflater.from(context))

  init {
    window =
        PopupWindow(context).apply {
          height = WindowManager.LayoutParams.WRAP_CONTENT
          width = WindowManager.LayoutParams.MATCH_PARENT
          isFocusable = true
          setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
          elevation = 5F
          contentView = binding.root
        }
    applyBackground()
    binding.root.setOnTouchListener { v, event ->
      if (event.action == MotionEvent.ACTION_OUTSIDE) {
        window.dismiss()
        true
      } else false
    }
  }

  fun applyBackground() {
    val drawable =
        GradientDrawable().apply {
          shape = GradientDrawable.RECTANGLE
          cornerRadius = SizeUtils.px2dp(10F).toFloat()
          setColor(MaterialColors.getColor(context, R.attr.colorSurface, 0))
          setStroke(1, MaterialColors.getColor(context, R.attr.colorOutline, 0))
        }
    binding.root.background = drawable
  }

  fun show() {
    window.showAtLocation(binding.root, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
  }
}
