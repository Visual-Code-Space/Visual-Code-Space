package com.raredev.vcspace.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.R.attr
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.extensions.getAttrColor
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.Utils

abstract class BaseActivity : AppCompatActivity() {

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

  private fun showRequestPermissionDialog() {
    MaterialAlertDialogBuilder(this)
      .setCancelable(false)
      .setTitle(R.string.file_access_title)
      .setMessage(R.string.file_access_message)
      .setPositiveButton(
        R.string.grant_permission
      ) { _, _ ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          val intent = Intent()
          intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
          intent.data = Uri.fromParts("package", packageName, null)
          startActivity(intent)
        } else {
          ActivityCompat.requestPermissions(
            this,
            arrayOf(
              Manifest.permission.READ_EXTERNAL_STORAGE,
              Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
          )
        }
      }
      .setNegativeButton(
        R.string.exit
      ) { _, _ ->
        finishAffinity()
        System.exit(0)
      }
      .show()
  }
}
