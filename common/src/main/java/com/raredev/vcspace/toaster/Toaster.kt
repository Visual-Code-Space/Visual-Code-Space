package com.raredev.vcspace.toaster

import android.content.Context
import android.view.View
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.Gravity
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.R.attr
import com.raredev.vcspace.res.R
import com.raredev.vcspace.res.databinding.LayoutToastBinding
import java.lang.ref.WeakReference

class Toaster {

  private var lastToast: WeakReference<ToastWindow>? = null

  private var type = TYPE_SUCCESS
  private var duration = LENGTH_SHORT
  private var text: CharSequence? = null

  fun setType(type: Int): Toaster {
    this.type = type
    return this
  }

  fun setDuration(duration: Int): Toaster {
    this.duration = duration
    return this
  }

  fun setText(text: CharSequence): Toaster {
    this.text = text
    return this
  }

  fun show(context: Context) {
    lastToast?.get()?.cancel()

    val toast = ToastWindow(context)
    toast.show()

    lastToast = WeakReference<ToastWindow>(toast)
  }

  private fun createToastView(toast: ToastWindow): View {
    val binding = LayoutToastBinding.inflate(LayoutInflater.from(toast.context))
    val iconResId = when (type) {
      TYPE_ERROR -> R.drawable.ic_error
      TYPE_ALERT -> R.drawable.ic_alert
      TYPE_INFO -> R.drawable.ic_info
      TYPE_SUCCESS -> R.drawable.ic_check
      else -> R.drawable.ic_error
    }

    binding.icon.setImageResource(iconResId)
    binding.text.text = text
    binding.root.setOnClickListener { toast.cancel() }
    binding.root.setBackgroundResource(R.drawable.toast_bg)
    return binding.root
  }

  inner class ToastWindow(val context: Context) {
    private var windowManager: WindowManager? = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var toastView: View? = createToastView(this)

    private val params = WindowManager.LayoutParams()

    fun show() {
      params.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW
      params.height = WindowManager.LayoutParams.WRAP_CONTENT
      params.width = WindowManager.LayoutParams.WRAP_CONTENT
      params.format = PixelFormat.TRANSLUCENT
      params.windowAnimations = android.R.style.Animation_Toast

      params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
      params.y = 150

      params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      params.packageName = context.getPackageName()

      try {
        windowManager?.addView(toastView!!, params)
      } catch (e: Exception) {
        e.printStackTrace()
      }
      ThreadUtils.runOnUiThreadDelayed({ cancel() }, if (duration == LENGTH_SHORT) 2000 else 3500)
    }

    fun cancel() {
      try {
        windowManager?.removeViewImmediate(toastView!!)
        windowManager = null
        toastView = null
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  companion object {
    const val TYPE_ERROR = 0
    const val TYPE_ALERT = 1
    const val TYPE_INFO = 2
    const val TYPE_SUCCESS = 3

    const val LENGTH_SHORT = 0
    const val LENGTH_LONG = 1
  }
}
