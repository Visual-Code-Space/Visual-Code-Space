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

package com.teixeira.vcspace.plugins;

import android.content.Context;

import androidx.annotation.Nullable;

import com.vcspace.plugins.Plugin;

import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;

public class PluginLoader {

  /**
   * @noinspection CallToPrintStackTrace
   */
  @Nullable
  public static Plugin loadPlugin(Context context, String jarFilePath, String className) {
    try {
      DexClassLoader dexClassLoader = new DexClassLoader(
        jarFilePath,
        null,
        null,
        context.getApplicationContext().getClassLoader()
      );

      Class<?> pluginClass = dexClassLoader.loadClass(className);

      if (Plugin.class.isAssignableFrom(pluginClass)) {
        Constructor<?> constructor = pluginClass.getConstructor();
        return (Plugin) constructor.newInstance();
      } else {
        throw new IllegalArgumentException("Class does not implement Plugin interface");
      }
    } catch (ClassNotFoundException e) {
      System.err.println("Plugin class not found: " + className);
      e.printStackTrace();
      return null;
    } catch (IllegalAccessException | InstantiationException e) {
      System.err.println("Unable to instantiate plugin class: " + className);
      e.printStackTrace();
      return null;
    } catch (NoSuchMethodException e) {
      System.err.println("Default constructor not found in plugin class: " + className);
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      System.err.println("Unexpected error while loading plugin: " + className);
      e.printStackTrace();
      return null;
    }
  }
}
