package org.sims.processor.instructions.data

import org.sims.processor.GPRS.GPR
import org.sims.processor.memory.I16
import org.sims.processor.instructions._

class Pop(val arg: Argument) extends Instruction {

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert (parsable(header))

    InstructionHeader.argument(header, 0) match {
      case Some(dstReg) => Pop(dstReg)
      case None => Instruction.invalidInstruction(header, "Destination of pop instruction does not encode register")
    }
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(arg), None, None)
  override val instructionName: String = "pop"
  override val instructionID: Int = 2

  override def toString: String = s"Pop(${args(0).get})"
}

object Pop {
  def unapply(pop: Pop): Option[GPR] = Some(pop.arg.asGPR().get)

  def apply(args: Iterator[Argument]): Pop = Pop(args.next)
  def apply(argument: Argument) = new Pop(argument)
  def apply(): Pop = Pop(Argument(I16(0)))
}