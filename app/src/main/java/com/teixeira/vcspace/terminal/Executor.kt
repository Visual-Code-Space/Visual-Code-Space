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

package com.teixeira.vcspace.terminal

import android.os.Process

/**
 * The `Executor` object provides an advanced mechanism for executing native binaries
 * in Android environments. It offers fine-grained control over execution by allowing
 * the user to specify custom arguments, environment variables, and optional linker usage.
 *
 * This object is particularly useful for scenarios where `Runtime.getRuntime().exec(...)`
 * lacks the flexibility to handle native binaries, shared library dependencies, or custom environments.
 *
 * Example usage:
 * ```
 * val binaryPath = "/system/bin/ls"
 * val args = arrayOf("-l", "/sdcard")
 * val env = arrayOf(
 *     "HOME=/data/user/0/<package>/files/home",
 *     "PATH=/system/bin:/system/xbin",
 *     "LD_LIBRARY_PATH=/system/lib:/system/lib64:/data/user/0/<package>/files/lib"
 * )
 *
 * val result = Executor.runBinary(binaryPath, args, env)
 * if (result == 0) {
 *     println("Execution succeeded!")
 * } else {
 *     println("Execution failed with code: $result")
 * }
 * ```
 */
object Executor {

  /**
   * Executes a native binary with the specified arguments and environment variables.
   *
   * This method provides enhanced functionality compared to `Runtime.getRuntime().exec(...)`,
   * enabling users to:
   * - Set custom environment variables such as `LD_LIBRARY_PATH`.
   * - Pass precise command-line arguments to the binary.
   * - Optionally specify a dynamic linker for executing ELF binaries.
   *
   * @param binaryPath The absolute path to the binary to execute.
   *                   Example: `/system/bin/ls`, `/system/bin/sh`, or `/sdcard/local/tmp/binary`.
   * @param args       The arguments to pass to the binary.
   *                   Example: `arrayOf("-l", "/data")`.
   * @param env        The environment variables to set during execution.
   *                   Example: `arrayOf("LD_LIBRARY_PATH=/system/lib:/system/lib64")`.
   * @param linker     (Optional) The dynamic linker to use for ELF binaries.
   *                   Defaults to `/system/bin/linker` for 32-bit and `/system/bin/linker64` for 64-bit.
   *                   Set this to an empty string to skip using a linker explicitly.
   *
   * @return The exit code of the binary. A return value of `0` indicates success, while
   *         non-zero values indicate errors or failures during execution.
   *
   * Example:
   * ```
   * val binaryPath = "/system/bin/ls"
   * val args = arrayOf("-l", "/data")
   * val env = arrayOf(
   *     "LD_LIBRARY_PATH=/system/lib:/system/lib64:/data/local/tmp/libs"
   * )
   *
   * val result = Executor.runBinary(binaryPath, args, env)
   * println("Execution result: $result")
   * ```
   *
   * @throws IllegalArgumentException If the `binaryPath` is empty or null.
   * @throws Exception If the native binary fails to execute due to internal errors.
   */
  external fun runBinary(
    binaryPath: String,
    args: Array<String>,
    env: Array<String>,
    linker: String = Executor.linker
  ): Int

  val linker: String
    get() = if (Process.is64Bit()) "/system/bin/linker64" else "/system/bin/linker"
}
