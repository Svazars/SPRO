package org.sims.processor.extensions

import org.sims.processor.GPRS.{IP, SP}
import org.sims.processor.instructions.control._
import org.sims.processor.instructions.data._
import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.memory.{I16, Memory}
import org.sims.processor.{HaltExecution, ProcessorGPRState}

/**
 * Core extension module aggregates instructions that seems to be really necessary for writing
 * any non-trivial programs.
 *
 * Arithmetic operations, even add and sub, intentionally placed to separate module, called {@link Arithmetic}.
 *
 * Stack operations can be found in Stack extension module.
 *
 * TODO: Jump*, which are just syntactic sugar for {mov, add IP, sub IP} combination, must be moved to separate Jumps extension.
 */
class Core(override val memory: Memory, override val gprs: ProcessorGPRState) extends ExtensionModule(memory, gprs) {
  override def instructionSet(): Set[Instruction] = Set(
    Mov(), Store(), Load(),
    Jump(), Halt(), Nop(),
    JumpZero(), JumpEquals()
  )

  override def execute(cmd: Instruction): Unit = {
    assert(isSupported(cmd))
    cmd match {

      case Jump(addr) =>
        //      instruction        addr constant
        val offset = 2 + (if (addr.isImmediate) 2 else 0)
        setGPR(IP, argumentContent(addr).atOffset(-offset))

      case JumpZero(tst, addr) =>
        //      instruction        tst constant                       addr constant
        val offset = 2 + (if (tst.isImmediate) 2 else 0) + (if (addr.isImmediate) 2 else 0)

        if (argumentContent(tst).equals(I16(0))) {
          setGPR(IP, argumentContent(addr).atOffset(-offset))
        }

      case JumpEquals(x, y, addr) =>
        //      instruction        x constant                       y constant                     addr constant
        val offset = 2 + (if (x.isImmediate) 2 else 0) + (if (y.isImmediate) 2 else 0) + (if (addr.isImmediate) 2 else 0)

        if (argumentContent(x).equals(argumentContent(y))) {
          setGPR(IP, argumentContent(addr).atOffset(-offset))
        }

      case Load(loadAddress, dstGPR) => setGPR(dstGPR, memoryAt(argumentContent(loadAddress)))
      case Store(value, dstAddr) => memoryUpdate(value, dstAddr)
      case Mov(src, dst) => setGPR(dst, src)
      case _: Halt => HaltExecution.raise("Halt instruction executed")
      case _: Nop => // what have you expected?
    }
  }
}

object Core {
  def apply(memory: Memory, gprs: ProcessorGPRState) = new Core(memory, gprs)
}
