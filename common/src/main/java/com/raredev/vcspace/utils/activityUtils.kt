package com.raredev.vcspace.utils

import android.app.Activity
import com.blankj.utilcode.util.ActivityUtils

fun <T> withActivity(action: Activity.() -> T): T {
  return ActivityUtils.getTopActivity()?.let { it.action() }
      ?: run { throw IllegalArgumentException("No activity found!") }
}
