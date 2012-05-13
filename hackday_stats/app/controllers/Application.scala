package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import views._

object Application extends Controller {
  val searchForm = Form(
    "item" -> nonEmptyText)

  def index = Action {
    Ok(views.html.index(searchForm))
  }

  def searchItem = Action {
    implicit request =>
      searchForm.bindFromRequest.fold(
        hasErrors => Ok(views.html.index(searchForm)),
        { 
          
          case (item) => 
            val parser = new JSONParser(search(item))
            val categories_list = processResult(parser.getFilters())
            Ok(views.html.result(categories_list, "aaaaaaa"))
          })
  }

  def processResult(categories: List[Filters]): List[Item] = {
   // categories.flatMap(_._2.values.map(k =>))
    //Item(id: String, title: String, price: Double, sold_quantity: Int, permalink: String, thumbnail: String)
    List(Item("MLAItem1", "Titulo1", 12.01, 10, "http://disney.com", "http://www.bdigital.unal.edu.co/style/images/fileicons/text_html.png"),
        Item("MLAItem2", "Titulo2", 13.01, 11, "http://disney.com", "http://www.bdigital.unal.edu.co/style/images/fileicons/text_html.png"),
        Item("MLAItem3", "Titulo3", 14.01, 12, "http://disney.com", "http://www.bdigital.unal.edu.co/style/images/fileicons/text_html.png"))
  }

  def search(item: String): String = {
    val url = "https://api.mercadolibre.com/sites/MLA/search?q=" + item
    val httpClient = new DefaultHttpClient()
    val httpget = new HttpGet(url)
    val responseHandler = new BasicResponseHandler()
    val responseBody = httpClient.execute(httpget, responseHandler)
    return responseBody
  }
}