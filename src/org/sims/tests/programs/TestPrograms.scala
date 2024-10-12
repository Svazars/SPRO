package org.sims.tests.programs

import org.sims.assembler.Assembler
import org.sims.bootstrap.SingleExecutableLoader
import org.sims.processor.Processor
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.I8

object TestPrograms {

  def runProgram(program: Seq[Instruction], cycles: Int, memoryToUse:Int) = {
    val binaryProg: Seq[I8] = Assembler.asBinary(Assembler.assemble(program)).toSeq

    val processor = SingleExecutableLoader(binaryProg, memoryToUse).loadToFreshProcessor(Processor.SimpleInstructionSet)
    processor.execute(cycles)
  }

  def runAll() = {
    runProgram(HalterN.code(3), 120 * 1024, 1024)
  }

}
