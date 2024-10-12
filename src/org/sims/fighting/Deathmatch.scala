package org.sims.fighting


import org.sims.Utils
import org.sims.bootstrap.SingleExecutableLoader
import org.sims.processor.GPRS.IP
import org.sims.processor.{Processor, ProcessorGPRState}
import org.sims.processor.memory.{I16, I8}
import org.sims.visual._

import scala.collection.mutable
import scala.reflect.io.{Directory, File}
import scala.util.Random


case class ParticipantDescription(programName: String, program: Seq[I8], extraSteps: Int)

class Deathmatch(val participants: Seq[ParticipantDescription], val maxTurns: Int, val arenaSizeInBytes: Int) {
  require(participants.length > 1)
  require(maxTurns > 0)
  require(arenaSizeInBytes > 0)

  private val MINIMAL_SIZE = 20
  private val fractionSize = arenaSizeInBytes / (2 * participants.length)

  var suddenDeathCounter = 0

  require(fractionSize > MINIMAL_SIZE, s"${participants.length} participants in deathmatch in area ${arenaSizeInBytes} will not fit, need at least ${MINIMAL_SIZE} per participant + buffer")

  private val offsets = new Array[Int](participants.length)

  for ((p, i) <- participants.zipWithIndex) {
    require(p.extraSteps >= 0)
    require(p.program.length <= fractionSize)
    require(p.program.length % 2 == 0)

    val offs = Utils.alignDown(i * (arenaSizeInBytes / participants.length) + Random.nextInt(fractionSize / 2), 2)
    assert(offs % 2 == 0)
    assert(I16(offs).toInt == offs)
    offsets(i) = offs
  }

  var doneTurns = 0
  var logCallback: (String) => Unit = println

  /**
   * @return num of player that survived, -1 if a tie
   */
  def start(visMode: VisualizationMode): Int = {

    var mem = zeroMem(offsets(0))
    for ((p, i) <- participants.zipWithIndex) {
      logCallback(s"Loading player${i} at offset ${offsets(i)}")
      mem = mem ++ p.program
      if (i < participants.length - 1) {
        val bytesToNext = offsets(i + 1) - offsets(i) - p.program.length
        mem = mem ++ zeroMem(bytesToNext)
      }
    }

    logCallback(s"Max turns: $maxTurns")
    logCallback(s"Visualization mode: $visMode")

    val proc = SingleExecutableLoader(mem, 0, arenaSizeInBytes).loadToFreshProcessor(Processor.SimpleInstructionSet)
    proc.log = logCallback

    val executeCallbacks = visMode match {
      case NoVisualization => false
      case vis: VisualizationMode =>
        vis match {
          case DumpImages(d) => VisualizedExecution.initProcessor(proc, d.path + File.separator + "deathmatch")
          case rep: RecordReplay => ReplayRecorder.initProcessor(proc, rep.pathToReplay())
        }
        proc.slowVisCallback(proc)
        true
    }

    gameLoop(proc, executeCallbacks)
  }

  private def gameLoop(proc: Processor, doVis: Boolean): Int = {
    var totalCycles = 0
    var doneCycles = 0
    doneTurns = 0

    val p2ctx = mutable.Map.empty[Int, ProcessorGPRState]

    for ((_, i) <- participants.zipWithIndex) {
      val ctx = ProcessorGPRState()
      ctx.set(IP, I16(offsets(i)))
      p2ctx.put(i, ctx)
    }

    while (true) {
      logCallback(s"Turn = $doneTurns, cycles spent = $totalCycles")

      for ((i, ctx) <- p2ctx) {
        logCallback(s"Restoring player ${i} context")
        proc.switchContext(ctx)

        if (doVis) {
          proc.fastVisCallback(proc.gprs.get(IP), None, proc.memory)
        }

        doneCycles = turn(proc, participants(i).extraSteps)
        totalCycles += doneCycles

        val newCtx = proc.getContext
        p2ctx.put(i, newCtx)

        if (doneCycles == 0) {
          logCallback(s"Player ${i} is DEAD")
          p2ctx.remove(i)
        }

        if (p2ctx.keys.size == 1) {
          val winner = p2ctx.keys.head
          logCallback(s"The only one survivor left, it is player $winner, congratulations!")
          return winner
        }
      }

      doneTurns += 1

      if (doneTurns > maxTurns) {
        suddenDeathCounter += 1
      }

      if (suddenDeathCounter > 0) {
        logCallback(s"Sudden death counter = $suddenDeathCounter")
        val spoiledFrom = Random.nextInt(proc.memory.content.length)
        for (i <- 0 until suddenDeathCounter) {
          val addr = (spoiledFrom + i) % proc.memory.content.length
          logCallback(s"Storing halt opcode at $addr")
          proc.memory.content(addr) = I8(0x08) // HALT opcode
        }
      }

      // logCallback(s"Out of turns")
      //        throw new OutOfIterationsException()
    }
    assert(false)
    -1
  }

  private def zeroMem(length: Int): Seq[I8] = {
    val result = new Array[I8](length)
    (0 until length).foreach(i => result(i) = I8(0))
    result
  }

  private def cyclesToDo(extra: Int) = 50 + extra + Random.nextInt(25)

  private def turn(proc: Processor, extra: Int): Int = {
    val cycles = cyclesToDo(extra)
    val (real, halted) = proc.execute(cycles)
    logCallback(s"Executed $real cycles")
    if (halted) 0 else real
  }
}


class OutOfIterationsException extends Exception

sealed abstract class VisualizationMode(baseDir: Directory)
case object NoVisualization extends VisualizationMode(null)

case class DumpImages(d: Directory) extends VisualizationMode(d) {
  d.createDirectory(force = true, failIfExists = false)
}

case class RecordReplay(d: Directory, name: String = "fight") extends VisualizationMode(d) {
  d.createDirectory(force = true, failIfExists = false)
  def pathToReplay(): String = d.path + File.separator + name + ".replay"
}






