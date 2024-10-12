package org.sims.processor.instructions
import org.sims.processor.GPRS.GPR
import org.sims.processor.memory.I16

abstract class Binary(val arg1: Argument, val arg2: Argument, val dstRegister: Argument) extends Instruction {
  require(dstRegister.content.isRight, "3rd argument of binary op MUST be register")

  def byArgs(arg1: Argument, arg2:Argument, dstRegister: Argument): Binary

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))

    InstructionHeader.argument(header, 2) match {
      case Some(dstReg) => byArgs(
          InstructionHeader.argument(header, 0, stream),
          InstructionHeader.argument(header, 1, stream),
          dstReg)

      case None => Instruction.invalidInstruction(header, s"Destination of binary instruction ($instructionName) does not encode register")
    }
  }

  override def toString: String = s"$instructionName($arg1, $arg2, $dstRegister)"

  override def args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(arg1), Some(arg2), Some(dstRegister))
}

object Binary {
  def unapply(arg: Binary): Option[(Argument, Argument, GPR)] = Some(arg.arg1, arg.arg2, arg.dstRegister.asGPR().get)
}