package com.raredev.vcspace.ui.panels.snippets;

import static io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage.SNIPPETS_DIR_PATH;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.FileIOUtils;
import com.raredev.vcspace.activities.EditorActivity;
import com.raredev.vcspace.adapters.SnippetFilesAdapter;
import com.raredev.vcspace.databinding.LayoutSnippetsPanelBinding;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.models.UserSnippetModel;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.ui.panels.FloatingPanelArea;
import com.raredev.vcspace.ui.panels.Panel;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SnippetsPanel extends Panel implements SnippetFilesAdapter.SnippetFileListener {

  private LayoutSnippetsPanelBinding binding;

  private SnippetFilesAdapter adapter;
  private List<UserSnippetModel> userSnippets;
  
  public static final String SNIPPET_FILE_EXAMPLE =
      """
  {
    // Place your snippets for language_name here. Each snippet is defined under a snippet name and has a prefix, body and
    // description. The prefix is what is used to trigger the snippet and the body will be expanded and inserted. Possible variables are:
    // $1, $2 for tab stops, $0 for the final cursor position, and ${1:label}, ${1:another} for placeholders. Placeholders with the
    // same ids are connected.
    // example:
    // "Print to console": {
    //   "prefix": "log",
    //   "body": [
    //     "console.log('$1');",
    //     "$2"
    //   ],
    //   "description": "Log output to console"
    // }
  }
  """;

  public static FloatingPanelArea createFloating(Context context, FrameLayout parent) {
    FloatingPanelArea floatingPanel = new FloatingPanelArea(context, parent);
    floatingPanel.addPanel(new SnippetsPanel(context), true);
    return floatingPanel;
  }

  public SnippetsPanel(Context context) {
    super(context, R.string.snippets);
  }

  @Override
  public View createView() {
    binding = LayoutSnippetsPanelBinding.inflate(LayoutInflater.from(getContext()));
    return binding.getRoot();
  }

  @Override
  public void viewCreated(View view) {
    super.viewCreated(view);
    adapter = new SnippetFilesAdapter();
    userSnippets = getSnippetFiles();

    binding.search.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String query = binding.search.getText().toString();
            filterData(query);
          }

          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

          @Override
          public void afterTextChanged(Editable editable) {}
        });

    binding.rvSnippetFiles.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.rvSnippetFiles.setAdapter(adapter);

    adapter.setSnippetFileListener(this);
    adapter.setData(userSnippets);
  }

  @Override
  public void unselected() {}

  @Override
  public void selected() {}

  @Override
  public void destroy() {
    binding = null;
  }

  @Override
  public void onFileClick(UserSnippetModel userSnippet, View v) {
    if (!userSnippet.getSnippetFile().exists()) {
      FileIOUtils.writeFileFromString(
          userSnippet.getSnippetFile().getPath(),
          SNIPPET_FILE_EXAMPLE.replaceAll("language_name", userSnippet.getLanguageName()));
    }

    if (getContext() instanceof EditorActivity) {
      ((EditorActivity) getContext())
          .openFile(FileModel.fileToFileModel(userSnippet.getSnippetFile()));
    }

    removeThis();
  }

  @Override
  public void onFileLongClick(UserSnippetModel file, View v) {}

  private void filterData(String query) {
    List<UserSnippetModel> filteredData = new ArrayList<>();
    for (UserSnippetModel userSnippet : userSnippets) {
      if (userSnippet.getLanguageName().toLowerCase().contains(query.toLowerCase())) {
        filteredData.add(userSnippet);
      }
    }
    adapter.setData(filteredData);
  }

  private List<UserSnippetModel> getSnippetFiles() {
    List<UserSnippetModel> files = new ArrayList<>();
    files.add(new UserSnippetModel("Batch", new File(SNIPPETS_DIR_PATH + "/bat.json")));
    files.add(new UserSnippetModel("C", new File(SNIPPETS_DIR_PATH + "/c.json")));
    files.add(new UserSnippetModel("C++", new File(SNIPPETS_DIR_PATH + "/cpp.json")));
    files.add(new UserSnippetModel("C#", new File(SNIPPETS_DIR_PATH + "/csharp.json")));
    files.add(new UserSnippetModel("CSS", new File(SNIPPETS_DIR_PATH + "/css.json")));
    files.add(new UserSnippetModel("Go", new File(SNIPPETS_DIR_PATH + "/go.json")));
    files.add(new UserSnippetModel("Groovy", new File(SNIPPETS_DIR_PATH + "/groovy.json")));
    files.add(new UserSnippetModel("HTML", new File(SNIPPETS_DIR_PATH + "/html.json")));
    files.add(new UserSnippetModel("ini", new File(SNIPPETS_DIR_PATH + "/ini.json")));
    files.add(new UserSnippetModel("Java", new File(SNIPPETS_DIR_PATH + "/java.json")));
    files.add(new UserSnippetModel("JavaScript", new File(SNIPPETS_DIR_PATH + "/js.json")));
    files.add(new UserSnippetModel("Json", new File(SNIPPETS_DIR_PATH + "/json.json")));
    files.add(new UserSnippetModel("Julia", new File(SNIPPETS_DIR_PATH + "/ji.json")));
    files.add(new UserSnippetModel("Kotlin", new File(SNIPPETS_DIR_PATH + "/kt.json")));
    files.add(new UserSnippetModel("Lua", new File(SNIPPETS_DIR_PATH + "/lua.json")));
    files.add(new UserSnippetModel("Markdown", new File(SNIPPETS_DIR_PATH + "/md.json")));
    files.add(new UserSnippetModel("Php", new File(SNIPPETS_DIR_PATH + "/php.json")));
    files.add(new UserSnippetModel("Python", new File(SNIPPETS_DIR_PATH + "/py.json")));
    files.add(new UserSnippetModel("Shell Script", new File(SNIPPETS_DIR_PATH + "/sh.json")));
    files.add(new UserSnippetModel("Smali", new File(SNIPPETS_DIR_PATH + "/smali.json")));
    files.add(new UserSnippetModel("Type Script", new File(SNIPPETS_DIR_PATH + "/ts.json")));
    files.add(new UserSnippetModel("XML", new File(SNIPPETS_DIR_PATH + "/xml.json")));
    files.add(new UserSnippetModel("YAML", new File(SNIPPETS_DIR_PATH + "/yaml.json")));
    return files;
  }
}