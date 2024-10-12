package org.sims.processor.instructions.arith

import org.sims.processor.GPRS.{GPR, IP}
import org.sims.processor.instructions.{Argument, Binary}
import org.sims.processor.memory.I16

class Add(val a1:Argument, val a2:Argument, val dst:Argument) extends Binary(a1, a2, dst) {
  override def byArgs(arg1: Argument, arg2: Argument, dstRegister: Argument): Binary =
    Add(arg1, arg2, dstRegister)

  override def instructionName: String = "add"

  override def instructionID: Int = 8
}

object Add {
  def unapply(arg: Add): Option[(Argument, Argument, GPR)] = Binary.unapply(arg)

  def apply(args: Iterator[Argument]): Add = Add(args.next, args.next, args.next)
  def apply(a1:Argument, a2:Argument, dst:Argument) = new Add(a1, a2, dst)
  def apply(): Add = Add(Argument(I16(0)), Argument(I16(0)), Argument(IP))
}

