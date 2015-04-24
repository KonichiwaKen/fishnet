package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;
import views.html.index;
import models.User;

public class ViewController extends Controller {
	
	public static Result loginPage() {
		String userId = session().get("id");
		
        if (userId != null) {
            User user = UserController.getUserById(userId);
            
            if (user != null ) {
                return redirect(routes.ViewController.homePage());
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }
        
		return ok(index.render());
	}

	public static Result homePage() {
		String userId = session().get("id");
		User user = UserController.getUserById(userId);
		
		return ok(home.render(user));
	}
}