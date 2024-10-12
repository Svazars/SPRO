package org.sims.processor

class HaltExecution(val msg: String) extends Exception {
  override def getMessage: String = msg
}

object HaltExecution {
  def raise(msg: String) = throw new HaltExecution(msg)
}
