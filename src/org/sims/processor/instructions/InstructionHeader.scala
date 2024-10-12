package org.sims.processor.instructions

import org.sims.processor.memory.I16

/**
  * Instruction header fits to I16 and has the following structure:
  *   0000000               000                  000                  000
  *   [command-ID, 7 bits]  [arg2 type, 3 bits]  [arg1 type, 3 bits]  [arg0 type, 3 bits]
  *
  * arg type has simple interpretation:
  *     111 for immediate,
  *     GPR with id = Integer.parseInt(arg, 2), otherwise
  * */
abstract class InstructionHeader {
  def headerContent(): I16 = InstructionHeader.headerContent(this)

  def parsable(bytes: I16): Boolean =
    Integer.parseInt(bytes.high.highestNBits(InstructionHeader.instructionIDEncodingBits), 2) == instructionID

  def decode(header: I16, stream: Iterator[I16]): Instruction

  def args: IndexedSeq[Option[Argument]]
  def instructionName: String
  def instructionID: Int
}

object InstructionHeader {
  val instructionIDEncodingBits = 7
  val maxInstructionID = math.pow(2, instructionIDEncodingBits).toInt

  def headerContent(header: InstructionHeader): I16 = {
    assert(0 <= header.instructionID && header.instructionID < maxInstructionID)
    assert(header.args.length <= 3)

    def padZeros(str: String, n: Int) = str.reverse.padTo(n, "0").reverse.mkString("")

    val rawHeader = padZeros(Integer.toBinaryString(header.instructionID), instructionIDEncodingBits) +
        header.args.padTo(3, None).reverse.map {
          case None => "000"
          case Some(arg: Argument) => arg.encoding
        }.mkString("")

    I16(rawHeader)
  }

  /**
    * Parses nth argument type from header and loads it from iterator, if it is immediate.
    * Otherwise, returns GPR argument, iterator is not advanced.
    */
  def argument(header: I16, n: Int, it: Iterator[I16]): Argument = argument(header, n).getOrElse(Argument(it.next))

  /**
    * Parses nth argument type from header and returns None if it is immediate.
    */
  def argument(header: I16, n: Int): Option[Argument] = {
    val binary = header.toBinary()
    val argEncoding = n match {
      case 2 => binary.substring(7, 10)
      case 1 => binary.substring(10, 13)
      case 0 => binary.substring(13, 16)
    }
    assert(argEncoding.length == 3)
    Argument(Integer.parseInt(argEncoding, 2))
  }
}