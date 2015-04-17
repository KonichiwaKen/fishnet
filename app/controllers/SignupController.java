package controllers;

import models.User;
import play.data.Form;
import play.mvc.Result;
import views.html.index;
import views.html.badRequest;

public class SignupController extends UserController{
	
	// TODO: Return ok?
	public static Result createUser() {
		Form<User> userForm = new Form<User>(User.class);
		User user = userForm.bindFromRequest().get();
		if (saveToDatabase(user)) {
			return ok(index.render());
		} else {
			return badRequest(badRequest.render());
		}
	}
}
