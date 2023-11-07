package com.raredev.vcspace.models

class GrammarModel(
  val name: String,
  val scopeName: String,
  val grammar: String,
  val languageConfiguration: String? = null,
  val embeddedLanguages: Map<String, String>? = null,
  val fileExtensions: Array<String>? = null
)