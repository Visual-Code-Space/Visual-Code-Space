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

package com.teixeira.vcspace.ui.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.teixeira.vcspace.APPLICATION_REPOSITORY_NAME
import com.teixeira.vcspace.BuildConfig
import com.teixeira.vcspace.ORGANIZATION_NAME
import com.teixeira.vcspace.github.Contributor
import com.teixeira.vcspace.github.GitHubService
import com.teixeira.vcspace.github.User
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.extensions.harmonizeWithPrimary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ContributorsCard(modifier: Modifier = Modifier) {
  val githubApiService = GitHubService.createGitHubApiService(BuildConfig.GITHUB_TOKEN)
  val contributorsCall = githubApiService.getContributors(
    owner = ORGANIZATION_NAME,
    repo = APPLICATION_REPOSITORY_NAME
  )

  var isLoadingContributors by remember { mutableStateOf(true) }
  val contributorList = remember { mutableStateListOf<Contributor>() }

  val uriHandler = LocalUriHandler.current

  LaunchedEffect(Unit) {
    contributorsCall.enqueue(object : Callback<List<Contributor>> {
      override fun onResponse(
        call: Call<List<Contributor>>,
        response: Response<List<Contributor>>
      ) {
        if (response.isSuccessful) {
          response.body()?.let { contributorList.addAll(it) }
          isLoadingContributors = false
        }
      }

      override fun onFailure(call: Call<List<Contributor>>, throwable: Throwable) {
        isLoadingContributors = false
      }
    })
  }

  OutlinedCard(modifier = modifier) {
    Text(
      text = stringResource(R.string.contributors),
      modifier = Modifier.padding(16.dp),
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.SemiBold
    )

    if (isLoadingContributors) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    } else {
      // I will improve this ui later
      LazyColumn {
        items(contributorList) { contributor ->
          var user by remember { mutableStateOf<User?>(null) }

          LaunchedEffect(contributor) {
            githubApiService.getUser(contributor.username).enqueue(object : Callback<User> {
              override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                  user = response.body()
                }
              }

              override fun onFailure(call: Call<User>, throwable: Throwable) {
                throwable.printStackTrace()
              }
            })
          }

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable {
                uriHandler.openUri(contributor.profileUrl)
              },
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = user?.avatarUrl ?: contributor.avatarUrl,
              contentDescription = null,
              modifier = Modifier
                .padding(start = 14.dp, end = 2.dp, top = 5.dp, bottom = 5.dp)
                .clip(CircleShape)
                .size(50.dp),
            )

            Column(
              modifier = Modifier.padding(10.dp)
            ) {
              Text(
                text = user?.name ?: contributor.username,
                style = MaterialTheme.typography.bodyMedium
              )

              Text(
                text = user?.bio ?: contributor.profileUrl,
                color = Color(0xFF2A61E7).harmonizeWithPrimary(0.4f),
                style = MaterialTheme.typography.bodySmall
              )
            }
          }
        }
      }
    }
  }
}