package com.raredev.common.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class Utils {

    public static boolean isPermissionGaranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    public static void copyText(Context context, String text) {
        ((ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("path", text));
    }
    
    public static void dialogError(Context ctx, String text) {
        new AlertDialog.Builder(ctx)
            .setTitle("Error")
            .setMessage(text)
            .setPositiveButton("Ok", null)
            .show();
    }

    public static void showToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}
