package com.raredev.vcspace.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.raredev.vcspace.databinding.LayoutNavigationItemBinding

class NavigationSpace : LinearLayout {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet?,
      defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  fun addItem(title: Int, icon: Int, listener: View.OnClickListener) {
    addItem(context.getString(title), icon, listener)
  }

  fun addItem(title: CharSequence, icon: Int, listener: View.OnClickListener) {
    val binding = LayoutNavigationItemBinding.inflate(LayoutInflater.from(context))
    binding.root.setOnClickListener(listener)
    binding.root.setTooltipText(title)
    binding.icon.setImageResource(icon)
    binding.title.setText(title)
    addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f))
  }
}
