import org.sims.Utils
import org.sims.assembler.Assembler
import org.sims.bootstrap.SingleExecutableLoader
import org.sims.debugger.CommandLineDebug
import org.sims.fighting.{Deathmatch, OutOfIterationsException, ParticipantDescription, RecordReplay}
import org.sims.parsing.{AsmParser, BinParser}
import org.sims.processor.Processor
import org.sims.visual._

import scala.collection.mutable
import scala.io.Source
import scala.reflect.io.{Directory, File}
import scala.util.Random

/**
 * Stack In-Memory Simulator
 */
object Main {

  def writeToFile(filename: String, data: TraversableOnce[String]): Unit = {
    val writer = File(filename).printWriter()
    data.foreach(writer.write)
    writer.close()
  }

  def replay(path: String, step: Int, dstDir: Directory, arenaSize: Int) = {
    println(s"Restoring visualization by replay: $path")
    val file = File(path)
    assert(file.exists, s"Expected $file to exist")
    println(s"Erasing ${dstDir.path}")
    dstDir.deleteRecursively()
    dstDir.createDirectory(force = true)

    val replay = ReplayVisualizer.parseReplayFile(file.path).toArray
    val redraws = replay.count { cmd =>
      cmd.tpe match {
        case Redraw => true
        case _ => false
      }
    }

    println(s"Total redraws = $redraws, step = $step")
    val timeMarks = (1 to redraws - 100 by step).toSet ++ (redraws - 100 to redraws)
    ReplayVisualizer.visualizeReplay(replay, timeMarks, dstDir, Utils.arenaSizeSqrt(arenaSize))
  }

  def main(arg: Array[String]): Unit = {
    println("Args: " + arg.mkString(","))
    if (arg.length < 1) {
      printUsage
      System.exit(-1)
    }

    if (arg.head.toLowerCase.trim.equals("replay")) {
      doReplay(arg)
      System.exit(0)
    }

    if (arg.head.toLowerCase.trim.equals("replays")) {
      doReplays(arg)
      System.exit(0)
    }

    if (arg.head.toLowerCase.trim.equals("deathmatch")) {
      doDeathmatch(arg)
      System.exit(0)
    }


    val filename = arg(0)

    if (filename.endsWith(".asm")) {
      doAssemble(filename)
      System.exit(0)
    }

    if (filename.endsWith(".bin") && arg.length == 4 && arg(1).trim.toLowerCase.equals("exec")) {
      val instr = Integer.parseInt(arg(2).trim)
      val memSize = Integer.parseInt(arg(3).trim)
      doExec(filename, instr, memSize)
      System.exit(0)
    }

    if (filename.endsWith(".bin") && arg.length == 3 && arg(1).trim.toLowerCase.equals("debug")) {
      val memSize = Integer.parseInt(arg(2).trim)
      doDebug(filename, memSize, enableVisualization = true)
      System.exit(0)
    }

    if (filename.endsWith(".bin") && arg.length == 3 && arg(1).trim.toLowerCase.equals("debug-novis")) {
      val memSize = Integer.parseInt(arg(2).trim)
      doDebug(filename, memSize, enableVisualization = false)
      System.exit(0)
    }

    if (filename.endsWith(".bin") && arg.length == 4 && arg(1).trim.toLowerCase.equals("vis")) {
      val instr = Integer.parseInt(arg(2).trim)
      val memSize = Integer.parseInt(arg(3).trim)

      doVisuzalize(filename, instr, memSize)
      System.exit(0)
    }

    printUsage
    System.exit(-1)
  }

  private def doVisuzalize(filename: String, instr: Int, memSize: Int) = {
    print(s"Loading $filename binary file ... ")
    val binary = BinParser.parseBinary(Source.fromFile(filename).getLines()).toArray
    println(" success")

    print(s"Loading binary data (${binary.length} bytes) to memory of size ${memSize} ...")
    val proc = SingleExecutableLoader(binary, offset = 0, memory = memSize).loadToFreshProcessor(Processor.SimpleInstructionSet)
    println(" success")

    println(s"Start visualizing for $instr instructions")

    print(s"Initializing debug environment ...")
    val dbg = CommandLineDebug(proc, true)
    println(" success")

    assert(false)
    // MemoryVisualizer.createPanelOnSeparateFrame(Utils.arenaSizeSqrt(memSize))//.setVisible(true)


    VisualizedExecution.initProcessor(proc,
      Directory("./vis").createDirectory(force = true).path + File.separator + "test")
    proc.slowVisCallback(proc)
    for (_ <- 1 to instr) {
      proc.execute(1)
    }
  }

