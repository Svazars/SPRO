package org.sims.tests.extensions

import org.sims.processor.ProcessorGPRState
import org.sims.processor.extensions.{Arithmetic, Core, ExtensionModule, Stack}
import org.sims.processor.instructions.Instruction
import org.sims.processor.memory.Memory

object TestExtensions {

  def testAll() = {
    val implementedExtensions = Array[(Memory, ProcessorGPRState) => ExtensionModule](
      Core.apply,
      Stack.apply,
      Arithmetic.apply

    ).map(_(null, null))

    implementedExtensions.foreach(supportsSelf)
    implementedExtensions.foreach(_.instructionSet().foreach(testDecode))
  }

  def testDecode(cmd: Instruction) = {
    val header = cmd.headerContent()
    assert(cmd.parsable(header),
      s"Command $cmd header is equal to $header which is not parsable by the same command")
  }

  def supportsSelf(ext: ExtensionModule) = ext.instructionSet().foreach { cmd =>
    assert(ext.isSupported(cmd),
      s"Extension $ext included $cmd into supportedCommands list but isSuppported fails")
  }
}
