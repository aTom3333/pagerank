package models

class Page(override val id: Int, val name: String, var rank: Double = 1.0, var linked: Array[Page] = Array()) extends Info(id) {

  override def toString: String = {s"Page($id, $name, $rank, [" + linked.map(p => p.name).reduce((a, b) => a+", "+b)+"])"}
}