  private def doDebug(filename: String, memSize: Int, enableVisualization: Boolean) = {
    print(s"Loading $filename binary file ... ")
    val binary = BinParser.parseBinary(Source.fromFile(filename).getLines()).toArray
    println(" success")

    print(s"Loading binary data (${binary.length} bytes) to memory of size ${memSize} ...")
    val proc = SingleExecutableLoader(binary, offset = 0, memory = memSize).loadToFreshProcessor(Processor.SimpleInstructionSet)
    println(" success")

    print(s"Initializing debug environment ...")
    val dbg = CommandLineDebug(proc, enableVisualization)
    println(" success")

    if (enableVisualization) {
      assert(false)
      // MemoryVisualizer.createPanelOnSeparateFrame(Utils.arenaSizeSqrt(memSize))//.setVisible(true)
    }
    dbg.startDebug()
  }

  private def doExec(filename: String, instr: Int, memSize: Int) = {
    print(s"Loading $filename binary file ... ")
    val binary = BinParser.parseBinary(Source.fromFile(filename).getLines()).toArray
    println(" success")

    print(s"Loading binary data (${binary.length} bytes) to memory of size ${memSize} ...")
    val proc = SingleExecutableLoader(binary, offset = 0, memory = memSize).loadToFreshProcessor(Processor.SimpleInstructionSet)
    println(" success")

    print(s"Initializing debug environment ...")
    val dbg = CommandLineDebug(proc, false)
    println(" success")

    dbg.dbg.enableVerbose()
    dbg.emulate(Seq(Integer.toString(instr)))
    dbg.dbg.dumpRegs(println)
  }

  private def doAssemble(filename: String) = {
    val binname = filename.stripSuffix(".asm") ++ ".bin"
    println(s"Assembling $filename into $binname")
    val code = Assembler.assemble(AsmParser.parseProgram(Source.fromFile(filename).getLines()))
    writeToFile(s"$binname", code.map(_.toHex + " "))
  }

  private def doDeathmatch(arg: Array[String]): Unit = {
    val maxTurns = Integer.parseInt(arg(1))
    val arenaSize = Integer.parseInt(arg(2))
    val seed = Integer.parseInt(arg(3))

    println(s"Seed: ${seed}")
    Random.setSeed(seed)

    val b = mutable.ArrayBuffer.empty[ParticipantDescription]
    var p = 0
    var i = 4
    while (i < arg.length) {
      val f = arg(i)
      val extra = Integer.parseInt(arg(i + 1))

      print(s"Loading player's $p $f binary file ... ")
      val binary = BinParser.parseBinary(Source.fromFile(f).getLines()).toArray
      println(" success")

      println(s"Extra cycles per context switch for player $p: ${extra}")
      b.addOne(ParticipantDescription(f, binary, extra))

      i += 2
      p += 1
    }

    println(s"Max turns: ${maxTurns}")
    println(s"Arena size: ${arenaSize}")

    val arena = new Deathmatch(b.toSeq, maxTurns, arenaSize)

    try {
      val visMode = RecordReplay(Directory("."))
      println(s"Recordind replay at ${visMode.pathToReplay()}")
      val wonNum = arena.start(visMode)
      println(s"Winner is $wonNum <${b(wonNum).programName}>")
    } catch {
      case e: OutOfIterationsException =>
        println("OutOfIterrations! It's a tie.")
    }
  }

  private def doReplays(arg: Array[String]) = {
    assert(arg.length == 3)
    val replaysDir = Directory(Directory(".").path + File.separator + arg(1))
    val step = Integer.parseInt(arg(2))
    assert(replaysDir.exists)
    replaysDir.files.toArray.foreach({ file => {
      val dstDir = Directory(replaysDir + File.separator + file.name.replace(".replay", ""))
      assert(false)
      replay(replaysDir.path + File.separator + file.name, step, dstDir, 0)
    }
    })
  }

  private def doReplay(arg: Array[String]) = {
    assert(arg.length == 4)
    val dstDir = Directory(Directory(".").path + "vis-replay")
    dstDir.createDirectory(force = true)
    replay(arg(1), Integer.parseInt(arg(2)), dstDir, Integer.parseInt(arg(3)))
  }

  private def printUsage = {
    println("Expected parameters: <file_name.asm>")
    println("                     <file_name.bin> exec [cycles] [memory]")
  }
}
