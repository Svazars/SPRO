package org.sims.processor.instructions.control

import org.sims.processor.instructions._
import org.sims.processor.memory.I16

class Jump(val address: Argument) extends Instruction {

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))
    Jump(InstructionHeader.argument(header, 0, stream))
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(address), None, None)
  override val instructionName: String = "jump"
  override val instructionID: Int = 7
  override def toString: String = s"Jump($address)"
}

object Jump {
  def unapply(arg: Jump): Option[Argument] = Some(arg.address)

  def apply(args: Iterator[Argument]): Jump = Jump(args.next)
  def apply(addr: Argument) = new Jump(addr)
  def apply(): Jump = Jump(Argument(I16(0)))
}
