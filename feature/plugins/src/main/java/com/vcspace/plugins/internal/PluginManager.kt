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

package com.vcspace.plugins.internal

import android.app.Application
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.extensions.toBase64String
import com.teixeira.vcspace.extensions.toFile
import com.vcspace.plugins.BuildConfig
import com.vcspace.plugins.Manifest
import com.vcspace.plugins.Plugin
import com.vcspace.plugins.Script
import com.vcspace.plugins.internal.distribution.GitHubService
import com.vcspace.plugins.internal.distribution.github.Content
import com.vcspace.plugins.internal.distribution.github.FileCreateRequest
import com.vcspace.plugins.internal.distribution.github.FileCreateResponse
import com.vcspace.plugins.internal.extensions.loadPlugins
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

object PluginManager {
  private const val ORGANIZATION_NAME = "Visual-Code-Space"
  private const val REPO_NAME = "vcspace-plugins"

  @JvmOverloads
  fun init(application: Application, onError: (Plugin, Exception) -> Unit = { _, _ -> }) {
    val plugins = application.loadPlugins()
    plugins.filter { it.manifest.enabled }.forEach { it.start { err -> onError(it, err) } }
  }

  fun getPlugins(application: Application) = application.loadPlugins()
  fun getPlugins() = getPlugins(BaseApplication.instance)

  fun uploadPlugin(plugin: Plugin, callback: (Boolean, String?) -> Unit) {
    val pluginFile = plugin.fullPath.toFile()

    uploadFileToGithub(
      File(pluginFile, "manifest.json"),
      plugin.manifest
    ) { manifestSuccess, manifestError ->
      if (manifestSuccess) {
        fun uploadScriptsSequentially(scripts: List<Script>, index: Int = 0) {
          if (index < scripts.size) {
            val script = scripts[index]
            uploadFileToGithub(
              File(pluginFile, script.name),
              plugin.manifest
            ) { scriptSuccess, scriptError ->
              if (scriptSuccess) {
                uploadScriptsSequentially(scripts, index + 1)
              } else {
                callback(false, "Failed to upload script: ${script.name}. Error: $scriptError")
              }
            }
          } else {
            callback(true, null)
          }
        }

        uploadScriptsSequentially(plugin.manifest.scripts.toList())
      } else {
        callback(false, "Failed to upload manifest. Error: $manifestError")
      }
    }
  }

  private fun uploadFileToGithub(
    file: File,
    manifest: Manifest,
    callback: (Boolean, String?) -> Unit
  ) {
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
      path = "plugins/${manifest.name} - ${manifest.versionName} (${manifest.versionCode})/${file.name}",
      request = createRequest
    )
    call.enqueue(object : Callback<FileCreateResponse> {
      override fun onResponse(
        call: Call<FileCreateResponse>,
        response: Response<FileCreateResponse>
      ) {
        if (response.isSuccessful) {
          println("File uploaded (${file.name}): ${response.body()?.commit?.sha}")
          callback(true, response.message())
        } else {
          println("Failed to upload file (${file.name}): ${response.code()} ${response.message()}")
          response.errorBody()?.string()?.let { errorBody ->
            println("Error Body: $errorBody")
            callback(false, errorBody)
          } ?: callback(false, "Unknown error")
        }
      }

      override fun onFailure(call: Call<FileCreateResponse>, t: Throwable) {
        println("Error uploading file (${file.name}): ${t.message}")
        callback(false, t.message)
      }
    })
  }

  fun fetchPluginsFromGithub(path: String? = "", callback: (List<Content>?) -> Unit) {
    val token = BuildConfig.GITHUB_TOKEN
    val service = GitHubService.createGitHubApiService(token)

    val call = service.getContents(
      owner = ORGANIZATION_NAME,
      repo = REPO_NAME,
      path = if (path.isNullOrEmpty()) "plugins" else "plugins/$path"
    )
    call.enqueue(object : Callback<List<Content>> {
      override fun onResponse(call: Call<List<Content>>, response: Response<List<Content>>) {
        if (response.isSuccessful) {
          val contents = response.body()
          contents?.forEach { content ->
            if (content.type == "dir") getPluginSize(content.path) {
              println("Plugin Package Name: ${content.name}")
              println("Download URL: ${content.downloadUrl}")
              println("Size: $it bytes")
              println("-------------------------")
            } else {
              println("Plugin Package Name: ${content.name}")
              println("Download URL: ${content.downloadUrl}")
              println("Size: ${content.size} bytes")
              println("-------------------------")
            }
          }
          callback(contents)
        } else {
          println("Failed to fetch plugins: ${response.message()}")
          callback(null)
        }
      }

      override fun onFailure(call: Call<List<Content>>, t: Throwable) {
        println("Error: ${t.message}")
        callback(null)
      }
    })
  }

  fun getPluginSize(path: String, callback: (Long) -> Unit) {
    val token = BuildConfig.GITHUB_TOKEN
    val service = GitHubService.createGitHubApiService(token)

    val call = service.getContents(
      owner = ORGANIZATION_NAME,
      repo = REPO_NAME,
      path = path
    )
    call.enqueue(object : Callback<List<Content>> {
      override fun onResponse(call: Call<List<Content>>, response: Response<List<Content>>) {
        if (response.isSuccessful) {
          val contents = response.body()
          var totalSize = 0L
          contents?.forEach { content ->
            if (content.type == "file") totalSize += content.size
          }
          callback(totalSize)
        } else {
          println("Failed to fetch directory contents: ${response.message()}")
          callback(0)
        }
      }

      override fun onFailure(call: Call<List<Content>>, t: Throwable) {
        println("Error: ${t.message}")
        callback(0)
      }
    })
  }

  private fun downloadDirectory(path: String, targetDir: File) {
    fetchPluginsFromGithub(path = path) { contents ->
      contents?.forEach {
        if (it.type == "file") {
          downloadFile(it.downloadUrl, File(targetDir, it.name))
        } else if (it.type == "dir") {
          val newDir = File(targetDir, it.name)
          newDir.mkdirs()
          downloadDirectory(it.path, newDir)
        }
      }
    }
  }

  private fun downloadFile(downloadUrl: String?, targetFile: File) {
    downloadUrl?.let {
      val request = Request.Builder().url(it).build()
      val client = OkHttpClient()

      client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
          println("Failed to download file: ${e.message}")
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
          response.body?.byteStream()?.use {
            targetFile.outputStream().use { os -> it.copyTo(os) }
          }
        }
      })
    }
  }
}