package org.sims.processor.instructions.data

import org.sims.processor.GPRS.{GPR, IP, SP}
import org.sims.processor.HaltExecution
import org.sims.processor.instructions.{Argument, Instruction, InstructionHeader}
import org.sims.processor.memory.I16

class Load(val loadAddress: Argument, val dst: Argument) extends Instruction {
  require(dst.asGPR().nonEmpty)

  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))
    Load(InstructionHeader.argument(header, 0, stream),
         InstructionHeader.argument(header, 1) match {
            case Some(x) => x
            case None => HaltExecution.raise("Destination of load instruction does not encode register")
      }
    )
  }

  override def args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(loadAddress), Some(dst), None)
  override def instructionName: String = "load"
  override def instructionID: Int = 6
  override def toString: String = s"Load(from $loadAddress to $dst)"
}

object Load {
  def unapply(arg: Load): Option[(Argument, GPR)] = Some(arg.loadAddress, arg.dst.asGPR().get)

  def apply(args: Iterator[Argument]): Load = Load(args.next, args.next())
  def apply(loadAddress: Argument, dst: Argument) = new Load(loadAddress, dst)
  def apply(loadAddress: Argument, dst: GPR) = new Load(loadAddress, Argument(dst))
  def apply(): Load = Load(Argument(SP), Argument(IP))
}
