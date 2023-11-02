package com.raredev.vcspace.editor.ace.options

import com.raredev.vcspace.editor.ace.Range

interface SearchOptions {
  var needle: String
  var preventScroll: Boolean
  var backwards: Boolean
  var start: Range
  var skipCurrent: Boolean
  var range: Range
  var preserveCase: Boolean
  var regExp: Boolean
  var wholeWord: Boolean
  var caseSensitive: Boolean
  var wrap: Boolean
}
