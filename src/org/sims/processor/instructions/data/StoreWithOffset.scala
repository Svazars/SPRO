package org.sims.processor.instructions.data


import org.sims.processor.memory.I16
import org.sims.processor.instructions._

class StoreWithOffset(val value: Argument, val dstAddress: Argument, val offset: Argument) extends Instruction {
  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))

    StoreWithOffset(InstructionHeader.argument(header, 0, stream),
      InstructionHeader.argument(header, 1, stream),
      InstructionHeader.argument(header, 2, stream))
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(value), Some(dstAddress), Some(offset))
  override val instructionName: String = "StoreWithOffset"
  override val instructionID: Int = 16

  override def toString: String = s"StoreWithOffset($value to [$dstAddress + $offset])"
}

object StoreWithOffset {
  def unapply(arg: StoreWithOffset): Option[(Argument, Argument, Argument)] = Some(arg.value, arg.dstAddress, arg.offset)

  def apply(args: Iterator[Argument]): StoreWithOffset = StoreWithOffset(args.next, args.next, args.next)
  def apply(v: Argument, add: Argument, offset: Argument) = new StoreWithOffset(v, add, offset)
  def apply(): StoreWithOffset = StoreWithOffset(Argument(I16(0)), Argument(I16(0)),  Argument(I16(0)))
}
