package com.raredev.vcspace.ui.panels.compiler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.google.gson.Gson;
import com.raredev.vcspace.compiler.CompilationRequest;
import com.raredev.vcspace.compiler.databinding.LayoutExecutePanelBinding;
import com.raredev.vcspace.events.PanelEvent;
import com.raredev.vcspace.events.UpdateExecutePanelEvent;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.ui.panels.FloatingPanelArea;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.TextPanel;
import com.raredev.vcspace.ui.panels.web.WebViewPanel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class ExecutePanel extends Panel {

  private static final String[] LANGUAGES = {"Java", "Kotlin", "Python3", "C", "Cpp", "CSharp"};

  private LayoutExecutePanelBinding binding;

  private OkHttpClient client;
  private MediaType mediaType;

  private ArrayAdapter<String> adapter;
  private String path;

  public static FloatingPanelArea createFloating(Context context, FrameLayout parent) {
    FloatingPanelArea floatingPanel = new FloatingPanelArea(context, parent);
    floatingPanel.addPanel(new ExecutePanel(context), true);
    return floatingPanel;
  }

  public ExecutePanel(Context context) {
    super(context, R.string.execute);
  }

  @Override
  public View createView() {
    binding = LayoutExecutePanelBinding.inflate(LayoutInflater.from(getContext()));
    return binding.getRoot();
  }

  @Override
  public void viewCreated(View view) {
    super.viewCreated(view);
    client = new OkHttpClient();
    mediaType = MediaType.parse("application/json");

    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, LANGUAGES);
    binding.execute.setOnClickListener(
        v -> {
          var input = binding.input.getText().toString().toLowerCase();
          var language = binding.language.getText().toString().toLowerCase();
          compile(input, language);
        });
    binding.language.setAdapter(adapter);
  }

  @Override
  public void receiveEvent(PanelEvent event) {
    if (event instanceof UpdateExecutePanelEvent) {
      var updateExecuteEvent = (UpdateExecutePanelEvent) event;
      this.path = updateExecuteEvent.getPath();
      if (updateExecuteEvent.getFileExtension() == null) {
        binding.language.setText("");
        return;
      }
      binding.language.setText(getLanguageByExtension(updateExecuteEvent.getFileExtension()));
    }
  }

  @Override
  public void destroy() {
    mediaType = null;
    client = null;
    binding = null;
  }

  @Override
  public void unselected() {}

  @Override
  public void selected() {}

  private void compile(String input, String language) {
    if (language.equals("html")) {
      addWebViewPanel();
    } else {
      ProgressDialog progress = ProgressDialog.create(getContext());
      progress.setLoadingMessage("Compiling..");
      var dialog = progress.create();
      dialog.setCancelable(false);
      dialog.show();

      TaskExecutor.executeAsync(
          () -> {
            return compileWithServer(
                new CompilationRequest(
                    language, "lasted", FileIOUtils.readFile2String(path), input));
          },
          (result) -> {
            dialog.cancel();
            addTextPanel((String) result);
          });
    }
  }

  private void addWebViewPanel() {
    WebViewPanel webViewPanel = getPanelArea().getPanel(WebViewPanel.class);
    if (webViewPanel == null) {
      webViewPanel = new WebViewPanel(getContext());
      getPanelArea().addPanel(webViewPanel, false);
    }
    webViewPanel.loadFile(path);
    getPanelArea().setSelectedPanel(webViewPanel);
  }

  private void addTextPanel(String result) {
    TextPanel textPanel = getPanelArea().getPanel(TextPanel.class);
    if (textPanel == null) {
      textPanel = new TextPanel(getContext(), R.string.output);
      getPanelArea().addPanel(textPanel, false);
    }
    textPanel.setText(result);
    getPanelArea().setSelectedPanel(textPanel);
  }

  private String compileWithServer(CompilationRequest compilationRequest) {
    try {
      RequestBody body = RequestBody.create(mediaType, new Gson().toJson(compilationRequest));
      Request request =
          new Request.Builder()
              .url("https://online-code-compiler.p.rapidapi.com/v1/")
              .post(body)
              .addHeader("content-type", "application/json")
              .addHeader("X-RapidAPI-Key", "365fc54914msha80ac510381198fp107ffdjsn5206cb7b60fd")
              .addHeader("X-RapidAPI-Host", "online-code-compiler.p.rapidapi.com")
              .build();

      Response response = client.newCall(request).execute();
      return new JSONObject(response.body().string()).getString("output");
    } catch (Exception e) {
      return e.toString();
    }
  }

  private String getLanguageByExtension(String extension) {
    switch (extension) {
      case "py":
        return "Python3";
      case "java":
        return "Java";
      case "kt":
        return "Kotlin";
      case "cs":
        return "CSharp";
      default:
        return extension;
    }
  }

  public static boolean isExecutable(String fileName) {
    if (fileName == null || !fileName.contains(".")) return false;
    switch (FileUtils.getFileExtension(fileName).toLowerCase()) {
      case ".java":
      case ".kt":
      case ".c":
      case ".cpp":
      case ".cs":
      case ".py":
      case ".html":
      case ".md":
        return true;
      default:
        return false;
    }
  }
}
