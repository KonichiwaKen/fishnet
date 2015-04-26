package controllers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.friends;
import views.html.home;
import views.html.login;
import views.html.profile;
import models.Event;
import models.User;
import models.utils.FriendStatus;

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
        
		return ok(login.render());
	}

	public static Result homePage() {
		String userId = session().get("id");
		User user = UserController.getUserById(userId);
		
		return ok(home.render(user));
	}
	
	public static Result profilePage() throws IOException {
		String userId = session().get("id");
		User user = UserController.getUserById(userId);
		String profileUserId = request().getQueryString("user");
		User profileUser = UserController.getUserById(profileUserId);
		
		FriendStatus friendStatus = UserController
				.getFriendStatus(userId, profileUserId);
		
		List<Event> eventsAttending = UserController
				.publicEventsAttending(profileUserId);
		List<Event> eventsHosting = UserController
				.publicEventsHosting(profileUserId);
		
		if (friendStatus == FriendStatus.FRIENDS) {
			List<Event> privateEventsAttending = UserController
					.privateEventsAttending(userId, profileUserId);
			List<Event> privateEventsHosting = UserController
					.privateEventsHosting(userId, profileUserId);
			
			if (!privateEventsAttending.isEmpty()) {
				eventsAttending.addAll(privateEventsAttending);
			}
			
			if (!privateEventsHosting.isEmpty()) {
				eventsHosting.addAll(privateEventsHosting);
			}
		}
		
		String eventsAttendingJsonString = new Gson().toJson(eventsAttending);
		String eventsHostingJsonString = new Gson().toJson(eventsHosting);
		
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode profileJson = nodeFactory.objectNode();
		JsonNode eventsAttendingJson = mapper
				.readTree(eventsAttendingJsonString);
		JsonNode eventsHostingJson = mapper.readTree(eventsHostingJsonString);
		
		profileJson.put("friendStatus", friendStatus.toString());
		profileJson.put("eventsAttending", eventsAttendingJson);
		profileJson.put("eventsHosting", eventsHostingJson);
		
		return ok(profile.render(user, profileUser, profileJson.toString()));
	}
	
	public static Result friendsPage() {
		String userId = session().get("id");
		User user = UserController.getUserById(userId);
		
		return ok(friends.render(user));
	}
	
}
