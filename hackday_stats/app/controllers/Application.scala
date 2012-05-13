package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import org.apache.http.client.ResponseHandler
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import views._

object Application extends Controller {
  val searchForm = Form(
    "q" -> nonEmptyText)

  def index = Action {
    Ok(views.html.index(searchForm))
  }

  def search = Action {
    implicit request =>
      searchForm.bindFromRequest.fold(
        hasErrors => Ok(views.html.index(searchForm)),
        {

          case (q) =>
            val query = "q=" + q
            val parser = new JSONParser(searchToMELI(query))
            val filters = processResult(parser.getFilters())
            Ok(views.html.result(parser.getFilters(), query))
        })
  }

  def process = Action {
    implicit request =>
      val query = buildQuery(request.queryString)
      val parser = new JSONParser(searchToMELI(query))
      val filters = processResult(parser.getFilters())
      Ok(views.html.result(parser.getFilters(), query))
  }

  def processResult(filters: List[Filters]): List[Filters] = {
     // categories.flatMap(_._2.values.map(k =>))
    //Item(id: String, title: String, price: Double, sold_quantity: Int, permalink: String, thumbnail: String)
    //List(Item("MLAItem1", "Titulo1", 12.01, 10, "http://disney.com", "http://www.bdigital.unal.edu.co/style/images/fileicons/text_html.png"),
    //Item("MLAItem2", "Titulo2", 13.01, 11, "http://disney.com", "http://www.bdigital.unal.edu.co/style/images/fileicons/text_html.png"),
    //Item("MLAItem3", "Titulo3", 14.01, 12, "http://disney.com", "http://www.bdigital.unal.edu.co/style/images/fileicons/text_html.png"))
    filters
  }

  def buildQuery(queryMap: Map[String, Seq[String]]): String = {
    queryMap.keys.toList.sortWith(_.length < _.length).map(f => f + "=" + queryMap(f)(0) + "&").foldLeft("")((a, b) => a + b).dropRight(1)
  }

  def searchToMELI(query: String): String = {
    val url = "https://api.mercadolibre.com/sites/MLA/search?" + query
    httpRequest(url)
  }

  def httpRequest(url: String): String = {
    val httpClient = new DefaultHttpClient()
    val httpget = new HttpGet(url)
    val responseHandler = new BasicResponseHandler()
    httpClient.execute(httpget, responseHandler)
  }
}