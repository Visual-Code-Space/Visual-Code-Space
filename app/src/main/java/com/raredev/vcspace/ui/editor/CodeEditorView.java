package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.raredev.vcspace.databinding.LayoutCodeEditorBinding;
import com.raredev.vcspace.models.LanguageScope;
import com.raredev.vcspace.ui.IDECodeEditor;
import com.raredev.vcspace.ui.VCSpaceSearcherLayout;
import com.raredev.vcspace.ui.language.html.HtmlLanguage;
import com.raredev.vcspace.ui.language.java.JavaLanguage;
import com.raredev.vcspace.ui.language.lua.LuaLanguage;
import com.raredev.vcspace.util.FileUtil;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class CodeEditorView extends LinearLayout {

  private LayoutCodeEditorBinding binding;

  private VCSpaceSearcherLayout searcherLayout;

  public CodeEditorView(Context context, File file) {
    super(context);
    binding = LayoutCodeEditorBinding.inflate(LayoutInflater.from(context));
    binding.editor.setFile(file);

    setOrientation(VERTICAL);
    removeAllViews();

    searcherLayout = new VCSpaceSearcherLayout(context, binding.editor);

    addView(
        binding.getRoot(),
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
    addView(searcherLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    setLoading(true);
    CompletableFuture.runAsync(
        () -> {
          var editor = binding.editor;
          var content = FileUtil.readFile(file.getAbsolutePath());
          editor.post(
              () -> {
                editor.setText(content, null);
                editor.markUnmodified();

                editor.setEditorLanguage(createLanguage());
                setLoading(false);
              });
        });

    binding.editor.configureEditor();
  }

  public void showAndHideSearcher() {
    searcherLayout.showAndHide();
  }

  public boolean searcherIsShowing() {
    return searcherLayout.isShowing;
  }

  public void release() {
    binding.editor.release();
  }

  public void undo() {
    if (binding.editor.canUndo()) {
      binding.editor.undo();
    }
  }

  public void redo() {
    if (binding.editor.canRedo()) {
      binding.editor.redo();
    }
  }

  public void saveFile() {
    binding.editor.saveFile();
  }

  public File getFile() {
    return binding.editor.getFile();
  }

  public IDECodeEditor getEditor() {
    return binding.editor;
  }

  public void setLoading(boolean loading) {
    binding.circularProgressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  public VCSpaceTMLanguage createLanguage() {
    try {
      final LanguageScope langScope = LanguageScope.Factory.forFile(getFile());

      switch (langScope) {
        case JAVA:
          return new JavaLanguage(this);
        case HTML:
          return new HtmlLanguage();
        case LUA:
          return new LuaLanguage();
      }

      return VCSpaceTMLanguage.create(langScope.getScope());
    } catch (Exception e) {
      return null;
    }
  }
}
