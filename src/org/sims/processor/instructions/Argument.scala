package org.sims.processor.instructions

import org.sims.Utils
import org.sims.processor.GPRS.GPR
import org.sims.processor.GPRS
import org.sims.processor.memory.I16

/**
  * Argument for any instruction, may be immediate or GPR.
  */
class Argument(val content: Either[I16, GPR]) {
  def encoding: String = content match {
      case Left(imm) => Argument.immediateEncoding
      case Right(gpr) => Utils.toBinary(gpr.id, 3)
  }

  def asGPR(): Option[GPR] = content match {
    case Right(gpr) => Some(gpr)
    case _ => None
  }

  override def toString: String = content match {
    case Left(a)  => a.toString
    case Right(b) => b.toString
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case a:Argument => (a.content, content) match {
      case (Left(imm1), Left(imm2)) => imm1.equals(imm2)
      case (Left(_), Right(_)) => false
      case (Right(_), Left(_)) => false
      case (Right(gpr1), Right(gpr2)) => gpr1.equals(gpr2)
    }

    case _ => false
  }

  override def hashCode(): Int = encoding.hashCode

  val isImmediate = content.isLeft
}

object Argument {
  def apply(imm: I16): Argument = new Argument(Left(imm))
  def apply(reg: GPR) : Argument = new Argument(Right(reg))
  def apply(iterator: Iterator[I16]): Argument = Argument(iterator.next)

  def apply(encoding: Int): Option[Argument] = encoding match {
    case Argument.immediateEncodingAsInt => None
    case _ => Some(Argument(GPRS(encoding)))
  }

  val immediateEncoding: String = "111"
  val immediateEncodingAsInt: Int = Integer.parseInt(immediateEncoding, 2)
}
