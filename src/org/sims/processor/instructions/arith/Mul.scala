package org.sims.processor.instructions.arith

import org.sims.processor.GPRS.{GPR, IP}
import org.sims.processor.instructions.{Argument, Binary}
import org.sims.processor.memory.I16

class Mul(val a1:Argument, val a2:Argument, val dst:Argument) extends Binary(a1, a2, dst) {
  override def byArgs(arg1: Argument, arg2: Argument, dstRegister: Argument): Binary =
    Mul(arg1, arg2, dstRegister)

  override def instructionName: String = "mul"

  override def instructionID: Int = 10
}

object Mul {
  def unapply(arg: Mul): Option[(Argument, Argument, GPR)] = Binary.unapply(arg)

  def apply(args: Iterator[Argument]): Mul = Mul(args.next, args.next, args.next)
  def apply(a1:Argument, a2:Argument, dst:Argument) = new Mul(a1, a2, dst)
  def apply(): Mul = Mul(Argument(I16(0)), Argument(I16(0)), Argument(IP))
}