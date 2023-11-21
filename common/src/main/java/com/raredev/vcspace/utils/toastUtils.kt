package com.raredev.vcspace.utils

import android.content.Context
import android.widget.Toast

fun showShortToast(context: Context, text: CharSequence) {
  Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context, text: CharSequence) {
  Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}
