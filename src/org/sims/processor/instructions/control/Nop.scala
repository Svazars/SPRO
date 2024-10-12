package org.sims.processor.instructions.control

import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.memory.I16

class Nop extends Instruction{
  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))
    Nop()
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(None, None, None)
  override val instructionName: String = "nop"
  override val instructionID: Int = 0

  override def toString: String = "Nop"
}

object Nop {

  def apply(args: Iterator[Argument]): Nop = Nop()
  def apply(): Nop = new Nop()
}
