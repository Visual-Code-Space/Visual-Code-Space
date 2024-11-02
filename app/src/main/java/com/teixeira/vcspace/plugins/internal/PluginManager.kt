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

package com.teixeira.vcspace.plugins.internal

import com.google.gson.Gson
import com.teixeira.vcspace.BuildConfig
import com.teixeira.vcspace.ORGANIZATION_NAME
import com.teixeira.vcspace.app.VCSpaceApplication
import com.teixeira.vcspace.extensions.decodeBase64
import com.teixeira.vcspace.extensions.extractZipFile
import com.teixeira.vcspace.extensions.toBase64String
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.extensions.toZipFile
import com.teixeira.vcspace.github.Content
import com.teixeira.vcspace.github.FileCreateRequest
import com.teixeira.vcspace.github.FileCreateResponse
import com.teixeira.vcspace.github.GitHubService
import com.teixeira.vcspace.plugins.Manifest
import com.teixeira.vcspace.plugins.Plugin
import com.teixeira.vcspace.plugins.internal.extensions.loadPlugins
import com.teixeira.vcspace.preferences.pluginsPath
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object PluginManager {
  private const val REPO_NAME = "vcspace-plugins"

  @JvmOverloads
  fun init(application: VCSpaceApplication, onError: (Plugin, Throwable) -> Unit = { _, _ -> }) {
    val plugins = application.loadPlugins()
    plugins.filter { it.manifest.enabled }.forEach { it.start { err -> onError(it, err) } }
  }

  private fun getPlugins(application: VCSpaceApplication) = application.loadPlugins()
  fun getPlugins() = getPlugins(VCSpaceApplication.getInstance())

  suspend fun uploadPlugin(
    plugin: Plugin,
    onSuccess: () -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    val pluginFile = plugin.fullPath.toFile()
    val pluginZip = pluginFile.toZipFile()

    runCatching {
      uploadFileToGithub(
        File(pluginFile, "manifest.json"),
        manifest = plugin.manifest
      )
      uploadFileToGithub(
        file = pluginZip,
        manifest = plugin.manifest
      )
    }.onSuccess {
      onSuccess()
    }.onFailure(onFailure)
  }

  private suspend fun uploadFileToGithub(
    file: File,
    manifest: Manifest
  ): FileCreateResponse {
    val token = BuildConfig.GITHUB_TOKEN
    val service = GitHubService.createGitHubApiService(token)

    val createRequest = FileCreateRequest(
      message = "Upload ${file.name} as a part of ${manifest.name} ${
        if (manifest.name.lowercase().endsWith("plugin")) "" else "plugin"
      }",
      content = file.toBase64String()
    )

    val call = service.createFile(
      owner = ORGANIZATION_NAME,
      repo = REPO_NAME,
      path = "plugins/${manifest.name} - v${manifest.versionName} (${manifest.versionCode})/${file.name}",
      request = createRequest
    )

    return call.awaitResult().getOrThrow()
  }

  suspend fun fetchPluginsFromGithub(
    path: String? = "",
    onSuccess: (List<Content>) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    val token = BuildConfig.GITHUB_TOKEN
    val service = GitHubService.createGitHubApiService(token)

    val call = service.getContents(
      owner = ORGANIZATION_NAME,
      repo = REPO_NAME,
      path = if (path.isNullOrEmpty()) "plugins" else "plugins/$path"
    )

    call.awaitResult().onSuccess(onSuccess).onFailure(onFailure)
  }

  suspend fun getPluginSize(
    path: String,
    onSuccess: (Int) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    val token = BuildConfig.GITHUB_TOKEN
    val service = GitHubService.createGitHubApiService(token)

    val call = service.getContents(
      owner = ORGANIZATION_NAME,
      repo = REPO_NAME,
      path = path
    )

    call.awaitResult().onSuccess {
      val size = it.sumOf { content -> content.size }
      onSuccess(size)
    }.onFailure(onFailure)
  }

  suspend fun downloadPlugin(
    plugin: Content,
    onSuccess: (Plugin) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    val token = BuildConfig.GITHUB_TOKEN
    val service = GitHubService.createGitHubApiService(token)

    val manifestCall = service.getFileContent(
      owner = ORGANIZATION_NAME,
      repo = REPO_NAME,
      path = "${plugin.path}/manifest.json"
    )

    manifestCall.awaitResult().onSuccess { response ->
      val content = response.content.decodeBase64().toString(Charset.defaultCharset())
      val manifest = Gson().fromJson(content, Manifest::class.java)

      val pluginZipCall = service.getFileContent(
        owner = ORGANIZATION_NAME,
        repo = REPO_NAME,
        path = "${plugin.path}/${manifest.packageName}.zip"
      )

      pluginZipCall.awaitResult().onSuccess {
        val outputDir = "$pluginsPath/${manifest.packageName}".toFile()
        outputDir.mkdirs()

        val zipFile = outputDir.resolve("${manifest.packageName}.zip")
        zipFile.writeBytes(it.content.decodeBase64())

        zipFile.extractZipFile(outputDir)
        zipFile.delete()

        onSuccess(
          Plugin(
            fullPath = outputDir.absolutePath,
            manifest = manifest,
            app = VCSpaceApplication.getInstance()
          )
        )
      }.onFailure {
        println("Error: ${it.message}")
        onFailure(it)
      }
    }.onFailure {
      println("Error: ${it.message}")
      onFailure(it)
    }
  }
}

suspend fun <T> Call<T>.awaitResult(): Result<T> {
  return suspendCoroutine { continuation ->
    enqueue(object : Callback<T> {
      override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
          continuation.resume(Result.success(response.body()!!))
        } else {
          continuation.resume(Result.failure(HttpException(response)))
        }
      }

      override fun onFailure(call: Call<T>, t: Throwable) {
        continuation.resumeWithException(t)
      }
    })
  }
}