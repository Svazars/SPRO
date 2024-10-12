package org.sims.bootstrap

import org.sims.Utils
import org.sims.processor.GPRS.{IP, SP}
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.{I8, I16}

object SingleExecutableLoader {
  def apply(data: Seq[I8], offset: Int = 0, memory: Int = Utils.addressedMemory) = Loader(memory)
    .setupGPR(IP, I16(offset))
    .writeData(offset, data)
}
