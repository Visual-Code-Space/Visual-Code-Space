package com.teixeira.vcspace.preferences.base

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teixeira.vcspace.resources.R

abstract class SingleChoiceDialogPreference : Preference {

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

  abstract val defaultValue: Int
  abstract val choiceItems: Array<String>

  override fun onClick() {
    super.onClick()

    val editor = sharedPreferences?.edit() ?: return
    val value = sharedPreferences!!.getInt(key, defaultValue)

    MaterialAlertDialogBuilder(context).apply {
      setTitle(title)

      setSingleChoiceItems(choiceItems, value) { _, w ->
        editor.putInt(key, w)
        onChoose(w)
      }

      setNegativeButton(R.string.cancel, null)
      setPositiveButton(R.string.save) { _, _ ->
        editor.apply()
        onSave()
      }
      show()
    }
  }

  protected open fun onChoose(value: Int) {}

  protected open fun onSave() {}
}
