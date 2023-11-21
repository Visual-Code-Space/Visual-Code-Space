/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.langs.textmate.registry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.registry.IThemeSource;

import java.util.ArrayList;
import java.util.List;

import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;

public class ThemeRegistry {

  private static ThemeRegistry instance;

  public static synchronized ThemeRegistry getInstance() {
    if (instance == null) {
      instance = new ThemeRegistry();
    }
    return instance;
  }

  private final List<ThemeChangeListener> allListener = new ArrayList<>();
  private final List<ThemeModel> allThemeModel = new ArrayList<>();

  private ThemeModel currentThemeModel;

  private ThemeRegistry() {
    currentThemeModel = ThemeModel.EMPTY;
  }

  public synchronized void loadTheme(ThemeModel themeModel)
      throws Exception {
    if (!themeModel.isLoaded()) {
      themeModel.load();
    }
    var theme = findThemeByThemeName(themeModel.getName());
    if (theme != null) {
      setTheme(theme);
      return;
    }
    allThemeModel.add(themeModel);
  }

  @Nullable
  public ThemeModel findThemeByFileName(@NonNull String name) {
    for (var themeModel : allThemeModel) {
      if (themeModel.getName().equals(name)) {
        return themeModel;
      }
    }
    return null;
  }

  @Nullable
  public ThemeModel findThemeByThemeName(@NonNull String name) {
    for (var themeModel : allThemeModel) {
      var rawTheme = themeModel.getRawTheme();

      if (rawTheme == null) {
        continue;
      }

      if (name.equals(rawTheme.getName())) {
        return themeModel;
      }
    }
    return null;
  }

  public synchronized boolean setTheme(@NonNull String name) {
    var targetModel = findThemeByFileName(name);

    if (targetModel != null) {
      setTheme(targetModel);
      return true;
    }

    return false;
  }

  public void setTheme(ThemeModel theme) {
    currentThemeModel = theme;

    if (!allThemeModel.contains(theme)) {
      allThemeModel.add(theme);
    }

    if (!theme.isLoaded()) {
      try {
        theme.load();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    dispatchThemeChange(currentThemeModel);
  }

  public ThemeModel getCurrentThemeModel() {
    return currentThemeModel;
  }

  private void dispatchThemeChange(ThemeModel targetThemeModel) {
    for (var listener : allListener) {
      listener.onChangeTheme(targetThemeModel);
    }
  }

  public boolean hasListener(ThemeChangeListener themeChangeListener) {
    return allListener.contains(themeChangeListener);
  }

  public synchronized void addListener(ThemeChangeListener themeChangeListener) {
    allListener.add(themeChangeListener);
  }

  public synchronized void removeListener(ThemeChangeListener themeChangeListener) {
    allListener.remove(themeChangeListener);
  }

  public void dispose() {
    allListener.clear();
  }

  @FunctionalInterface
  public interface ThemeChangeListener {
    void onChangeTheme(ThemeModel newTheme);
  }
}
