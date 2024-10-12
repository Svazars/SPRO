package org.sims.processor.instructions.arith

import org.sims.processor.GPRS.{IP, GPR}
import org.sims.processor.instructions.{Binary, Argument}
import org.sims.processor.memory.I16

class And(val a1:Argument, val a2:Argument, val dst:Argument) extends Binary(a1, a2, dst) {
  override def byArgs(arg1: Argument, arg2: Argument, dstRegister: Argument): Binary =
    And(arg1, arg2, dstRegister)

  override def instructionName: String = "and"

  override def instructionID: Int = 13
}

object And {
  def unapply(arg: And): Option[(Argument, Argument, GPR)] = Binary.unapply(arg)

  def apply(args: Iterator[Argument]): And = And(args.next, args.next, args.next)
  def apply(a1:Argument, a2:Argument, dst:Argument) = new And(a1, a2, dst)
  def apply(): And = And(Argument(I16(0)), Argument(I16(0)), Argument(IP))
}
