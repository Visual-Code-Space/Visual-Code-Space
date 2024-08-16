package com.raredev.vcspace.preferences

import android.content.Context
import android.util.AttributeSet
import com.raredev.vcspace.resources.R
import com.raredev.vcspace.preferences.base.ChoiceDialogPreference
import com.raredev.vcspace.preferences.base.SliderDialogPreference

class EditorFontSizePreference : SliderDialogPreference {

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

  override val defaultValue = 14f
  override val valueFrom = 8.0f
  override val valueTo = 27.0f
  override val stepSize = 1.0f
}

class EditorIndentPreference : ChoiceDialogPreference {

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

  override val defaultValue = 1
  override val choiceItems = arrayOf("2", "4", "6", "8")
}

class EditorFontPreference : ChoiceDialogPreference {

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
      context.getString(R.string.pref_editor_font_value_firacode),
      context.getString(R.string.pref_editor_font_value_jetbrains)
    )
}
