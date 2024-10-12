package org.sims.processor.instructions.control

import org.sims.processor.instructions.{Argument, InstructionHeader, Instruction}
import org.sims.processor.memory.I16


class JumpZero(val tst: Argument, val address: Argument) extends Instruction {

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))
    JumpZero(InstructionHeader.argument(header, 0, stream), InstructionHeader.argument(header, 1, stream))
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(tst), Some(address), None)
  override val instructionName: String = "jump_zero"
  override val instructionID: Int = 14
  override def toString: String = s"JumpZero(if $tst to $address)"
}

object JumpZero {
  def unapply(arg: JumpZero): Option[(Argument, Argument)] = Some(arg.tst, arg.address)

  def apply(args: Iterator[Argument]): JumpZero = JumpZero(args.next, args.next)
  def apply(tst: Argument, addr: Argument) = new JumpZero(tst, addr)
  def apply(): JumpZero = JumpZero(Argument(I16(0)), Argument(I16(0)))
}
