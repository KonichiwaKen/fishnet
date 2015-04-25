package models;

import models.utils.RequestStatus;

import org.mongodb.morphia.annotations.Id;

public class FriendRequest {
	
	@Id
	public String id;
	public String requester;
	public String requestee;
	public RequestStatus status;
	
	public RequestStatus getStatus() {
		return status;
	}
	
}
