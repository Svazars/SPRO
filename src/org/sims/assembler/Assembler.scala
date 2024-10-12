package org.sims.assembler

import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.{I8, I16}

object Assembler {

  def assemble(instr: Instruction) : IterableOnce[I16] = Seq(instr.headerContent()) ++
    instr.args
      .filter(arg => arg.isDefined && arg.get.content.isLeft)
      .map(_.get.content.left.get)

  def assemble(program: IterableOnce[Instruction]) : IterableOnce[I16] = program.flatMap(Assembler.assemble)

  def asBinary(program: IterableOnce[I16]) : IterableOnce[I8] = program.flatMap(s => List(s.high, s.low))
}
