package org.sims.debugger

import scala.io.Source
import scala.util.control.Exception.allCatch
import org.sims.processor.{GPRS, Processor}
import org.sims.visual.VisualizedExecution

import scala.reflect.io.{Directory, File}

class CommandLineDebug(val processor: Processor, doVisualization: Boolean) {
  val dbg = new Debugger(processor)

  if (doVisualization) {
    VisualizedExecution.initProcessor(processor,
      Directory("./vis-dbg").createDirectory(force = true).path + File.separator + "dbg")
  }

  val WORDS_TO_DUMP_DEFAULT = 5

  val cmds = List(
    "help",
    "em [cycles: Int] - emulates defined number of cycles (some execution patterns are atomic. so it`s not very strict)",
    "dmp - dumps registers",
    s"dmp [fromAddr: Int] - dumps $WORDS_TO_DUMP_DEFAULT words starting from address",
    "dmp [fromAddr: Int, wordsToDump: Int]",
    s"dmp ip - dumps $WORDS_TO_DUMP_DEFAULT words starting from addres at ip (works for other regs too)",
    "vrbon - enables processor verbose mode",
    "vrboff - disables processor verbose mode",
    "exit")

  def emulate(args: Seq[String]) : Int = {
    if (args.length != 1) {
      println(s"Error: Expected one argument to emulate instruction")
    } else {
      allCatch.opt(args.head.toInt) match {
        case Some(cycles) => return dbg.emulate(cycles, println)
        case None => println(s"Error: Argument to emulate instruction <${args.head}> is not int")
      }
    }
    0
  }

  def dump(args: Seq[String]) : Unit = {
    if (args.isEmpty) {
      dbg.dumpRegs(println)
    } else if (args.length == 1) {

      GPRS.asGpr(args.head) match {
        case Some(gpr) =>
          dbg.dumpReg(gpr, WORDS_TO_DUMP_DEFAULT, println)
          return
        case _ =>
      }

      allCatch.opt(args.head.toInt) match {
        case Some(fromAddr) => dbg.dumpMemory(fromAddr, WORDS_TO_DUMP_DEFAULT, println)
        case None => println(s"Error: Argument to dump instruction <${args.head}> is not int")
      }
    } else if (args.length == 2) {
      val from = allCatch.opt(args(0).toInt)
      val wc = allCatch.opt(args(1).toInt)

      (from, wc) match {
        case (Some(fromAddr), Some(count)) => dbg.dumpMemory(fromAddr, count, println)
        case (None, _) => println(s"Error: First argument to dump instruction <${args(0)}> is not int")
        case (_, None) => println(s"Error: Second argument to dump instruction <${args(1)}> is not int")
      }
    } else {
      println(s"Error: Unexpected number of parameters to dump instruction")
    }
  }

  def parseCommand(cmd: String): Int = {
    val txt = cmd.toLowerCase.trim
    val parts = txt.split(' ').map(_.trim).filter(!_.isEmpty)

    val name = parts.head
    val args = parts.tail

    name match {
      case "exit" => return -1
      case "help" => println("Supported commands:\n   " + cmds.mkString("\n   "))
      case "em" => return emulate(args)
      case "dmp" => dump(args)
      case "vrbon" => dbg.enableVerbose()
      case "vrboff" => dbg.disableVerbose()
      case _ =>
        println(s"Error: Command name <$name> not recognized!")
        println("Supported commands:\n   " + cmds.mkString("\n   "))
    }

    0
  }

  def getUserInput: String = Source.fromInputStream(System.in).getLines().filter(!_.trim.isEmpty).next()

  def startDebug() : Unit = {
    println(" ============== Debug session started =========== ")
    println(s" Memory size   : ${processor.memory.content.length} bytes")

    var cycles = 0
    println(s"Cycles done: $cycles")
    dbg.dumpRegs(println)

    processor.slowVisCallback(processor)

    try {
      while (true) {
        val cyclesDone = parseCommand(getUserInput)
        if (cyclesDone == -1) return
        cycles += cyclesDone
        println(s"Cycles from start done: $cycles")
      }
    } finally {
      println(" ============== Debug session terminated =========== ")
    }
  }
}


object CommandLineDebug {
  def apply(proc: Processor, doVisualization: Boolean): CommandLineDebug = new CommandLineDebug(proc, doVisualization)
}
