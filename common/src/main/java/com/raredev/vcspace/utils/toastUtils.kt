package com.raredev.vcspace.utils

import android.content.Context
import com.raredev.vcspace.toaster.Toaster

private val toaster = Toaster()

fun showErrorToast(context: Context, text: CharSequence) {
  showToast(context, text, Toaster.TYPE_ERROR, Toaster.LENGTH_SHORT)
}

fun showAlertToast(context: Context, text: CharSequence) {
  showToast(context, text, Toaster.TYPE_ALERT, Toaster.LENGTH_SHORT)
}

fun showInfoToast(context: Context, text: CharSequence) {
  showToast(context, text, Toaster.TYPE_INFO, Toaster.LENGTH_SHORT)
}

fun showSuccessToast(context: Context, text: CharSequence) {
  showToast(context, text, Toaster.TYPE_SUCCESS, Toaster.LENGTH_SHORT)
}

fun showToast(context: Context, text: CharSequence, type: Int, duration: Int) {
  toaster.apply {
    setType(type)
    setDuration(duration)
    setText(text)
    show(context)
  }
}