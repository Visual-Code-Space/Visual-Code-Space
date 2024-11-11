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

import com.teixeira.vcspace.activities.EditorActivity

/**
 * A utility object for loading and running WebAssembly (Wasm) modules.
 *
 * This object provides functions for initializing the Wasm runtime and executing Wasm code.
 * It interacts with native code through JNI (Java Native Interface) to perform these operations.
 */
object WasmLoader {
  /**
   * Initializes the native library and sets up the necessary components for interaction with the editor.
   *
   * This function **must** be called before any other functions in the library are used. It establishes the
   * connection between the native library and the Android application, providing the library with the
   * necessary context and resources.
   *
   * @param editorActivity The Android Activity that hosts the editor UI. This provides access to
   *                       contextual information and resources required for initialization.
   *
   * @return An integer status code indicating the outcome of the initialization:
   *    - `0`: Initialization was successful.
   *    - Any other value: Initialization failed. Consult error logs for further details.
   */
  @JvmStatic
  external fun init(editorActivity: EditorActivity): Int


  /**
   * Executes a WebAssembly (WASM) module located at the specified path.
   *
   * This function loads and executes a WASM module. It optionally allows specifying
   * one or more function names to be invoked within the module. If no function
   * names are provided, the default entry point of the WASM module is executed.
   *
   * @param path The path to the WASM module file.
   * @param functionName An optional vararg of function names to be invoked.
   *                     If empty, the default entry point of the WASM module is executed.
   *
   * @throws Exception if an error occurs during WASM module loading or execution.
   *                   This could be due to invalid path, invalid WASM file, or errors
   *                   within the WASM code itself.
   */
  @JvmStatic
  external fun runWasm(path: String, vararg functionName: String)
}