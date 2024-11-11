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

package com.teixeira.vcspace.plugins.wasm

import java.io.Serializable

/**
 * Represents a manifest file for a WebAssembly module. This class holds metadata about the module,
 * including its name, version, description, entry point, author, license, dependencies, and exports.
 *
 * @property name The name of the WebAssembly module.
 * @property version The version of the WebAssembly module, defaults to "1.0.0".
 * @property description A brief description of the WebAssembly module, defaults to an empty string.
 * @property main The entry point of the WebAssembly module, defaults to "main.wasm".
 * @property author The author of the WebAssembly module, defaults to an empty string.
 * @property license The license of the WebAssembly module, defaults to "MIT".
 * @property dependencies A list of dependencies for the WebAssembly module, defaults to an empty list.
 * @property exports A list of exported functions or variables from the WebAssembly module, defaults to an empty list.
 *
 * @constructor Creates a new Manifest instance.
 */
data class Manifest @JvmOverloads constructor(
  val name: String,
  val version: String = "1.0.0",
  val description: String = "",
  val main: String = "main.wasm",
  val author: String = "",
  val license: String = "MIT",
  val dependencies: List<String> = emptyList(),
  val exports: List<String> = emptyList()
) : Serializable
