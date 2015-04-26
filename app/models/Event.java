package models;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("Events")
public class Event {
	
	@Id
	public String id;
	public String title;
	public String owner;
	public String description;
	public String startTime;
	public String endTime;
	public String location;
	public boolean isPublic;
	public List<String> invitedUsers;
	public List<String> acceptedUsers;
	public List<String> declinedUsers;
	
	public Event() {
		invitedUsers = new ArrayList<String>();
		acceptedUsers = new ArrayList<String>();
		declinedUsers = new ArrayList<String>();
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public List<String> getInvitedUsers() {
		return invitedUsers;
	}

	public boolean inviteUser(String user) {
		if (invitedUsers.contains(user) || acceptedUsers.contains(user) || declinedUsers.contains(user)) {
			return false;
		} else {
			invitedUsers.add(user);
			return true;
		}
	}
	
}
