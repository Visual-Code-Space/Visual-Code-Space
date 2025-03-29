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

package com.teixeira.vcspace.plugins

import java.io.IOException
import java.util.jar.JarFile

class PluginClassLoader(
    private val jarFilePath: String,
    parent: ClassLoader? = null
) : ClassLoader(parent ?: PluginClassLoader::class.java.classLoader) {

    override fun findClass(name: String): Class<*> {
        return try {
            val classData = readClassFromJar(name)
            defineClass(name, classData, 0, classData.size)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ClassNotFoundException("Could not load class $name", e)
        }
    }

    private fun readClassFromJar(className: String): ByteArray {
        val classFilePath = className.replace('.', '/') + ".class"
        val jarFile = JarFile(jarFilePath)

        val entry = jarFile.getJarEntry(classFilePath)
            ?: throw ClassNotFoundException("Class $className not found in JAR file")

        val classSize = entry.size
        if (classSize > Int.MAX_VALUE || classSize <= 0) {
            throw IOException("Invalid class file size: $classSize")
        }

        val inputStream = jarFile.getInputStream(entry)

        return inputStream.use { stream ->
            val classData = ByteArray(classSize.toInt())
            var bytesRead = 0
            while (bytesRead < classSize) {
                val result = stream.read(classData, bytesRead, (classSize - bytesRead).toInt())
                if (result == -1) {
                    break
                }
                bytesRead += result
            }
            classData
        }
    }
}
