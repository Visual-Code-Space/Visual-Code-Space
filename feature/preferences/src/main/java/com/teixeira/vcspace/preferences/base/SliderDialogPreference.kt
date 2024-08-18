package com.teixeira.vcspace.preferences.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teixeira.vcspace.preferences.databinding.LayoutMaterialSliderBinding
import com.teixeira.vcspace.resources.R

abstract class SliderDialogPreference : Preference {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
  ) : super(context, attrs, defStyleAttr)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes)

  abstract val defaultValue: Float
  abstract val valueFrom: Float
  abstract val valueTo: Float
  abstract val stepSize: Float

  override fun onClick() {
    super.onClick()

    val editor = sharedPreferences?.edit() ?: return
    val value = sharedPreferences!!.getFloat(key, defaultValue)

    MaterialAlertDialogBuilder(context).apply {
      val binding = LayoutMaterialSliderBinding.inflate(LayoutInflater.from(context))
      binding.apply {
        slider.valueFrom = valueFrom
        slider.valueTo = valueTo
        slider.value = value
        slider.stepSize = stepSize
      }

      setTitle(title)
      setMessage(summary)
      setView(binding.root)

      setNegativeButton(R.string.cancel, null)
      setPositiveButton(R.string.save) { _, _ ->
        editor.putFloat(key, binding.slider.value).apply()
        onSave()
      }
      show()
    }
  }

  protected open fun onSave() {}
}
