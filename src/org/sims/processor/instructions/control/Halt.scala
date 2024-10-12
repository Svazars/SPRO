package org.sims.processor.instructions.control

import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.memory.I16

class Halt extends Instruction {

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))
    Halt()
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(None, None, None)
  override val instructionName: String = "halt"
  override val instructionID: Int = 4

  override def toString: String = s"Halt"
}

object Halt {

  def apply(args: Iterator[Argument]): Halt = Halt()
  def apply() = new Halt()
}

