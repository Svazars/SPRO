package org.sims.visual

import java.awt.{Color, Graphics}
import javax.swing.JPanel

class DataPanel(val data: Array[Array[Color]]) extends JPanel {

  override def paintComponent(g: Graphics) = paintToGraphics(g)

  def paintToGraphics(g: Graphics) = {
    val bounds = g.getClipBounds()
    val dx = if(bounds!=null) g.getClipBounds.width.toFloat / data.length else 512.0
    val dy = if(bounds!=null) g.getClipBounds.height.toFloat / data.map(_.length).max else 512.0

    for {
      x <- data.indices
      y <- data(x).indices
      x1 = (x * dx).toInt
      y1 = (y * dy).toInt
      x2 = ((x + 1) * dx).toInt
      y2 = ((y + 1) * dy).toInt
    } {
      data(x)(y) match {
        case c: Color => g.setColor(c)
        case _ => g.setColor(Color.WHITE)
      }
      g.fillRect(x1, y1, x2 - x1, y2 - y1)
    }
  }
}
