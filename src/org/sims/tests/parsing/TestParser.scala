package org.sims.tests.parsing

import org.sims.parsing.AsmParser
import org.sims.processor.GPRS
import org.sims.processor.instructions.Argument
import org.sims.processor.instructions.arith.{Add, Sub}
import org.sims.processor.instructions.control.{Halt, Jump}
import org.sims.processor.instructions.data.{Mov, Store}
import org.sims.processor.memory.I16
import org.sims.tests.programs.HalterN

object TestParser {

  def runAll(): Unit = {
    testSimpleInstruction()
    testSimpleProgram()
  }

  def testSimpleInstruction() = {
    for(line <- List("  add   1 ,    128 ,  R1", "add 1,128,R1")) {
      val i = AsmParser.parseProgram(List(line)).toList.head
      assert (i.isInstanceOf[Add], s"Failed with i = $i")
      val add = i.asInstanceOf[Add]
      assert(add.arg1.equals(Argument(I16(1))))
      assert(add.arg2.equals(Argument(I16(128))))
      assert(add.dstRegister.equals(Argument(GPRS.R1)))
    }
  }

  def testSimpleProgram() = {
    val listing = List(
      "HALT",
      "AdD 1, 1, SP",
      "MUL SP, R4, R2",
      "sub 15, 3, R1",
      "jump 15",
      "jump IP",
      "nop",
      "load R4, R2",
      "LOAD 135, IP",
      "mov 17, R2",
      "mov IP, R5",
      "pop R1",
      "Push 16",
      "PusH R3",
      "Store 15, 1256",
      "Store SP, 1111",
      "Store 10342, R1",
      "Store IP, R1")

    val code = AsmParser.parseProgram(listing).toList
    assert (code != null)
    assert (code.length == listing.length)
    for( (cmd, index) <- code.zipWithIndex) {
      assert (cmd.equals(AsmParser.parseProgram(List(listing(index))).toList.head))
    }
  }

  def testHalter(): Unit = {
    for(i <- List(1, 5, 7, 13, 1024)) {
      val halterListing = List(
        "mov 2048, r1",  // 2048 is HALT instruction
        "mov -2, r2",    // R2 is pointer to yet unmodified memory
        "store r1, r2",
        s"sub r2, $i, r2",
        "jump 8"
      )

      val halterCode = AsmParser.parseProgram(halterListing).toList
      val halterAST = HalterN.code(i)

      assert(halterCode.length == halterAST.length)
      for (index <- 0 to halterCode.length) {
        assert(halterCode(index).equals(halterAST(index)))
      }
    }
  }
}
