package com.raredev.vcspace.compiler;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.raredev.vcspace.compiler.databinding.LayoutCompileDialogBinding;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class Compile {

  private static final String[] LANGUAGES = {"Java", "Kotlin", "Python3", "C", "Cpp", "CSharp"};

  private OkHttpClient client;
  private MediaType mediaType;

  public Compile() {
    client = new OkHttpClient();
    mediaType = MediaType.parse("application/json");
  }

  public void showCompileDialog(Context context, String fileExtension, String code) {
    var builder = new MaterialAlertDialogBuilder(context);

    var bind = LayoutCompileDialogBinding.inflate(LayoutInflater.from(context));

    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, LANGUAGES);

    bind.language.setAdapter(adapter);

    String defaultLanguage = getLanguageByExtension(fileExtension);
    if (defaultLanguage != null) {
      bind.language.setText(defaultLanguage);
    } else {
      bind.language.setText("Java");
    }

    builder.setView(bind.getRoot());
    builder.setTitle(R.string.compile);

    builder.setPositiveButton(android.R.string.cancel, null);
    builder.setPositiveButton(
        R.string.compile,
        (dlg, witch) -> {
          ProgressDialog progress = ProgressDialog.create(context);
          progress.setLoadingMessage("Compiling..");
          var dialog = progress.create();
          dialog.show();

          var language = bind.language.getText().toString().toLowerCase();
          var input = bind.input.getText().toString().toLowerCase();
          TaskExecutor.executeAsync(
              () -> {
                return compileWithServer(new CompilationRequest(language, "lasted", code, input));
              },
              (result) -> {
                dialog.cancel();
                showResultDialog(context, (String) result);
              });
        });
    builder.show();
  }

  private void showResultDialog(Context context, String result) {
    var builder = new MaterialAlertDialogBuilder(context);
    builder.setTitle(R.string.output);
    builder.setMessage(result);
    builder.setPositiveButton(android.R.string.ok, null);
    builder.show();
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
}
