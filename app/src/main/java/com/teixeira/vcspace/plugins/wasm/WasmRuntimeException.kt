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

/**
 * Represents a runtime exception that occurs during the execution of WebAssembly code.
 * This exception is a subclass of [RuntimeException] and can be thrown for various reasons,
 * such as invalid WebAssembly instructions, memory access violations, or other runtime errors.
 *
 * @param message The detail message, can be null.
 * @param cause The cause of this exception, can be null.
 */
class WasmRuntimeException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(cause: Throwable) : super(cause)
  constructor(message: String, cause: Throwable) : super(message, cause)
}