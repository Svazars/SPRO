package org.sims.processor

object GPRS {

  def apply(id: Int) = id match {
    case 0 => IP
    case 1 => SP
    case 2 => R1
    case 3 => R2
    case 4 => R3
    case 5 => R4
    case 6 => R5
  }

  def asGpr(arg: String): Option[GPR] = arg.toUpperCase match {
    case "IP" => Some(IP)
    case "SP" => Some(SP)
    case "R1" => Some(R1)
    case "R2" => Some(R2)
    case "R3" => Some(R3)
    case "R4" => Some(R4)
    case "R5" => Some(R5)
    case _ => None
  }

  sealed abstract class GPR(val id: Int)

  case object IP extends GPR(0)

  case object SP extends GPR(1)

  case object R1 extends GPR(2)

  case object R2 extends GPR(3)

  case object R3 extends GPR(4)

  case object R4 extends GPR(5)

  case object R5 extends GPR(6)

}
