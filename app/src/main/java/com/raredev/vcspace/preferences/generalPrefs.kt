package com.raredev.vcspace.preferences

import android.content.Context
import android.util.AttributeSet
import com.raredev.vcspace.resources.R
import com.raredev.vcspace.preferences.base.ChoiceDialogPreference
import androidx.appcompat.app.AppCompatDelegate

class UIModePreference : ChoiceDialogPreference {

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

  override val defaultValue = 0
  override val choiceItems =
    arrayOf(
      context.getString(R.string.pref_aparence_uimode_value_followsys),
      context.getString(R.string.pref_aparence_uimode_value_light),
      context.getString(R.string.pref_aparence_uimode_value_dark)
    )

  override fun onSave() {
    AppCompatDelegate.setDefaultNightMode(aparenceUIMode)
  }
}
