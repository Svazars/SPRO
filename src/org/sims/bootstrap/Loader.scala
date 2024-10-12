package org.sims.bootstrap

import org.sims.processor.GPRS.GPR
import org.sims.processor._
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.{I16, I8, Memory}

class Loader(private val registers: ProcessorGPRState, private val bytesToLoad: Array[I8]) {
  def setupGPR(gpr: GPR, value: I16): Loader= {
    registers.set(gpr, value)
    this
  }

  private def setupMemory(idx: Int, byte: I8): Int = {
    assert(0 <= idx && idx < bytesToLoad.length, s"Tried initialize memory at idx = $idx")
    bytesToLoad.update(idx, byte)
    idx + 1
  }

  private def setupMemory(idx: Int, value: I16): Int = {
    // Big endian
    setupMemory(idx, value.high)
    setupMemory(idx + 1, value.low)
    idx + 2
  }

  private def setupCommand(atByteIdx: Int, cmd: Instruction) : Int = {
    var idx = setupMemory(atByteIdx, cmd.headerContent())
    cmd.args.filter(_.isDefined).map(_.get).foreach(_.content match {
      case Left(imm) => idx = setupMemory(idx, imm)
      case Right(gpr) =>
    })

    idx
  }

  def writeData(start: Int, data: Seq[I8]): Loader = {
    for ((byte, offset) <- data.zipWithIndex) {
      setupMemory(start + offset, byte)
    }

    this
  }

  //TODO: think, if it is ever needed to load program into working processor
  def loadToFreshProcessor(processorGenerator: (Memory, ProcessorGPRState) => Processor) = processorGenerator(Memory(bytesToLoad), registers)
}

object Loader {
  def apply(memoryBytes: Int) = new Loader(ProcessorGPRState(), new Array[I8](memoryBytes))
}
