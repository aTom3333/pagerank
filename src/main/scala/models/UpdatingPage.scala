package models

class UpdatingPage(val page: Page, var sumOfOther: Double = 0.0) extends Info(page.id) {
  def update(dampingFactor: Double): Page = {
    new Page(id, page.name, (1-dampingFactor) + dampingFactor * sumOfOther, page.linked)
  }


  override def toString = s"UpdatingPage($page, $sumOfOther)"
}
