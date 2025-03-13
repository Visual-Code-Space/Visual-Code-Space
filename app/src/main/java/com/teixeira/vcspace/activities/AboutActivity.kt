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

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.core.components.common.VCSpaceLargeTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AboutActivity : BaseComposeActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    override fun MainScreen() {
        val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
        val backPressedDispatcher = backPressedDispatcherOwner?.onBackPressedDispatcher

        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                VCSpaceLargeTopBar(
                    title = stringResource(strings.open_source_licences),
                    navigationIcon = {
                        IconButton(onClick = {
                            backPressedDispatcher?.onBackPressed()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val context = LocalContext.current
                val uriHandler = LocalUriHandler.current
                val configuration = LocalConfiguration.current

                val libraries by produceState<Libs?>(null) {
                    value = withContext(Dispatchers.IO) {
                        Libs.Builder().withContext(context).build()
                    }
                }

                var openDialog by remember { mutableStateOf<Library?>(null) }

                LibrariesContainer(
                    libraries = libraries,
                    modifier = Modifier.fillMaxSize(),
                    showDescription = true,
                    onLibraryClick = { library ->
                        val license = library.licenses.firstOrNull()
                        if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                            openDialog = library
                        } else if (!license?.url.isNullOrBlank()) {
                            license?.url?.also {
                                try {
                                    uriHandler.openUri(it)
                                } catch (t: Throwable) {
                                    println("Failed to open url: ${it}")
                                }
                            }
                        }
                    },
                )

                openDialog?.let { library ->
                    AlertDialog(
                        onDismissRequest = {
                            openDialog = null
                        },
                        modifier = Modifier.heightIn(max = configuration.screenHeightDp.dp / 1.2f),
                        text = {
                            FlowRow(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .weight(1f)
                            ) {
                                Text(
                                    text = AnnotatedString.fromHtml(
                                        htmlString = library.licenses.firstOrNull()?.htmlReadyLicenseContent
                                            ?: "Nothing to show"
                                    )
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                openDialog = null
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}
