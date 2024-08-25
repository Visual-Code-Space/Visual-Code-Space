package com.teixeira.vcspace.activities

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.DeviceUtils
import com.teixeira.vcspace.BuildConfig
import com.teixeira.vcspace.databinding.ActivityCrashBinding
import com.teixeira.vcspace.resources.R
import java.util.Calendar
import java.util.Date

class CrashActivity : BaseActivity() {

  companion object {
    const val KEY_EXTRA_ERROR = "key_extra_error"
  }

  private var _binding: ActivityCrashBinding? = null
  private val binding: ActivityCrashBinding
    get() = checkNotNull(_binding)

  private val softwareInfo: String
    get() =
      StringBuilder("Manufacturer: ")
        .append(DeviceUtils.getManufacturer())
        .append("\n")
        .append("Device: ")
        .append(DeviceUtils.getModel())
        .append("\n")
        .append("SDK: ")
        .append(Build.VERSION.SDK_INT)
        .append("\n")
        .append("Android: ")
        .append(Build.VERSION.RELEASE)
        .append("\n")
        .append("Model: ")
        .append(Build.VERSION.INCREMENTAL)
        .append("\n")
        .toString()

  private val appInfo: String
    get() =
      StringBuilder("Version: ")
        .append(BuildConfig.VERSION_NAME)
        .append("\n")
        .append("Build: ")
        .append(BuildConfig.BUILD_TYPE)
        .toString()

  private val date: Date
    get() = Calendar.getInstance().time

  override fun getLayout(): View {
    _binding = ActivityCrashBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)

    onBackPressedDispatcher.addCallback(
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          finishAffinity()
        }
      }
    )

    binding.fab.setOnClickListener { ClipboardUtils.copyText(binding.result.text) }
    binding.result.text =
      StringBuilder()
        .append("$softwareInfo\n")
        .append("$appInfo\n\n")
        .append("$date\n\n")
        .append(intent.getStringExtra(KEY_EXTRA_ERROR))
        .toString()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menu.add(0, 0, 0, R.string.close_app).apply {
      setIcon(R.drawable.ic_close)
      setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      0 -> finishAffinity()
    }
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}
