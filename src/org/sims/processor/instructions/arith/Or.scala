package org.sims.processor.instructions.arith

import org.sims.processor.GPRS.{IP, GPR}
import org.sims.processor.instructions.{Binary, Argument}
import org.sims.processor.memory.I16

class Or(val a1:Argument, val a2:Argument, val dst:Argument) extends Binary(a1, a2, dst) {
  override def byArgs(arg1: Argument, arg2: Argument, dstRegister: Argument): Binary =
    Or(arg1, arg2, dstRegister)

  override def instructionName: String = "or"

  override def instructionID: Int = 12
}

object Or {
  def unapply(arg: Or): Option[(Argument, Argument, GPR)] = Binary.unapply(arg)

  def apply(args: Iterator[Argument]): Or = Or(args.next, args.next, args.next)
  def apply(a1:Argument, a2:Argument, dst:Argument) = new Or(a1, a2, dst)
  def apply(): Or = Or(Argument(I16(0)), Argument(I16(0)), Argument(IP))
}
