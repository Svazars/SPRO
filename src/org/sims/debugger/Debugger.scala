package org.sims.debugger

import org.sims.processor.GPRS._
import org.sims.processor.{GPRS, Processor}
import org.sims.processor.memory.I16

class Debugger(val processor: Processor) {

  def enableVerbose() {
    processor.log = println
  }

  def disableVerbose() {
    processor.log = (s:String) => ()
  }

  def emulate(cycles :Int, log: String => Unit): Int = {
    log(s"Emulate $cycles cycles")
    val (doneCycles, halted) = processor.execute(cycles)

    if(halted) {
      log(s"Processor Halted, execution stopped")
    }

    log(s"IP = ${processor.gprs.get(IP)}")
    doneCycles
  }

  def dumpMemory(from :Int, wordsCount :Int, log: String => Unit) : Unit = {
    log(s"Dumping $wordsCount words, starting from $from-th byte:")

    for (index <- from to from + wordsCount * 2 by 2) {
      val ptr = I16(index)
      val data = processor.memory.extractMemoryValueNoCost(ptr)
      val prefix = if(ptr.equals(processor.getContext.get(IP))) "->" else "  "
      log(s" $prefix  ${ptr.toInt} : $data")
    }
  }

  def dumpRegs(log: String => Unit) : Unit = {
    log(s"Dumping regs:")
    for (gpr <- List(IP, SP, R1, R2, R3, R4, R5)) {
      log(s" $gpr: ${processor.getContext.get(gpr)}")
    }
  }

  def dumpReg(reg: GPRS.GPR, wordsCount: Int, log: String => Unit) : Unit = {
    log(s"Dumping reg [$reg]:")
    dumpMemory(processor.getContext.get(reg).toInt, wordsCount, log)
  }
}
