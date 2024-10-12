package org.sims.visual

import org.sims.Utils
import org.sims.processor.Processor
import org.sims.processor.memory.{I16, Memory}

import scala.reflect.io.File

object ReplayRecorder {

  def initProcessor(proc: Processor, prefix: String) : Unit = {
    val callbacks = visualizationCallbacks(prefix)
    proc.slowVisCallback = callbacks._1
    proc.fastVisCallback = callbacks._2
    proc.redrawCallback = callbacks._3
    proc.memory.wordUpdateCallback = (addr:I16, value: Option[I16]) => {
      proc.fastVisCallback(addr, value, proc.memory)
      proc.redrawCallback()
    }
  }

  private def visualizationCallbacks(prefix: String) :
  (  Processor => Unit,
    (I16, Option[I16], Memory) => Unit,
    () => Unit
    ) = {

    val outFile = File(prefix)
    if(outFile.exists) outFile.delete()
    outFile.createFile(failIfExists = true)
    val out = outFile.printWriter()

    val redrawCallback = () => {
      out.println("Redraw")
    }

    val fastCallback = (addr: I16, word: Option[I16], mem: Memory) => word match {
      case Some(value) if addr.toInt % 2 == 0 => out.println(s"Store $addr, $value")
      case Some(value) if addr.toInt % 2 == 1 =>
        for (address <- List(addr.atOffset(-1), addr.atOffset(1))) {
          out.println(s"Store $address, ${mem.extractMemoryValueNoCost(address)}")
        }

      case None => out.println(s"IP $addr")
    }

    val slowCallback = (p:Processor) => {
      for( (word, index) <- Utils.asWords(p.memory.content).zipWithIndex) {
        val addr = I16(index * 2)
        out.println(s"Store $addr, ${p.memory.extractMemoryValueNoCost(addr)}")
      }
      redrawCallback()
    }

    (slowCallback, fastCallback, redrawCallback)
  }

}
