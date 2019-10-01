import models.{Info, Page, RankUpdate, UpdatingPage}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object Main extends App {

  def createGraph(sparkContext: SparkContext): RDD[(Int, Info)] = {
    val pages = Array(
      new Page(0, "A"),
      new Page(1, "B"),
      new Page(2, "C"),
      new Page(3, "D"),
    )
    pages(0).linked +:= pages(1)
    pages(0).linked +:= pages(2)

    pages(1).linked +:= pages(2)

    pages(2).linked +:= pages(0)

    pages(3).linked +:= pages(2)

    sparkContext.makeRDD(pages).map(p => (p.id, p))
  }


  val conf: SparkConf = new SparkConf()
    .setAppName("Pagerank")
    .setMaster("local[*]")

  val sparkContext: SparkContext = new SparkContext(conf)
  sparkContext.setLogLevel("ERROR")

  var graph = createGraph(sparkContext)


  for(i <- 0 to 20) {
    // Map
    graph = graph.flatMap(content => {
      val (id, info) = content
      info match {
        case page: Page =>
          var infos = Array[(Int, Info)]((id, new UpdatingPage(page))) // Resend Page as UpdatingPage
          for (linked <- page.linked) {
            infos +:= (linked.id, new RankUpdate(linked.id, page.rank / page.linked.length))
          }
          infos
        case _ =>
          throw new Exception("Unexpected type")
      }
    })

    // Reduce
    graph = graph.reduceByKey((info1, info2) => {
      (info1, info2) match {
        case (update1: RankUpdate, update2: RankUpdate) =>
          new RankUpdate(info1.id, update1.value + update2.value)
        case (page: UpdatingPage, update: RankUpdate) =>
          new UpdatingPage(page.page, page.sumOfOther+update.value)
        case (update: RankUpdate, page: UpdatingPage) =>
          new UpdatingPage(page.page, page.sumOfOther+update.value)
        case _ =>
          throw new Exception("Unexpected types")
      }
    })

    // Bonus map
    graph = graph.map(content => {
      val (id, page) = content
      page match {
        case page: UpdatingPage =>
          (id, page.update(0.85))
        case _ =>
          throw new Exception("Unexpected type")
      }
    })

    println("Iteration "+i)
    graph.foreach(content => println(content))

  }



}
