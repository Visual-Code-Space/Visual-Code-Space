package com.raredev.vcspace.activity;

import android.util.Log;
import android.view.View;
import com.raredev.vcspace.R;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.vcspace.databinding.ActivityLogViewBinding;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.ui.editor.textmate.DynamicTextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.util.List;

public class LogViewActivity extends VCSpaceActivity implements ILogger.Observer {
  private final String LOG_TAG = LogViewActivity.class.getSimpleName();
  private ActivityLogViewBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityLogViewBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    binding.editor.getProps().autoIndent = false;
    binding.editor.setEditable(false);
    binding.editor.setTextSize(12f);
    binding.editor.setTypefaceText(ResourcesCompat.getFont(this, R.font.jetbrains_mono));
    binding.editor.setTypefaceLineNumber(ResourcesCompat.getFont(this, R.font.jetbrains_mono));
    updateThemes();

    binding.fab.setOnClickListener(
        v -> {
          binding.editor.setText("");
          ILogger.clear();
        });
    ILogger.addObserver(this);
  }

  @Override
  protected void onDestroy() {
    ILogger.addObserver(null);
    super.onDestroy();
  }

  @Override
  public void onLogUpdated(List<String> logs) {
    CodeEditor editor = binding.editor;
    for (String log : logs) {
      appendText(log + "\n");
    }
  }

  public void updateThemes() {
    try {
      binding.editor.setColorScheme(
          DynamicTextMateColorScheme.create(this, ThemeRegistry.getInstance()));
      binding.editor.setEditorLanguage(TextMateLanguage.create("text.log", false));
    } catch (Exception e) {
      ILogger.error(LOG_TAG, Log.getStackTraceString(e));
    }
  }
  
  private int appendText(String text) {
    final var content = binding.editor.getText();
    if (binding.editor.getLineCount() <= 0) {
      return 0;
    }
    final int line = binding.editor.getLineCount() - 1;
    int col = content.getColumnCount(line);
    if (col < 0) {
      col = 0;
    }
    content.insert(line, col, text);
    return line;
  }
}
