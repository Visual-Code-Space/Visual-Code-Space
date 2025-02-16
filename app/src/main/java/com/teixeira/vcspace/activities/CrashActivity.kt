/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.activities

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.DeviceUtils
import com.teixeira.vcspace.BuildConfig
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.ui.ToastHost
import com.teixeira.vcspace.ui.rememberToastHostState
import com.teixeira.vcspace.ui.screens.crash.CrashScreen
import com.teixeira.vcspace.ui.screens.crash.components.CrashTopBar
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class CrashActivity : BaseComposeActivity() {
    companion object {
        const val KEY_EXTRA_ERROR = "key_extra_error"
    }

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun MainScreen() {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = rememberTopAppBarState()
        )
        val uriHandler = LocalUriHandler.current

        val errorString = buildString {
            append("$softwareInfo\n")
            append("$appInfo\n\n")
            append("$date\n\n")
            append(intent.getStringExtra(KEY_EXTRA_ERROR))
        }
        val error by remember { mutableStateOf(errorString) }
        val scope = rememberCoroutineScope()
        val toastHostState = rememberToastHostState()

        val appCrashedMessage = stringResource(strings.app_crashed)
        SideEffect {
            scope.launch {
                toastHostState.showToast(
                    message = appCrashedMessage,
                    icon = Icons.Outlined.ErrorOutline
                )
            }
        }

        val copiedMessage = stringResource(strings.copied_to_clipboard)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CrashTopBar(
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        ClipboardUtils.copyText(error).also {
                            scope.launch {
                                toastHostState.showToast(
                                    message = copiedMessage,
                                    icon = Icons.Outlined.ContentCopy
                                )
                            }
                        }

                        uriHandler.openUri("https://github.com/Visual-Code-Space/Visual-Code-Space/issues/new?assignees=&labels=bug&projects=&template=bug_report.md&title=")
                    },
                    text = {
                        Text(stringResource(strings.copy_and_report))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = stringResource(strings.copy)
                        )
                    }
                )
            }
        ) { innerPadding ->
            ToastHost(hostState = toastHostState)

            CrashScreen(
                modifier = Modifier.padding(innerPadding),
                error = error,
            )

            BackHandler(onBack = AppUtils::exitApp)
        }
    }
}