package org.sims.visual

import java.awt._

import javax.swing.JFrame
import org.sims.Utils

object MemoryVisualizer {

  // def reinit(memSize: Int): Unit = {
  //   val (p, f) = createPanelOnSeparateFrame(Utils.arenaSizeSqrt(memSize))
  //   panel = p
  // }

  var panel: DataPanel = null

  def createPanelOnSeparateFrame(areaSizeSqrt: Int): (DataPanel, JFrame) = {
    val p = new DataPanel(Array.ofDim[Color](areaSizeSqrt, areaSizeSqrt))
    val frame = new JFrame()
    frame.setSize(20 * areaSizeSqrt, 20 * areaSizeSqrt)
    frame.getContentPane.add(p)
    panel = p
    (panel, frame)
  }
}
