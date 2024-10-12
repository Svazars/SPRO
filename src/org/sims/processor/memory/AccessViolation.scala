package org.sims.processor.memory

import org.sims.processor.HaltExecution

class AccessViolation(override val msg: String) extends HaltExecution(msg) {}

object AccessViolation {
  def raise(info: String) = throw new AccessViolation("Access violation: " + info)
}
