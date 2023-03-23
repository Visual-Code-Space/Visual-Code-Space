package com.raredev.vcspace.models;

import androidx.annotation.DrawableRes
import com.raredev.vcspace.R
import java.io.File

enum class FileExtension(val extension: String, @DrawableRes val icon: Int) {
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
  JAVASCRIPT("js", R.drawable.language_javascript),
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
  
  class Factory {
    companion object {
      @JvmStatic
      fun forFile(file: File?): FileExtension {
        return forExtension(file?.extension)
      }

      @JvmStatic
      fun forExtension(extension: String?): FileExtension {
        if (extension == null) {
          return UNKNOWN
        }
        
        for (value in values()) {
          if (value.extension == extension) {
            return value
          }
        }

        return UNKNOWN
      }
    }
  }
}