package org.sims.processor.extensions

import org.sims.processor.GPRS.SP
import org.sims.processor.ProcessorGPRState
import org.sims.processor.instructions.data.{Pop, Push}
import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.memory.Memory

/**
  * Enables support for (fast) stack operations, stack grows downwards.
  *
  * Push(value) is functionally equivalent to
  *       sub SP, 2, SP
  *       Store(value to SP)
  *
  * Pop(gpr) is equivalent to
  *       Load(gpr to to SP)
  *       add SP, 2, SP
  *
  * However, stack operations take less cycles to be decoded and are more compact in machine code.
  */
class Stack (override val memory: Memory, override val gprs: ProcessorGPRState)
  extends ExtensionModule(memory, gprs) {

  override def instructionSet(): Set[Instruction] = Set(Push(), Pop())

  override def execute(instruction: Instruction): Unit = {
    assert(isSupported(instruction))
    instruction match {
      // Stack grows downwards, as usual
      case Push(value) =>
        addToGPR(SP, -2)
        memoryUpdate(value, Argument(SP))

      case Pop(dst) =>
        setGPR(dst, memoryAt(SP))
        addToGPR(SP, 2)
    }
  }
}

object Stack {
  def apply(memory: Memory, gprs: ProcessorGPRState) = new Stack(memory, gprs)
}


