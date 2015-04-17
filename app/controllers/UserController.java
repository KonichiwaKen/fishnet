package controllers;

import org.mongodb.morphia.query.Query;

import models.User;
import models.utils.AppException;
import models.utils.Hash;
import models.utils.MorphiaObject;
import play.mvc.Controller;

public class UserController extends Controller{
	/***
	 * Saves user being passed in to the databse
	 * @param user
	 * @return true if user doesnt already exist and saving is successful. false otherwise
	 */
	public static boolean saveToDatabase(User user){
		/**TODO: Change parameter to form.. user creation should happen on backend
		 * 		user.password = Hash.createPassword(form.get().password)
		 */
		if(checkBeforeSaving(user.email)) {
			try {
				user.password = Hash.createPassword(user.password);
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MorphiaObject.datastore.save(user);
			return true;
		}
		return false;
		
	}
	
	/***
	 * Private Helper method --> checks if user signing up already exist in database
	 * @param signUpEmail
	 * @return true if okay does not exist in database (good to save)...... return false if a user exist by that email
	 * 
	 */
	private static boolean checkBeforeSaving(String signUpEmail){
		User existingUser = null;
		existingUser = getUserByEmail(signUpEmail);
		return(existingUser == null);
	}
	
	/*** 
     * Query the databse to find if a user is stored with the email address parameter
     * If user exist in data, compare the password parameter to the hashed password stored 
     * in the database.
     * 
     * if Hash matches: return the User. authentication successful
     * @param email
     * @param clearPassword
     * @return User if authenticated, null if hash fails
     * @throws AppException
     */
    public User authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        User user = getUserByEmail(email);
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword,user.password)) {
                return user;
            }
        }
        return null;
    }
	public static User getUserByEmail(String email){
		return getEmailUserFind(email).get();
	}
	
	private static Query<User> getEmailUserFind(String email) {
		return MorphiaObject.datastore.createQuery(User.class).filter("email", email);
	}
	
	/***
	 * Queries database for the user... Front end will pass the user that is currenttly logged in by the 
	 * SecuredClass request.username() (cookies).. So this user should exist, but just incase the user doesn't
	 * I am going an exception for error checking...Gets current user and changes its password to a new hashed
	 * password. Updates this user information in database
	 * 
	 * @TODO: User class may contain getter and setters, use those instead of change instance variables
	 * @param userEmail
	 * @param newPass
	 * @return true if password change was successful. Exception
	 * @throws AppException
	 */
	public boolean changePasswordAndSave(String userEmail, String newPass) throws AppException{ 
		User currentUser = getUserByEmail(userEmail);
		if(currentUser == null){
			throw new AppException("User does not exist in DataBase");
			
		}
		
		currentUser.password = Hash.createPassword(newPass);
		MorphiaObject.datastore.save(currentUser);
		return true;
	}

}
