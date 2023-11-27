package com.raredev.vcspace.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.R.attr
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.extensions.getAttrColor
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.Utils
import kotlin.system.exitProcess

abstract class BaseActivity : AppCompatActivity() {

  val permissionLauncher =
    registerForActivityResult(StartActivityForResult()) {
      if (!Environment.isExternalStorageManager()) showRequestPermissionDialog()
    }

  open val navigationBarColor: Int
    get() = getAttrColor(attr.colorSurface)

  open val statusBarColor: Int
    get() = getAttrColor(attr.colorSurface)

  protected abstract fun getLayout(): View

  override fun onCreate(savedInstanceState: Bundle?) {
    window?.apply {
      navigationBarColor = this@BaseActivity.navigationBarColor
      statusBarColor = this@BaseActivity.statusBarColor
    }
    super.onCreate(savedInstanceState)
    setContentView(getLayout())

    if (!Utils.isPermissionGaranted(this)) showRequestPermissionDialog()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQCODE_STORAGE) {
      if (!Utils.isPermissionGaranted(this)) showRequestPermissionDialog()
    }
  }

  private fun showRequestPermissionDialog() {
    MaterialAlertDialogBuilder(this)
      .setCancelable(false)
      .setTitle(R.string.file_access_title)
      .setMessage(R.string.file_access_message)
      .setPositiveButton(
        R.string.grant_permission,
        { _, _ ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val uri = Uri.parse("package:$packageName")
            permissionLauncher.launch(
              Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
            )
          } else {
            ActivityCompat.requestPermissions(
              this,
              arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
              ),
              REQCODE_STORAGE
            )
          }
        }
      )
      .setNegativeButton(
        R.string.exit,
        { _, _ ->
          finishAffinity()
          exitProcess(0)
        }
      )
      .show()
  }

  companion object {
    const val REQCODE_STORAGE = 1009
  }
}
