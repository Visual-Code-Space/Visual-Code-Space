package com.raredev.vcspace.tools;

import com.blankj.utilcode.util.ToastUtils;
import com.raredev.vcspace.R;

public enum FileExtension {
  APK("apk", R.drawable.ic_file_apk),
  CSS("css", R.drawable.language_css3),
  CPP("cpp", R.drawable.language_cpp),
  C("c", R.drawable.language_c),
  GO("go", R.drawable.language_go),
  H("h", R.drawable.language_cpp),
  HTML("html", R.drawable.language_html5),
  JAVA("java", R.drawable.language_java),
  JAR("jar", R.drawable.language_java),
  JSON("json", R.drawable.language_json),
  KT("kt", R.drawable.language_kotlin),
  KTS("kts", R.drawable.language_kotlin),
  LOG("log", R.drawable.language_txt),
  LUA("lua", R.drawable.language_lua),
  PHP("php", R.drawable.language_php),
  PYTHON("py", R.drawable.language_python),
  TXT("txt", R.drawable.language_txt),
  XML("xml", R.drawable.language_xml),
  ZIP("zip", R.drawable.ic_zip),
  UNKNOWN("", R.drawable.ic_file);

  public String extension;
  public int icon;

  FileExtension(String extension, int icon) {
    this.extension = extension;
    this.icon = icon;
  }

  public static FileExtension getIcon(String fileName) {
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

    if (extension == null) {
      return UNKNOWN;
    }

    for (FileExtension value : values()) {
      if (value.extension.equals(extension)) {
        return value;
      }
    }

    return UNKNOWN;
  }
}
