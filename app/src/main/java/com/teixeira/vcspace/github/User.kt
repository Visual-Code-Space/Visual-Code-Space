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

package com.teixeira.vcspace.github

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("login")
    val username: String,
    val id: Int,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val profileUrl: String,
    val name: String?,
    val bio: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    @SerializedName("public_repos")
    val publicRepositories: Int,
    @SerializedName("public_gists")
    val publicGists: Int,
    val followers: Int,
    val following: Int
)
