package controllers;

import models.User;
import models.utils.AppException;

import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.index;

public class LoginController extends Controller {
	
	public static Result index() {
		return ok(index.render());

	}

	public static Result attemptLogin() {
		Form<User> userForm = new Form<User>(User.class);
		User user = userForm.bindFromRequest().get();	

		String email = user.email;
		String password = user.password;
		
		User attemptedUser = null;
		
		try {
			attemptedUser = UserController.authenticate(email, password);
		} catch (AppException e) {
			e.printStackTrace();
		}
		
		//Login was successful
		if (attemptedUser != null ) {
			session("id", attemptedUser.id);
			return ok();
		} else {
			Logger.debug("Incorrect username or password");
		}

		return badRequest();
	}
	
}
