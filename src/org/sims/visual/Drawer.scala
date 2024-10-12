package org.sims.visual

import java.awt.Color

import org.sims.Utils
import org.sims.bootstrap.SingleExecutableLoader
import org.sims.processor.GPRS.IP
import org.sims.processor.instructions.control.Halt
import org.sims.processor.memory.{I16, I8}
import org.sims.processor.Processor

object Drawer {

  val refProc: Processor = SingleExecutableLoader(List.empty, offset = 0).loadToFreshProcessor(Processor.SimpleInstructionSet)

  def colorDispatch(value: I16): Color = {
    if (value.equals(I16(0))) {
      Color.WHITE
    } else if (Halt().parsable(value)) {
      Color.RED
    } else if (refProc.supportedInstructions.exists(_.parsable(value))) {
      Color.GREEN
    } else {
      Color.GRAY
    }
  }

  def drawMemory(processor: Processor) = {
    val panel = MemoryVisualizer.panel
    val arenaSizeSqrt: Int = Utils.arenaSizeSqrt(processor)
    for ((word, index) <- Utils.asWords(processor.memory.content).zipWithIndex) {
      //  (0,0) (0, 1)
      //  (1,0) (1, 1)
      //  (2,0) (2, 1)
      val color = colorDispatch(word)
      drawWord(panel, I16(index * 2), color, arenaSizeSqrt)
    }

    drawIP(panel, processor.getContext.get(IP), arenaSizeSqrt)
  }

  def drawIP(panel: DataPanel, addr: I16, arenaSizeSqrt: Int) = drawWord(panel, addr, Color.BLUE, arenaSizeSqrt)

  def drawWord(panel: DataPanel, addr: I16, color: Color, arenaSizeSqrt: Int) = {
    assert(panel.data.length == arenaSizeSqrt)
    assert(panel.data(0).length == arenaSizeSqrt)
    val index = addr.toInt % (arenaSizeSqrt * arenaSizeSqrt)
    val row = index % arenaSizeSqrt
    val column = index / arenaSizeSqrt
    println(s"row = $row, column = $column, arenaSizeSqrt = $arenaSizeSqrt, addr = $addr")
    panel.data(row)(column) = color
    panel.data((row + 1) % arenaSizeSqrt)(column) = color
  }
}
