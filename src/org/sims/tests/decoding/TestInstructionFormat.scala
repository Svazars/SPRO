package org.sims.tests.decoding

import org.sims.processor.instructions.control.Nop
import org.sims.processor.instructions.data.Push
import org.sims.processor.memory.I16

object TestInstructionFormat {

  def testAll() = {
    testNop()
    testPush()
  }

  def testNop() = {
    val nop = Nop()
    Helpers.Header.validRaw  ("0000000000000000", nop)
    Helpers.Header.invalidRaw("0100000001010101", nop)
    Helpers.Header.invalidRaw("0000001000000000", nop)

    Helpers.Format.valid     (Array(I16(0))     , nop)
    Helpers.Format.validRaw  ("0000000001010101", nop)
    Helpers.Format.validRaw  ("0000000000111111", nop)
  }

  def testPush() = {
    val push = Push()
    Helpers.Header.validRaw(  "0000001000000000", push)
    Helpers.Header.invalidRaw("0100000001010101", push)
    Helpers.Header.invalidRaw("0000011000000000", push)

    Helpers.Format.validRaw  ("0000001000000010", push)
    Helpers.Format.validRaw  ("0000001000110010", push)
    Helpers.Format.validRaw  (Seq("0000001010000111", "0000000000000000"), push)
    Helpers.Format.validRaw  (Seq("0000001000000111", "0000000000000000"), push)
  }

  //TODO all instructions
  /*
  (2,Pop(I16(0000 | 0 | 0000000000000000)))
  (3,Mov (IP to IP))
  (4,Halt)
  (5,Store(I16(0000 | 0 | 0000000000000000) to [I16(0000 | 0 | 0000000000000000)]))
  (6,Load(from SP to IP))
  (7,Jump(I16(0000 | 0 | 0000000000000000)))
  */

}
