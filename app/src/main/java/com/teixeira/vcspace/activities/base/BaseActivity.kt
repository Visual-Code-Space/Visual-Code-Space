package com.teixeira.vcspace.activities.base

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.R.attr
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.app.VCSpaceApplication
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.getAttrColor
import com.teixeira.vcspace.utils.isStoragePermissionGranted
import kotlin.system.exitProcess

abstract class BaseActivity : AppCompatActivity() {

    private val permissionLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (!isStoragePermissionGranted()) showRequestPermissionDialog()
        }

    protected open val navigationBarDividerColor: Int
        get() = getAttrColor(attr.colorSurface)

    protected open val navigationBarColor: Int
        get() = getAttrColor(attr.colorSurface)

    protected open val statusBarColor: Int
        get() = getAttrColor(attr.colorSurface)

    protected val app: VCSpaceApplication
        get() = BaseApplication.instance as VCSpaceApplication

    protected abstract fun getLayout(): View

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.apply {
            this.statusBarColor = this@BaseActivity.statusBarColor
            this.navigationBarColor = this@BaseActivity.navigationBarColor
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                this.navigationBarDividerColor = this@BaseActivity.navigationBarDividerColor
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(getLayout())

        if (!isStoragePermissionGranted()) showRequestPermissionDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQCODE_STORAGE) {
            if (!isStoragePermissionGranted()) showRequestPermissionDialog()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun showRequestPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(R.string.file_storage_access)
            .setMessage(R.string.file_storage_access_message)
            .setPositiveButton(R.string.file_storage_access_grant) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val uri = Uri.parse("package:$packageName")
                    permissionLauncher.launch(
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            permission.READ_EXTERNAL_STORAGE,
                            permission.WRITE_EXTERNAL_STORAGE
                        ),
                        REQCODE_STORAGE,
                    )
                }
            }
            .setNegativeButton(R.string.close) { _, _ ->
                finishAffinity()
                exitProcess(0)
            }
            .show()
    }

    companion object {
        const val REQCODE_STORAGE = 1009
    }
}
