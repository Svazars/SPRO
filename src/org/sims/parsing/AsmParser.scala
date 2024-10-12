package org.sims.parsing

import org.sims.processor.GPRS
import org.sims.processor.instructions.arith._
import org.sims.processor.instructions.control._
import org.sims.processor.instructions.data._
import org.sims.processor.instructions.{Argument, Instruction}
import org.sims.processor.memory.I16


object AsmParser {

  val instructions = Map[String, (Iterator[Argument]) => Instruction](
    "nop" -> Nop.apply,
    "push" -> Push.apply,
    "pop" -> Pop.apply,
    "mov" -> Mov.apply,
    "halt" -> Halt.apply,
    "store" -> Store.apply,
    "load" -> Load.apply,
    "jump" -> Jump.apply,
    "add" -> Add.apply,
    "sub" -> Sub.apply,
    "mul" -> Mul.apply,
    "xor" -> Xor.apply,
    "or" -> Or.apply,
    "and" -> And.apply,
    "jumpzero" -> JumpZero.apply,
    "jumpequals" -> JumpEquals.apply,
    "storewithoffset" -> StoreWithOffset.apply
  )

  def parseProgram(text: TraversableOnce[String]) = text.map(Utils.eraseComments).filter(!_.isEmpty).map(parseInstruction)

  private def parseInstruction(line: String): Instruction = {
    assert(line.equals(Utils.eraseComments(line)))
    val txt = line.trim.toLowerCase

    val (name, args) = txt.indexOf(' ') match {
      case -1 => (txt, Array.empty[String])
      case firstSpace => (txt.substring(0, firstSpace),
        txt.substring(firstSpace + 1).split(",").map(_.trim))
    }

    if (!instructions.contains(name.toLowerCase)) {
      throw new IllegalArgumentException(s"Line <$line> can not be parsed as known instruction with name <$name>")
    }

    instructions(name)(args.map(decodeArgument).iterator)
  }

  private def decodeInteger(arg: String): Argument = {
    try {
      Argument(I16(Integer.parseInt(arg)))
    } catch {
      case _: NumberFormatException =>
        throw new IllegalArgumentException(s"Argument <$arg> is not valid register or integer immediate")
    }
  }

  private def decodeArgument(arg: String): Argument = {
    GPRS.asGpr(arg) match {
      case Some(gpr) => Argument(gpr)
      case None => decodeInteger(arg)
    }
  }
}
