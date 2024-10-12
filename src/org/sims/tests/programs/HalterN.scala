package org.sims.tests.programs

import org.sims.processor.GPRS
import org.sims.processor.instructions.arith.Sub
import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.instructions.control.{Halt, Jump, Nop}
import org.sims.processor.instructions.data.{Mov, Store}
import org.sims.processor.memory.I16

/**
  * HalterN is a simple program that stores HALT instructions at
  *   [ lastMemoryByte - 1      , lastMemoryByte      ] bytes
  *   [ lastMemoryByte - 1 - N  , lastMemoryByte - N  ] bytes
  *   [ lastMemoryByte - 1 - 2*N, lastMemoryByte - 2*N] bytes
  *   ...
  *  until kills itself.
  */
object HalterN {

  def code(N: Int): Seq[Instruction] = {
    val R1 = AsArgument.gpr(GPRS.R1)
    val R2 = AsArgument.gpr(GPRS.R2)

    Array(
      Mov(Argument(Halt().headerContent()), R1), // R1 contains data to write
      Mov(Argument(I16(-2)), R2),                // R2 pointer to yet unmodified memory
      Store(R1, R2),
      Sub(R2, Argument(I16(N)), R2),
      Jump(Argument(I16(8)))
    )
  }
}
