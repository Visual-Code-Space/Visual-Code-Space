package com.teixeira.vcspace.preferences

import android.content.Context
import android.util.AttributeSet
import com.teixeira.vcspace.preferences.base.SingleChoiceDialogPreference
import com.teixeira.vcspace.preferences.base.SliderDialogPreference
import com.teixeira.vcspace.resources.R

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
    defStyleRes: Int,
  ) : super(context, attrs, defStyleAttr, defStyleRes)

  override val defaultValue = 14f
  override val valueFrom = 8.0f
  override val valueTo = 27.0f
  override val stepSize = 1.0f
}

class EditorIndentPreference : SingleChoiceDialogPreference {

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

  override val defaultValue = 1
  override val choiceItems = arrayOf("2", "4", "6", "8")
}

class EditorFontPreference : SingleChoiceDialogPreference {

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
      context.getString(R.string.pref_editor_font_value_firacode),
      context.getString(R.string.pref_editor_font_value_jetbrains),
    )
}

class EditorColorSchemePreference : SingleChoiceDialogPreference {

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
      context.getString(R.string.pref_editor_colorscheme_value_followui),
      "Quietlight",
      "Darcula",
      "Abyss",
      "Solarized Dark",
      "Python Dark Mode",
      // context.getString(R.string.pref_editor_colorscheme_value_custom)
    )
}
