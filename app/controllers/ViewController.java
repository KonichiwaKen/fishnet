package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;
import views.html.index;
import models.User;

public class ViewController extends Controller {
	
	public static Result loginPage() {
		return ok(index.render());
	}

	public static Result homePage() {
		String userId = session().get("id");
		User user = UserController.getUserById(userId);
		return ok(home.render(user));
	}
}