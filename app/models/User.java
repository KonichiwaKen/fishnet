package models;

import models.utils.MorphiaObject;
import models.utils.AppException;
import models.utils.Hash;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.query.Query;

@Entity("Users")
public class User {

	@Id
	public String id;  //@Id field is filled in for you (after the save)
	
	public String firstName;
	public String lastName;
	public String email;
	public String password;
	
	public User() {}
	
	public User(String firstName, String lastName, String email, String password){
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}
	
	/**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    public static User findByEmail(String email) {
        return getEmailUserFind(email).get();
    }
    
    private static Query<User> getEmailUserFind(final String email) {
		return MorphiaObject.datastore.createQuery(User.class).filter("email", email);
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
    public static User authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        User user = findByEmail(email);
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword,user.password)) {
                return user;
            }
        }
        return null;
    }
}
