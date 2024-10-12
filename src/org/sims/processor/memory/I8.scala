package org.sims.processor.memory

import org.sims.Utils

/**
  * It is just unsigned byte with fancy constructors.
  * Note, that underlying implementation uses Int, so memory consumption is not very optimized.
  */
class I8(val b: Int) {
  require(0 <= b && b < 256)

  private def hexLetter(x: Int): String = {
    assert (0 <= x && x < 16)
    Integer.toHexString(x)
  }

  def highestNBits(n: Int = 8) = {
    assert(0 <= n && n <= 8)
    Utils.toBinary(b, 8).substring(0, n)
  }

  val toHex: String = hexLetter(b / 16) + hexLetter(b % 16)
  val toInt: Int = b

  override def toString: String = s"0x$toHex"

  override def equals(obj: scala.Any): Boolean = obj match {
    case I8(x) => b == x
    case _ => false
  }

  override def hashCode(): Int = b

  def xor(other: I8) : I8 = {
    val x:Short = b.toShort
    val y:Short = other.b.toShort
    I8( (x ^ y).toShort )
  }

  def or(other: I8) : I8 = {
    val x:Short = b.toShort
    val y:Short = other.b.toShort
    I8( (x | y).toShort )
  }

  def and(other: I8) : I8 = {
    val x:Short = b.toShort
    val y:Short = other.b.toShort
    I8( (x & y).toShort )
  }
}

object I8 {

  def unapply(arg: I8): Option[Int] = Some(arg.b)

  def apply(b: Int): I8 = {
    assert(0 <= b && b < 256, s"$b does not fit to I8")
    new I8(b)
  }

  def apply(b: String): I8 = {
    assert(b.length == 8)
    I8(Integer.parseInt(b, 2))
  }

  def parseFromHex(b: String) : I8 = {
    assert (b.length == 2)
    I8(Integer.parseInt(b, 16))
  }
}
