package org.sims.visual

import org.sims.Utils
import org.sims.processor.memory.I16

import scala.io.Source
import scala.reflect.io.{Directory, File}

object ReplayVisualizer {
  def preprocessLine(line: String): String = line.toLowerCase.trim

  private def parseWord(txt: String) = {
    val data = txt.trim
    assert(data.startsWith("0x"), s"Error parsing word $data")
    I16(Integer.parseInt(data.substring(2), 16))
  }

  private def parseLine(line: String): Option[Command] = {
    if (line.startsWith("redraw")) {
      Some(Command(Redraw, null))
    } else if (line.startsWith("ip")) {
      Some(Command(Ip, Seq(parseWord(line.substring(3)))))
    } else if (line.startsWith("store")) {
      Some(Command(Store, line.substring(5).split(",").map(parseWord).toSeq))
    } else {
      None
    }
  }

  def parseReplay(txt: IterableOnce[String]) =
    txt.map(preprocessLine).map(parseLine).filter(_.isDefined).map(_.get)

  def parseReplayFile(file: String) = parseReplay(Source.fromFile(file).getLines())

  def visualizeReplay(replay: IterableOnce[Command], timeMarks: Set[Int], dir: Directory, areaSizeSqrt: Int) = {
    assert(timeMarks.nonEmpty)
    assert (dir.exists)

    val (panel, frame) = MemoryVisualizer.createPanelOnSeparateFrame(areaSizeSqrt)
    frame.setVisible(true)
    var redrawsFound = 0
    var imageCount = 0

    for (Command(tpe, args) <- replay) {tpe match {
      case Redraw =>
        redrawsFound += 1
        if (timeMarks.contains(redrawsFound)) {
          val targetFile = dir.path + File.separator + s"replay_${Utils.trimTo5Digits(imageCount)}.png"
          println(s"Redraw to $targetFile")
          panel.repaint()
          VisualizedExecution.dumpPanelToImage(panel, targetFile, areaSizeSqrt)
          println(s"Redraw #$imageCount done")
          imageCount += 1
        }

      case Ip => Drawer.drawIP(panel, args.head, areaSizeSqrt)
      case Store => Drawer.drawWord(panel, args.head, Drawer.colorDispatch(args(1)), areaSizeSqrt)
    }}

    frame.dispose()
  }
}

sealed abstract class CommandType

case object Store extends CommandType

case object Ip extends CommandType

case object Redraw extends CommandType

case class Command(tpe: CommandType, args: Seq[I16])
