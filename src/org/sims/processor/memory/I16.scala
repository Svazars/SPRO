package org.sims.processor.memory

import org.sims.Utils

/**
  * It is C-like char, big-endian. Read values as you do in maths:
  *
  *           A010 = A * 16^3 + 0 * 16^2 + 1 * 16 + 0
  *
  * Initially, I planned to implement little-endian version too, but was too lazy :)
  */
class I16(val low: I8, val high: I8) {
  def toHex: String = high.toHex + low.toHex
  def toInt: Int = low.toInt + 256 * high.toInt
  def toBinary(n : Int = 16) = Utils.toBinary(toInt, n)

  def atOffset(offs: Int) = I16(toInt + offs)
  override def toString: String = s"0x$toHex"

  override def equals(obj: scala.Any): Boolean = obj match {
    case I16(l, h) => low.equals(l) && high.equals(h)
    case _ => false
  }

  override def hashCode(): Int = toInt
}

object I16 {
  def sum(x: I16 , y: I16) = I16(x.toInt + y.toInt)
  def sub(x: I16 , y: I16) = I16(x.toInt - y.toInt)
  def mul(x: I16 , y: I16) = I16(x.toInt * y.toInt)

  def xor(x: I16 , y: I16) = I16(x.low.xor(y.low), x.high.xor(y.high))
  def or(x: I16 , y: I16)  = I16(x.low.or(y.low), x.high.or(y.high))
  def and(x: I16 , y: I16) = I16(x.low.and(y.low), x.high.and(y.high))

  def unapply(arg: I16): Option[(I8, I8)] = Some(arg.low, arg.high)
  def apply(low: I8, high: I8) = new I16(low, high)
  def apply(x: Int): I16 = {
    val wrapped = Utils.wrapValue(x)
    I16(I8(wrapped % 256), I8(wrapped / 256))
  }

  def apply(x: String): I16 = {
    assert(x.length == 16, s"$x is not I16, length != 16")
    I16(I8(x.substring(8)), I8(x.substring(0, 8)))
  }
}
