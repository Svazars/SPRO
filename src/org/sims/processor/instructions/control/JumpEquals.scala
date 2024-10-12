package org.sims.processor.instructions.control

import org.sims.processor.instructions.{InstructionHeader, Instruction, Argument}
import org.sims.processor.memory.I16


class JumpEquals(val x: Argument, val y: Argument, val addr: Argument) extends Instruction {

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))
    JumpEquals(InstructionHeader.argument(header, 0, stream),
      InstructionHeader.argument(header, 1, stream),
      InstructionHeader.argument(header, 2, stream))
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(x), Some(y), Some(addr))
  override val instructionName: String = "jump_equals"
  override val instructionID: Int = 15
  override def toString: String = s"JumpEquals(if $x == $y to $addr)"
}

object JumpEquals {
  def unapply(arg: JumpEquals): Option[(Argument, Argument, Argument)] = Some(arg.x, arg.y, arg.addr)

  def apply(args: Iterator[Argument]): JumpEquals = JumpEquals(args.next, args.next, args.next)
  def apply(x: Argument, y: Argument, addr: Argument) = new JumpEquals(x, y, addr)
  def apply(): JumpEquals = JumpEquals(Argument(I16(0)), Argument(I16(0)), Argument(I16(0)))
}
