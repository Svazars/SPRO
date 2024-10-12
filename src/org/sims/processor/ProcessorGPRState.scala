package org.sims.processor

import org.sims.processor.GPRS.GPR
import org.sims.processor.memory.I16

class ProcessorGPRState(val state: Array[I16]) {
  require(state.length == 7)
  require(!state.contains(null))

  def addTo(gpr: GPR, n: Int) = set(gpr, I16(get(gpr).toInt + n))
  def get(gpr: GPR) = state(gpr.id)
  def set(gpr: GPR, value: I16) = state.update(gpr.id, value)
  override def clone(): ProcessorGPRState = new ProcessorGPRState(state.clone())
}

object ProcessorGPRState {
  def apply() = {
    val gprs = new Array[I16](7)
    (0 to 6).foreach(i => gprs(i) = I16(0))
    new ProcessorGPRState(gprs)
  }
}
