package com.raredev.vcspace.editor.ace

data class Point(val row: Int, val column: Int)

data class Range(val start: Point, val end: Point) {
  companion object {
    fun fromPoints(start: Point, end: Point) = Range(start, end)
  }
}
