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

package com.teixeira.vcspace.plugins.internal.distribution

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GitHubService {
  fun createGitHubApiService(token: String): GitHubApiService {
    val client = OkHttpClient.Builder().addInterceptor { chain ->
      val request = chain.request().newBuilder()
        .header("Authorization", Credentials.basic("itsvks19", token))
        .build()
      chain.proceed(request)
    }.build()

    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.github.com/")
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    return retrofit.create(GitHubApiService::class.java)
  }
}