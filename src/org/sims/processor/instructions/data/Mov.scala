package org.sims.processor.instructions.data

import org.sims.processor.GPRS
import org.sims.processor.GPRS.GPR
import org.sims.processor.instructions.{Argument, Instruction, InstructionHeader}
import org.sims.processor.memory.I16

class Mov(val src: Argument, val dst: Argument) extends Instruction{
  require(dst.asGPR().nonEmpty)

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert (parsable(header))
    Mov(
      InstructionHeader.argument(header, 0, stream),
      InstructionHeader.argument(header, 1) match {
        case Some(dstReg) => dstReg
        case None => Instruction.invalidInstruction(header, "Destination of mov instruction does not encode register")
      }
    )
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(src), Some(dst), None)
  override val instructionName: String = "mov"
  override val instructionID: Int = 3

  override def toString: String = s"Mov ($src to $dst)"
}

object Mov {
  def unapply(arg: Mov): Option[(Argument, GPR)] = Some(arg.src, arg.dst.asGPR().get)

  def apply(args: Iterator[Argument]): Mov = Mov(args.next, args.next)
  def apply(src: Argument, dst: Argument) = new Mov(src, dst)
  def apply(): Mov = Mov(Argument(GPRS.IP), Argument(GPRS.IP))
}