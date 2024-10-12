package org.sims

import org.sims.processor.Processor
import org.sims.processor.memory.{I16, I8}

/**
 * Move it somewhere.
 */
object Utils {

  val addressedMemory: Int = kb2bytes(64)

  def arenaSizeSqrt(arenaSize: Int) : Int = {
    val r = Math.sqrt(arenaSize).toInt
    assert(r * r == arenaSize, "Current visualizer limitation: memory size should be square")
    r
  }

  def arenaSizeSqrt(p: Processor) : Int = arenaSizeSqrt(p.memory.content.length)

  def alignDown(v: Int, alignment: Int): Int = {
    assert(v > 0)
    assert(alignment > 0)
    val r = (v / alignment) * alignment
    assert(r % alignment == 0)
    assert(v - r >= 0)
    assert(v - r < alignment)
    r
  }

  def kb2bytes(x: Int): Int = x * 1024

  def fitsToMemory(x: Int): Boolean = 0 <= x && x < addressedMemory

  def wrapValue(x: Int): Int = if (x < 0) wrapValue(x + addressedMemory) else x % addressedMemory

  def toBinary(x: Int, l: Int): String = Integer.toBinaryString(x).reverse.padTo(l, "0").reverse.mkString("")

  def trimTo5Digits(x: Int) = x.toString.reverse.padTo(5, "0").reverse.mkString("")

  def asWords(binary: Seq[I8]): Iterator[I16] = new Iterator[I16] {
    val it = binary.iterator

    override def hasNext: Boolean = it.hasNext

    override def next(): I16 = {
      val high = it.next()
      assert(it.hasNext)
      val low = it.next()
      I16(low, high)
    }
  }

}
