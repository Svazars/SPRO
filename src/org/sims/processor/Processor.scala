package org.sims.processor

import org.sims.processor.GPRS.IP
import org.sims.processor.extensions.{Arithmetic, Core, ExtensionModule, Stack}
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.{I16, Memory}

import scala.collection.mutable
import scala.util.control.Exception._

class Processor(val memory: Memory,
                val gprs: ProcessorGPRState,
                val extensions: Seq[ExtensionModule],
                var log: String => Unit) {

  val supportedInstructions = mutable.Set[Instruction]()

  var slowVisCallback: (Processor) => Unit = (_: Processor) => ()
  var fastVisCallback: (I16, Option[I16], Memory) => Unit = (_:I16, _:Option[I16], _:Memory) => ()
  var redrawCallback : () => Unit = () => ()

  for (ext <- extensions) {
    for (inst <- ext.instructionSet()) {

      if (supportedInstructions.contains(inst)) {
        throw new AssertionError(s"Following extensions has the same instruction [$inst] defined: " +
          extensions.filter(_.instructionSet().contains(inst)).map(_.toString))
      }

      supportedInstructions += inst
    }
  }

  // (done cycles, halted)
  def execute(cycles: Int): (Int, Boolean) = {
    assert(cycles >= 0)
    memory.zeroMemoryFetches()
    log(s"Start $cycles-length execution round")
    var doneCycles = 0

    while (doneCycles < cycles) {
      catching(classOf[HaltExecution]) either executeCommand() match {
        case Left(halt) =>
          log(s"Execution interrupted. Reason: ${halt.getMessage}");
          return (0, true)

        case Right(cmd) => log(s"""Executed command $cmd
                                |  took memory fetches = ${memory.doneMemoryFetches}""".stripMargin)
      }
      doneCycles += memory.doneMemoryFetches * 3 + 1 //TODO think about weights of commands or add customization tool
      memory.zeroMemoryFetches()
    }

    log(
      s"""Finished execution round, real used cycles = $doneCycles
          |         with limit       = $cycles""".stripMargin)
    (doneCycles, false)
  }

  def switchContext(context: ProcessorGPRState): Unit =
    for ((gprValue, id) <- context.state.zipWithIndex) gprs.set(GPRS(id), gprValue)

  def getContext: ProcessorGPRState = gprs.clone()

  private def executeCommand(): Instruction = {
    val ip = gprs.get(IP)
    val header = memory.at(ip)
    log(s"Memory(${ip.toInt}, ${ip.toInt + 1}) = $header")

    val cmdProto = supportedInstructions.filter(_.parsable(header))
    if (cmdProto.isEmpty) HaltExecution.raise(s"Processor can not decode $header as supported command header")

    val decodeIterator = memory.view(ip.atOffset(2))
    val cmdToExecute = cmdProto.head.decode(header, decodeIterator)
    val ext = extensions.filter(_.isSupported(cmdToExecute))
    assert(ext.nonEmpty, s"No extension for command $cmdToExecute")

    if(fastVisCallback != null) {
      val ip = gprs.get(IP)
      fastVisCallback(ip, Some(memory.extractMemoryValueNoCost(ip)), memory)
    }

    ext.head.execute(cmdToExecute)

    // Note architecture decision: post-increment of IP always happen (even for Jump, Mov(any to IP) and others).
    // Added offset is equal to number of decoded bytes for instruction.
    // Keep it in mind while implementing new instructions (see how jump instruction modifies IP for example).

    gprs.addTo(IP, /*instruction header*/ 2 + decodeIterator.offset)

    if(fastVisCallback != null) {
      assert (redrawCallback != null)
      fastVisCallback(gprs.get(IP), None, memory)
      redrawCallback()
    }

    cmdToExecute
  }
}

object Processor {

  def SimpleInstructionSet(mem: Memory, gprs: ProcessorGPRState) = new Processor(mem, gprs, Array(
    Core(mem, gprs),
    Stack(mem, gprs),
    Arithmetic(mem, gprs)
  ), (s: String) => ())

}


