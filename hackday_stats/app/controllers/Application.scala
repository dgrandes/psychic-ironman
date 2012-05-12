package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import views._

object Application extends Controller {
  val searchForm = Form(
		  "item"->nonEmptyText
      )
      
  def index = Action {
    Ok(views.html.index(searchForm))
  }
  
  def searchItem = Action { 
    implicit request => searchForm.bindFromRequest.fold(
        hasErrors => Ok(views.html.index(searchForm)),
        {case(item) => Ok(search(item))}
        )
  }
  
  def search (item:String):String = {
    val url = "https://api.mercadolibre.com/sites/MLA/search?q=" + item
	val httpClient = new DefaultHttpClient()
	val httpget = new HttpGet(url)
	val responseHandler = new BasicResponseHandler()
	val responseBody = httpClient.execute(httpget, responseHandler)
	return responseBody
  }
}