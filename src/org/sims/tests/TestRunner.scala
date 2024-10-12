package org.sims.tests

import org.sims.tests.decoding.TestInstructionFormat
import org.sims.tests.extensions.TestExtensions
import org.sims.tests.parsing.TestParser
import org.sims.tests.programs.TestPrograms


object TestRunner {

  def main(args: Array[String]): Unit = {
    runAll()
  }

  def runAll() = {
    TestExtensions.testAll()
    TestInstructionFormat.testAll()
    TestPrograms.runAll()
    TestParser.runAll()
  }
}
