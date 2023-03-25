package com.raredev.vcspace;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import com.raredev.vcspace.actions.ActionManager;
import com.raredev.vcspace.actions.editor.CloseAllAction;
import com.raredev.vcspace.actions.editor.CloseFileAction;
import com.raredev.vcspace.actions.editor.CloseOthersAction;
import com.raredev.vcspace.actions.file.CopyPathAction;
import com.raredev.vcspace.actions.file.CreateFileAction;
import com.raredev.vcspace.actions.file.CreateFolderAction;
import com.raredev.vcspace.actions.file.DeleteFileAction;
import com.raredev.vcspace.actions.file.RenameFileAction;
import com.raredev.vcspace.fragments.SettingsFragment;
import com.raredev.vcspace.util.ILogger;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import java.util.concurrent.CompletableFuture;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class VCSpaceApplication extends Application {
  public static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    registerActions();

    loadTextMate();
  }

  private void registerActions() {
    ActionManager manager = ActionManager.getInstance();
    manager.clear();
    // Editor
    manager.registerAction(new CloseFileAction());
    manager.registerAction(new CloseOthersAction());
    manager.registerAction(new CloseAllAction());

    // File tree
    manager.registerAction(new CopyPathAction());
    manager.registerAction(new CreateFileAction());
    manager.registerAction(new CreateFolderAction());
    manager.registerAction(new RenameFileAction());
    manager.registerAction(new DeleteFileAction());
  }

  private void loadTextMate() {
    // Load editor themes
    try {
      String[] themes = new String[] {"vcspace_dark", "vcspace_light"};
      ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
      for (String name : themes) {
        String path = "textmate/" + name + ".json";
        themeRegistry.loadTheme(
            new ThemeModel(
                IThemeSource.fromInputStream(
                    FileProviderRegistry.getInstance().tryGetInputStream(path), path, null),
                name));
      }
      ILogger.info("ThemeLoader", "Themes loaded successfully");
    } catch (Exception e) {
      ILogger.error(
          "ThemeLoader", "Error when trying to load themes: \t" + Log.getStackTraceString(e));
    }
    // Load editor languages
    try {
      GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
      ILogger.info("LanguageLoader", "Languages loaded successfully");
    } catch (Exception e) {
      ILogger.error(
          "LanguageLoader", "Error when trying to load languages: \t" + Log.getStackTraceString(e));
    }
  }
}
