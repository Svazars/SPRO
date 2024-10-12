package org.sims.visual

import org.sims.processor._
import java.awt.image.BufferedImage
import java.io.{BufferedOutputStream, File, FileOutputStream}
import javax.imageio.ImageIO

import org.sims.Utils
import org.sims.processor.memory.{I16, Memory}

object VisualizedExecution {

  def dumpPanelToImage(dataPanel: DataPanel, filename: String, arenaSizeSqrt: Int): Unit = {
    val bi: BufferedImage = new BufferedImage(arenaSizeSqrt * 20, arenaSizeSqrt * 20, BufferedImage.TYPE_INT_RGB)
    val g = bi.createGraphics()

    dataPanel.paint(g)

    val file = new File(filename)
    val imageOutputStream: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))
    ImageIO.write(bi, "PNG", imageOutputStream)
    imageOutputStream.close()
    g.dispose()
  }

  def initProcessor(proc: Processor, prefix: String): Unit = {
    val callbacks = visualizationCallbacks(prefix, Utils.arenaSizeSqrt(proc))
    proc.slowVisCallback = callbacks._1
    proc.fastVisCallback = callbacks._2
    proc.redrawCallback = callbacks._3
    proc.memory.wordUpdateCallback = (addr: I16, value: Option[I16]) => {
      proc.fastVisCallback(addr, value, proc.memory)
      proc.redrawCallback()
    }
  }

  //                                              addr newvalue/None if it is IP
  private def visualizationCallbacks(prefix: String, arenaSizeSqrt: Int):
  (Processor => Unit,
    (I16, Option[I16], Memory) => Unit,
    () => Unit
    ) = {
    var counter = 0
    val panel = MemoryVisualizer.panel

    val redrawCallback = () => {
      counter += 1
      panel.repaint()
      dumpPanelToImage(panel, prefix + s"_${Utils.trimTo5Digits(counter)}.png", arenaSizeSqrt)
    }

    val fastCallback = (addr: I16, word: Option[I16], mem: Memory) => {
      word match {
        case Some(value) if addr.toInt % 2 == 0 =>
          Drawer.drawWord(panel, addr, Drawer.colorDispatch(value), arenaSizeSqrt)

        case Some(value) if addr.toInt % 2 == 1 =>
          val wordBeforeAddr = addr.atOffset(-1)
          val wordAfterAddr = addr.atOffset(1)
          Drawer.drawWord(panel, wordBeforeAddr, Drawer.colorDispatch(mem.extractMemoryValueNoCost(wordBeforeAddr)), arenaSizeSqrt)
          Drawer.drawWord(panel, wordAfterAddr, Drawer.colorDispatch(mem.extractMemoryValueNoCost(wordAfterAddr)), arenaSizeSqrt)

        case None => Drawer.drawIP(panel, addr, arenaSizeSqrt)
      }
    }

    val slowCallback = (p: Processor) => {
      Drawer.drawMemory(p)
      redrawCallback()
    }

    (slowCallback, fastCallback, redrawCallback)
  }
}
