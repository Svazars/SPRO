package org.sims.parsing

object Utils {
  def eraseComments(line: String) : String = {
    val eraseFrom = line.indexOf(";")
    val r = if (eraseFrom < 0) line else line.substring(0, eraseFrom)
    r.trim
  }
}
