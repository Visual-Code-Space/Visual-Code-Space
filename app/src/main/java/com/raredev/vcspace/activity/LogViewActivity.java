package com.raredev.vcspace.activity;

import android.util.Log;
import android.view.View;
import com.raredev.vcspace.R;
import androidx.core.content.res.ResourcesCompat;
import com.raredev.vcspace.databinding.ActivityLogViewBinding;
import com.raredev.common.util.ILogger;
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
    ILogger.addObserver(this);
    binding.editor.getProps().autoIndent = false;
    binding.editor.setEditable(false);
    binding.editor.setTextSize(12f);
    binding.editor.setTypefaceText(ResourcesCompat.getFont(this, R.font.jetbrains_mono));
    binding.editor.setTypefaceLineNumber(ResourcesCompat.getFont(this, R.font.jetbrains_mono));

    binding.fab.setOnClickListener(
        v -> {
          binding.editor.setText("");
          ILogger.clear();
        });
    updateThemes();
  }

  @Override
  protected void onDestroy() {
    binding.editor.setText("");
    lineIndex = 0;
    super.onDestroy();
  }

  private int lineIndex = 0;

  @Override
  public void onLogUpdated(List<String> logs) {
    CodeEditor editor = binding.editor;

    for (String log : logs) {
      editor.getText().insert(lineIndex, 0, log + "\n");
      lineIndex++;
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
}
