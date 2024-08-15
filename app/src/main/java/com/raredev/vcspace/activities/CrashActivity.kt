package com.raredev.vcspace.activities

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.DeviceUtils
import com.raredev.vcspace.databinding.ActivityCrashBinding
import com.raredev.vcspace.resources.R
import java.util.Calendar
import java.util.Date

class CrashActivity : BaseActivity() {

  companion object {
    const val KEY_EXTRA_ERROR = "error"
  }

  private val onBackPressedCallback =
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        finishAffinity()
      }
    }

  private var _binding: ActivityCrashBinding? = null
  private val binding: ActivityCrashBinding
    get() = checkNotNull(_binding)

  override fun getLayout(): View {
    _binding = ActivityCrashBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)

    val error = StringBuilder()
    error.append("Manufacturer: ${DeviceUtils.getManufacturer()}\n")
    error.append("Device: ${DeviceUtils.getModel()}\n")
    error.append("${getSoftwareInfo()}\n")
    error.append("${getDate()}\n\n")
    error.append(intent.getStringExtra(KEY_EXTRA_ERROR))

    error.toString().also { binding.result.text = it }

    binding.fab.setOnClickListener { ClipboardUtils.copyText(binding.result.text) }

    onBackPressedDispatcher.addCallback(onBackPressedCallback)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val close = menu.add(R.string.close_app)
    close.setIcon(R.drawable.ic_close)
    close.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.title) {
      getString(R.string.close_app) -> finishAffinity()
    }
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  private fun getSoftwareInfo(): String {
    return StringBuilder("SDK: ")
      .append(Build.VERSION.SDK_INT)
      .append("\n")
      .append("Android: ")
      .append(Build.VERSION.RELEASE)
      .append("\n")
      .append("Model: ")
      .append(Build.VERSION.INCREMENTAL)
      .append("\n")
      .toString()
  }

  private fun getDate(): Date {
    return Calendar.getInstance().time
  }
}
