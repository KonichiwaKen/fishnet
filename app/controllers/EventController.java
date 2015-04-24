package controllers;

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

}
