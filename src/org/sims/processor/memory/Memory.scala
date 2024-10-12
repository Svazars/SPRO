package org.sims.processor.memory

import org.sims.Utils

/**
  * Memory is represented by contiguous array of bytes (I8).
  * Memory access can never fail, since address is always computed by mod content.length.
  * It implies interesting behaviour in border cases, see example below:
  *
  *     content.length is equal to 1024 bytes
  *     IP is pointing to 1022-th byte
  *     bytes (1022, 1023) is Add(Immediate, R1, R1) // header.addr = 1022
  *
  *     then Add header will be decoded and immediate will be loaded from
  *         (header + 2, header + 3) % 1024 = (0, 1)
  *
  * Moreover, it will be more mind-bending when content.length is odd :)
  *
  * It is interesting that if content.length < 64 * 1024, than non-equal I16 values point to the
  * same memory location, say goodbye to naive address comparison.
  *
  * Also remember that I16 constructors also do mod 64 * 1024, so, for highest possible memory instances,
  * behaviour described above is quite natural and naive address comparison (through a1 - a2 == 0)
  * works as expected.
  */
class Memory(val partiallyInitialized: Array[I8]) {
  require(partiallyInitialized.length <= Utils.addressedMemory)

  var wordUpdateCallback : (I16, Option[I16]) => Unit = _

  val content = partiallyInitialized map {
    case null => I8(0)
    case any:I8 => any
  }

  private var memFetchesCounter = 0
  def doneMemoryFetches: Int = memFetchesCounter
  def zeroMemoryFetches(): Unit = memFetchesCounter = 0

  def update(index: I16, value: I16): Unit = {
    //Big endianess
    update(index.toInt    , value.high)
    update(index.toInt + 1, value.low)

    if(wordUpdateCallback != null) {
      wordUpdateCallback(index, Some(value))
    }
  }

  def at(memIdx: I16): I16 = {
    memFetchesCounter += 1
    extractMemoryValueNoCost(memIdx)
  }

  def extractMemoryValueNoCost(memIndex: I16) = {
    val memIdx = memIndex.toInt
    // remember, we use big-endian and cycled memory
    val highByte = content(   memIdx     % content.length)
    val lowByte  = content( (memIdx + 1) % content.length)
    I16(high = highByte, low = lowByte)
  }

  def view(start: I16) = new MemoryView(start.toInt)

  private def update(index: I16, value: I8): Unit = update(index.toInt, value)
  private def update(index: Int, value: I8): Unit = content.update(index % content.length, value)

  class MemoryView(val start: Int) extends Iterator[I16] {
    var offset = 0
    override def hasNext: Boolean = true
    override def next(): I16 = {
      val memIdx = start + offset
      offset += 2
      at(I16(memIdx))
    }
  }

  override def toString: String = content.toString
}

object Memory {
  def apply(bytes: Array[I8]) = new Memory(bytes)
}
