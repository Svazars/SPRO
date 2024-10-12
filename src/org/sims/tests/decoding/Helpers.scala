package org.sims.tests.decoding

import org.sims.processor.HaltExecution
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.I16

import scala.util.control.Exception._

object Helpers {

  private def checkForErrors(data: Seq[I16], instruction: Instruction,
                             shouldBeParsed: Boolean, shouldThrow: Boolean): Unit= {
    val s = data.iterator
    val h = s.next
    if (!shouldBeParsed) {
      assert(instruction.parsable(h) == shouldBeParsed,
        s"Ill-formed header $h was successfully parsed by $instruction")
      return
    }

    catching(classOf[HaltExecution]) either instruction.decode(h, s) match {
      case Left(halt) => assert(shouldThrow,
        s"Instruction $instruction thrown unexpected $halt while decoding from valid test stream")
      case Right(inst) => assert(inst == instruction,
        s"Instruction $instruction decoded to $inst from valid test stream")
    }
  }

  object Format {
    def test(data: Seq[I16], instruction: Instruction, shouldThrow: Boolean) = {
      checkForErrors(data, instruction, shouldBeParsed = true, shouldThrow)
    }

    def valid(binary: Seq[I16], instruction: Instruction) =
      test(binary, instruction, shouldThrow = false)

    def validRaw(binary: Seq[String], instruction: Instruction) =
      valid(binary.map(I16.apply), instruction)

    def validRaw(binary: String, instruction: Instruction) =
      valid(Seq(I16(binary)), instruction)

    def invalid(binary: Seq[I16], instruction: Instruction) =
      test(binary, instruction, shouldThrow = true)

    def invalidRaw(binary: Seq[String], instruction: Instruction) =
      invalid(binary.map(I16.apply), instruction)

    def invalidRaw(binary: String, instruction: Instruction) =
      invalid(Seq(I16(binary)), instruction)
  }

  object Header {

    def test(header: I16, instruction: Instruction, shouldBeParsed : Boolean) = {
      checkForErrors(Seq(header), instruction, shouldBeParsed, shouldThrow = false)
    }

    def validRaw(binary: String, instruction: Instruction) =
      test(I16(binary), instruction, shouldBeParsed = true)

    def invalidRaw(binary: String, instruction: Instruction) =
      test(I16(binary), instruction, shouldBeParsed = false)
  }
}
