package models

class RankUpdate(override val id: Int, val value: Double) extends Info(id) {

  override def toString = s"RankUpdate($id, $value)"
}
