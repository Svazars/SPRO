package org.sims.processor.extensions

import org.sims.processor.GPRS.GPR
import org.sims.processor.ProcessorGPRState
import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.memory.{I16, Memory}

/**
  * Extension module defines instruction set, supported by it and way how to execute this instructions.
  * This mechanism enables to create funny processor architectures (e.g. without push/pop or
  * arithmetic operations) if necessary. Idea is to make customization of processor as easy as possible.
  *
  * Also you can define instructions with the same IDs and they would not conflict until
  * corresponding modules used in the same processor.
  *
  * !!! REMEMBER: add implemented modules to org.sims.tests.extensions.TestExtensions
  */
abstract class ExtensionModule(val memory: Memory, val gprs: ProcessorGPRState) {
  def instructionSet() : Set[Instruction]
  def execute(instruction: Instruction)

  def isSupported(instr: Instruction): Boolean = instructionSet() contains instr

  protected def argumentContent(arg: Argument) : I16 = arg.content match {
      case Left(imm) => imm
      case Right(gpr) => gprs.get(gpr)
  }

  protected def addToGPR(gpr: GPR, value: Int): Unit = gprs.addTo(gpr, value)
  protected def setGPR(gpr: GPR, value: I16): Unit = gprs.set(gpr, value)
  protected def setGPR(gpr: GPR, value: Argument): Unit = gprs.set(gpr, argumentContent(value))

  protected def memoryAt(gpr: GPR): I16 = memory.at(gprs.get(gpr))
  protected def memoryAt(addr: I16): I16 = memory.at(addr)

  protected def memoryUpdate(value: Argument, dst: Argument) : Unit =
    memoryUpdate(value, argumentContent(dst))

  protected def memoryUpdate(value: Argument, dst: I16) : Unit =
    memory.update(dst, argumentContent(value))
}
