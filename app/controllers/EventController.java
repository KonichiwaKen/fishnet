package controllers;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.google.gson.Gson;

import models.Event;
import models.utils.MorphiaObject;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class EventController extends Controller {
	
	public static Result createEvent() {
		Form<Event> eventForm = new Form<Event>(Event.class);
		Event event = eventForm.bindFromRequest().get();
		event.setOwner(session().get("id"));
		saveToDatabase(event);
		
		return ok();
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

}
