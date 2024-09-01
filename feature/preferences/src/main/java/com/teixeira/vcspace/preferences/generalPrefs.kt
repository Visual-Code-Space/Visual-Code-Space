package com.teixeira.vcspace.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatDelegate
import com.teixeira.vcspace.preferences.base.SingleChoiceDialogPreference
import com.teixeira.vcspace.resources.R

class UIModePreference : SingleChoiceDialogPreference {

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
    defStyleRes: Int,
  ) : super(context, attrs, defStyleAttr, defStyleRes)

  override val defaultValue = 0
  override val choiceItems =
    arrayOf(
      context.getString(R.string.pref_aparence_uimode_value_followsys),
      context.getString(R.string.pref_aparence_uimode_value_light),
      context.getString(R.string.pref_aparence_uimode_value_dark),
    )

  override fun onSave() {
    AppCompatDelegate.setDefaultNightMode(appearanceUIMode)
  }
}
