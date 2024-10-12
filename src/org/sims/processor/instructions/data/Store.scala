package org.sims.processor.instructions.data

import org.sims.processor.memory.I16
import org.sims.processor.instructions._

class Store(val value: Argument, val dstAddress: Argument) extends Instruction {
  override def decode(header: I16, stream: Iterator[I16]): Instruction = {
    assert(parsable(header))

    Store(InstructionHeader.argument(header, 0, stream),
          InstructionHeader.argument(header, 1, stream))
  }

  override val args: IndexedSeq[Option[Argument]] = IndexedSeq(Some(value), Some(dstAddress), None)
  override val instructionName: String = "store"
  override val instructionID: Int = 5

  override def toString: String = s"Store($value to [$dstAddress])"
}

object Store {
  def unapply(arg: Store): Option[(Argument, Argument)] = Some(arg.value, arg.dstAddress)

  def apply(args: Iterator[Argument]): Store = Store(args.next, args.next)
  def apply(v: Argument, add: Argument) = new Store(v, add)
  def apply(): Store = Store(Argument(I16(0)), Argument(I16(0)))
}
