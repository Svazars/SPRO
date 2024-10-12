package org.sims.processor.instructions.data

import org.sims.processor.memory.I16
import org.sims.processor.instructions._

class Push(val arg: Argument) extends Instruction {

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert (parsable(header))
    Push(InstructionHeader.argument(header, 0, stream))
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(arg), None, None)
  override val instructionName: String = "push"
  override val instructionID: Int = 1

  override def toString: String = s"Push(${args(0).get})"
}

object Push {
  def unapply(arg: Push): Option[Argument] = Some(arg.arg)

  def apply(args: Iterator[Argument]): Push = Push(args.next)
  def apply(arg: Argument) = new Push(arg)
  def apply(): Push = Push(Argument(I16(0)))
}