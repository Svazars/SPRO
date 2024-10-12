package org.sims.tests.programs

import org.sims.processor.GPRS
import org.sims.processor.GPRS.GPR
import org.sims.processor.instructions.Argument

object AsArgument {

  def gpr(gpr: GPR): Argument= Argument(GPRS(gpr.id))

}
