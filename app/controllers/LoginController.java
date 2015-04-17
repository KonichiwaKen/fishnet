package controllers;

import static play.data.Form.form;
import models.User;
import models.utils.AppException;
import play.*;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.*;
import views.html.*;

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
			attemptedUser = User.authenticate(email, password);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Login was successful
		if (attemptedUser != null ) {
			flash("Login Success! ", Messages.get(""));
			return ok(index.render());
		} else {
			Logger.debug("Incorrect username or password");
		}

		return badRequest(index.render());
	}
}
