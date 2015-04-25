package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Event;
import models.User;
import models.FriendRequest;
import models.utils.AppException;
import models.utils.FriendStatus;
import models.utils.Hash;
import models.utils.MorphiaObject;
import models.utils.RequestStatus;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import play.mvc.Controller;

public class UserController extends Controller {
	
	/***
	 * Saves user being passed in to the database
	 * 
	 * @param user
	 * @return True if user doesn't already exist and saving is successful,
	 * 		   false otherwise
	 */
	public static boolean saveToDatabase(User user) {
		if (checkBeforeSaving(user.email)) {
			try {
				user.password = Hash.createPassword(user.password);
			} catch (AppException e) {
				e.printStackTrace();
			}
			
			String userId = MorphiaObject.datastore.save(user).getId().toString();
			session().put("id", userId);
			
			return true;
		}
		
		return false;
	}
	
	/***
	 * Private helper method that checks if user signing up already exist in
	 * database
	 * 
	 * @param signUpEmail
	 * @return True if does not exist in database (good to save). False if a
	 * 		   user exists with that email
	 * 
	 */
	private static boolean checkBeforeSaving(String signUpEmail) {
		User existingUser = null;
		existingUser = getUserByEmail(signUpEmail);
		return existingUser == null;
	}
	
	/*** 
     * Query the database to find if a user is stored with the email address
     * parameter. If user exist in data, compare the password parameter to the
     * hashed password stored in the database. If Hash matches, return the User,
     * authentication successful
     * 
     * @param email
     * @param clearPassword
     * @return User if authenticated, null if hash fails
     * @throws AppException
     */
    public static User authenticate(String email, String clearPassword) throws AppException {
        // get the user with email only to keep the salt password
        User user = getUserByEmail(email);
        
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.password)) {
                return user;
            }
        }
        
        return null;
    }
    
	public static User getUserById(String id) {
		return MorphiaObject.datastore.createQuery(User.class)
				.filter("_id", new ObjectId(id)).get();
	}
	
	public static User getUserByEmail(String email) {
		return MorphiaObject.datastore.createQuery(User.class)
				.filter("email", email).get();
	}
	
	public static FriendRequest getFriendRequest(String requester, String requestee) {
		return MorphiaObject.datastore.createQuery(FriendRequest.class)
				.filter("requester", requester).filter("requestee", requestee)
				.get();
	}
	
	/***
	 * Queries database for the user... Front end will pass the user that is
	 * currently logged in by the SecuredClass request.username() (cookies).
	 * So this user should exist, but just in case the user doesn't I am going
	 * an exception for error checking. Gets current user and changes its
	 * password to a new hashed password. Updates this user information in
	 * database
	 * 
	 * @TODO: User class may contain getter and setters, use those instead of change instance variables
	 * @param userEmail
	 * @param newPass
	 * @return true if password change was successful. Exception
	 * @throws AppException
	 */
	public boolean changePasswordAndSave(String userEmail, String newPass)
			throws AppException { 
		User currentUser = getUserByEmail(userEmail);
		if(currentUser == null){
			throw new AppException("User does not exist in Database");
			
		}
		
		currentUser.password = Hash.createPassword(newPass);
		MorphiaObject.datastore.save(currentUser);
		return true;
	}

	/**
	 * Checks for the friend status between two users
	 * 
	 * @param user		  The current user
	 * @param profileUser The user to check friend status with
	 * @return			  The friend status between the two users
	 */
	public static FriendStatus getFriendStatus(String user, String profileUser) {
		FriendRequest userRequester = getFriendRequest(user, profileUser);
		FriendRequest userRequestee = getFriendRequest(profileUser, user);
		
		if (userRequester != null) {
			if (userRequester.getStatus() == RequestStatus.ACCEPTED) {
				return FriendStatus.FRIENDS;
			} else {
				return FriendStatus.REQUEST_SENT;
			}
		} else if (userRequestee != null) {
			if (userRequestee.getStatus() == RequestStatus.ACCEPTED) {
				return FriendStatus.FRIENDS;
			} else if (userRequestee.getStatus() == RequestStatus.PENDING) {
				return FriendStatus.REQUEST_RECEIVED;
			} else {
				return FriendStatus.REQUEST_DECLINED;
			}
		} else {
			return FriendStatus.NOT_FRIENDS;
		}
	}

	public static List<Event> publicEventsAttending(String userId) {
		List<Event> publicEvents = new ArrayList<Event>();
		
		Query<Event> eventQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("acceptedUsers")
				.equal(userId).field("isPublic").equal(true);
		List<Event> acceptedEvents = eventQuery.asList();
		publicEvents.addAll(acceptedEvents);
		
		return publicEvents;
	}

	public static List<Event> privateEventsAttending(String userId,
			String profileUserId) {
		List<Event> privateEvents = new ArrayList<Event>();
		
		Query<Event> acceptedEventsQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("acceptedUsers")
				.equal(userId).field("acceptedUsers")
				.equal(profileUserId).field("isPublic").equal(false);
		List<Event> acceptedEvents = acceptedEventsQuery.asList();
		privateEvents.addAll(acceptedEvents);
		
		Query<Event> invitedEventsQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("invitedUsers")
				.equal(userId).field("acceptedUsers")
				.equal(profileUserId).field("isPublic").equal(false);
		List<Event> invitedEvents = invitedEventsQuery.asList();
		privateEvents.addAll(invitedEvents);
		
		Query<Event> declinedEventsQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("declinedUsers")
				.equal(userId).field("acceptedUsers")
				.equal(profileUserId).field("isPublic").equal(false);
		List<Event> declinedEvents = declinedEventsQuery.asList();
		privateEvents.addAll(declinedEvents);
		
		return privateEvents;
	}

	public static List<Event> publicEventsHosting(String userId) {
		List<Event> publicEvents = new ArrayList<Event>();
		
		Query<Event> eventQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("owner")
				.equal(userId).field("isPublic").equal(true);
		List<Event> hostedEvents = eventQuery.asList();
		publicEvents.addAll(hostedEvents);
		
		return publicEvents;
	}

	public static List<Event> privateEventsHosting(String userId,
			String profileUserId) {
		List<Event> privateEvents = new ArrayList<Event>();
		
		Query<Event> acceptedEventsQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("acceptedUsers")
				.equal(userId).field("owner").equal(profileUserId)
				.field("isPublic").equal(false);
		List<Event> acceptedEvents = acceptedEventsQuery.asList();
		privateEvents.addAll(acceptedEvents);
		
		Query<Event> invitedEventsQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("invitedUsers")
				.equal(userId).field("owner").equal(profileUserId)
				.field("isPublic").equal(false);
		List<Event> invitedEvents = invitedEventsQuery.asList();
		privateEvents.addAll(invitedEvents);
		
		Query<Event> declinedEventsQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("declinedUsers")
				.equal(userId).field("owner").equal(profileUserId)
				.field("isPublic").equal(false);
		List<Event> declinedEvents = declinedEventsQuery.asList();
		privateEvents.addAll(declinedEvents);
		
		return privateEvents;
	}

}
