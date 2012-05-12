package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

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
        {case(item) => Ok("Searching MELI for "+item)}
        )
  }
  
}