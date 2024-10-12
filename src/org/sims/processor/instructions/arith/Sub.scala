package org.sims.processor.instructions.arith

import org.sims.processor.GPRS.{GPR, IP}
import org.sims.processor.instructions.{Argument, Binary}
import org.sims.processor.memory.I16

class Sub(val a1:Argument, val a2:Argument, val dst:Argument) extends Binary(a1, a2, dst) {
  override def byArgs(arg1: Argument, arg2: Argument, dstRegister: Argument): Binary =
    Sub(arg1, arg2, dstRegister)

  override def instructionName: String = "sub"

  override def instructionID: Int = 9
}

object Sub {
  def unapply(arg: Sub): Option[(Argument, Argument, GPR)] = Binary.unapply(arg)


  def apply(args: Iterator[Argument]): Sub= Sub(args.next, args.next, args.next)
  def apply(a1:Argument, a2:Argument, dst:Argument) = new Sub(a1, a2, dst)
  def apply(): Sub = Sub(Argument(I16(0)), Argument(I16(0)), Argument(IP))
}