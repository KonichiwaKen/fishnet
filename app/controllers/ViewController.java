package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import models.User;

public class ViewController extends Controller{

	public static Result homePage() {
		String userId = session().get("id");
		User user = UserController.getUserById(userId);
		return ok(views.html.home.render(user));
	}
}