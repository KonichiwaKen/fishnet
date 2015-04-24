package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecurityController extends Security.Authenticator {

	@Override
    public String getUsername(Context ctx) {
        return ctx.session().get("email");
    }

	/***
	 * getUsername is used to get the username of the current logged in user.
	 * In our case this is the email address, that we set in the email attribute
	 * in the session when the user logged in. If this method returns a value,
	 * then the authenticator considers the user to be logged in, and lets the
	 * request proceed. If however the method returns null, then the
	 * authenticator will block the request, and instead invoke onUnathorized,
	 * which we have implemented to redirect to our login screen (index).
	 */
//    @Override
//    public Result onUnauthorized(Context ctx) {
//       return redirect(views.LoginController.index());
//    }
    
}
