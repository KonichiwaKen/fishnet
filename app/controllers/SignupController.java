package controllers;

import models.User;
import play.data.Form;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

public class SignupController extends Controller {
	
	public static Result createUser() {
		Form<User> userForm = new Form<User>(User.class);
		User user = userForm.bindFromRequest().get();
		
		if (UserController.saveToDatabase(user)) {
			return ok();
		} else {
			return badRequest();
		}
	}
	
}
