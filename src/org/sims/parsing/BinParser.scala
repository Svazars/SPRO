package org.sims.parsing

import org.sims.processor.memory.{I8, I16}

object BinParser {

  def parseBinary(text: TraversableOnce[String]) : TraversableOnce[I8] = text.map(Utils.eraseComments).filter(!_.isEmpty).flatMap(parseLine)

  def parseLine(line: String) : TraversableOnce[I8] = {
    val txt = line.split(' ').mkString("")
    if (txt.length % 2 != 0) {
      throw new IllegalArgumentException(s"At line <$line> there is ${txt.length} not commented symbols (NOT even)")
    }

    (0 to (txt.length / 2) - 1).map(i => I8.parseFromHex(txt.substring(2*i, 2*i+2)))
  }
}
