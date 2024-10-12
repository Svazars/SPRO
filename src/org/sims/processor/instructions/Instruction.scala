package org.sims.processor.instructions

import org.sims.processor.HaltExecution
import org.sims.processor.memory.I16

abstract class Instruction extends InstructionHeader {
  override def equals(obj: scala.Any): Boolean = obj match {
      case command: Instruction => command.instructionID == this.instructionID
      case _ => false
  }

  override def hashCode(): Int = instructionID
}

object Instruction {
  def invalidInstruction(header: I16, msg: String) =
    HaltExecution.raise(s"Error when decoded header $header: $msg")
}

