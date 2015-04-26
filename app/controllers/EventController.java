package controllers;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.google.gson.Gson;

import models.Event;
import models.FriendRequest;
import models.utils.MorphiaObject;
import models.utils.RequestStatus;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class EventController extends Controller {
	
	public static Result createEvent() {
		Form<Event> eventForm = new Form<Event>(Event.class);
		Event event = eventForm.bindFromRequest().get();
		event.setOwner(session().get("id"));
		String eventId = saveToDatabase(event);
		
		if(eventId != null) {
			return ok(eventId);
		} else {
			return badRequest();
		}
	}
	
	/***
	 * Saves event being passed in to the database
	 * 
	 * @param event
	 * @return MongoDB document ID if event doesn't already exist and saving is
	 * 		   successful, false otherwise
	 */
	public static String saveToDatabase(Event event) {
		String eventId = MorphiaObject.datastore.save(event).getId().toString();
		return eventId;
	}
	
	public static Result getEvents() {
		String currentUser = session().get("id");
		List<Event> userEvents = getEvents(currentUser);
		String eventsJson = new Gson().toJson(userEvents);
		return ok(eventsJson);
	}
	
	public static List<Event> getEvents(String userId) {
		Query<Event> eventQuery =  MorphiaObject.datastore
				.createQuery(Event.class).field("owner").equal(userId);
		List<Event> userEvents = eventQuery.asList();
		
		return userEvents;
	}
	
	public static Result deleteEvent() {
		String eventId = request().getQueryString("eventId");
		Datastore ds = MorphiaObject.datastore;
		
		Query<Event> eventQuery = ds.createQuery(Event.class)
				.filter("_id", new ObjectId(eventId));
		Event deletedEvent = ds.findAndDelete(eventQuery);
		
		if (deletedEvent != null) {
			return ok();
		} else {
			return badRequest();
		}
	}
	
	public static Result inviteToEvent() {
		String currentUser = session().get("id");
		String eventId = request().getQueryString("event");
		String user = request().getQueryString("user");
		
		Query<Event> eventQuery = MorphiaObject.datastore
				.createQuery(Event.class).field("_id").equal(new ObjectId(eventId));
		eventQuery.or(
				eventQuery.criteria("owner").equal(currentUser),
				eventQuery.criteria("isPublic").equal(true)
		);
		
		Event event = eventQuery.get();
		
		if (event == null) {
			return badRequest();
		} else if (!event.inviteUser(user)) {
			return badRequest();
		} else {
			UpdateOperations<Event> eventOps = MorphiaObject.datastore
					.createUpdateOperations(Event.class)
					.set("invitedUsers", event.getInvitedUsers());
			MorphiaObject.datastore.update(eventQuery, eventOps);
			
			return ok();
		}
	}
	
	public static Result acceptEventInvite() {
		String currentUser = session().get("id");
		String eventId = request().getQueryString("event");
		
		Query<Event> eventQuery = MorphiaObject.datastore.createQuery(Event.class)
				.field("_id").equal(new ObjectId(eventId))
				.field("invitedUsers").equal(currentUser);
		
		Event event = eventQuery.get();
		
		if (event == null) {
			return badRequest();
		} else {
			event.acceptUser(currentUser);
			UpdateOperations<Event> eventOps = MorphiaObject.datastore
					.createUpdateOperations(Event.class)
					.set("invitedUsers", event.getInvitedUsers())
					.set("acceptedUsers", event.getAcceptedUsers());
			MorphiaObject.datastore.update(eventQuery, eventOps);
			
			return ok();
		}
	}
	
	public static Result declineEventInvite() {
		String currentUser = session().get("id");
		String eventId = request().getQueryString("event");
		
		Query<Event> eventQuery = MorphiaObject.datastore.createQuery(Event.class)
				.field("_id").equal(new ObjectId(eventId))
				.field("invitedUsers").equal(currentUser);
		
		Event event = eventQuery.get();
		
		if (event == null) {
			return badRequest();
		} else {
			event.declineUser(currentUser);
			UpdateOperations<Event> eventOps = MorphiaObject.datastore
					.createUpdateOperations(Event.class)
					.set("invitedUsers", event.getInvitedUsers())
					.set("declinedUsers", event.getDeclinedUsers());
			MorphiaObject.datastore.update(eventQuery, eventOps);
			
			return ok();
		}
	}

}
