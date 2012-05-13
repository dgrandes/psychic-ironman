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
import scala.collection.immutable.Page

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
            val filters = parser.getFilters()
            Ok(views.html.result(parser.getFilters(), parser.getItems(), query, -1.0))
        })
  }

  def process = Action {
    implicit request =>
      val query = buildQuery(request.queryString)
      val parser = new JSONParser(searchToMELI(query))
      val filters = parser.getFilters()
      val items = parser.getItems()
      val average = processResult(filters,items)
      Ok(views.html.result(parser.getFilters(), items, query, average))
  }

  def processResult(filters: List[Filters],items: List[Item]): Double = {
    if(filters.exists(_.id == "category")){ 
    	-1
    }else{
    	items.foldLeft(0.0)((a,b) => a+b.price)/items.length
    }
    
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