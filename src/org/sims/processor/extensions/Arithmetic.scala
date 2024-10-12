package org.sims.processor.extensions

import org.sims.processor.ProcessorGPRState
import org.sims.processor.instructions.arith._
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.{I16, Memory}

class Arithmetic (override val memory: Memory, override val gprs: ProcessorGPRState)
  extends ExtensionModule(memory, gprs) {

  override def instructionSet(): Set[Instruction] = Set(Add(), Sub(), Mul(), Xor(), Or(), And())

  override def execute(instruction: Instruction): Unit = {
    assert(isSupported(instruction))
    instruction match {
      case Add(a1, a2, dstReg) =>
        setGPR(dstReg, I16.sum(argumentContent(a1), argumentContent(a2)))

      case Sub(a1, a2, dstReg) =>
        setGPR(dstReg, I16.sub(argumentContent(a1), argumentContent(a2)))

      case Mul(a1, a2, dstReg) =>
        setGPR(dstReg, I16.mul(argumentContent(a1), argumentContent(a2)))

      case Xor(a1, a2, dstReg) =>
        setGPR(dstReg, I16.xor(argumentContent(a1), argumentContent(a2)))

      case Or(a1, a2, dstReg) =>
        setGPR(dstReg, I16.or(argumentContent(a1), argumentContent(a2)))

      case And(a1, a2, dstReg) =>
        setGPR(dstReg, I16.and(argumentContent(a1), argumentContent(a2)))
    }
  }
}

object Arithmetic {
  def apply(memory: Memory, gprs: ProcessorGPRState) = new Arithmetic(memory, gprs)
}



