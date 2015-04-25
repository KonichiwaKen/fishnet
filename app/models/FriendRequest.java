package models;

import models.utils.RequestStatus;

import org.mongodb.morphia.annotations.Id;

public class FriendRequest {
	
	@Id
	public String id;
	public String requester;
	public String requestee;
	public RequestStatus status;
	
	public FriendRequest(String requester, String requestee) {
		this.requester = requester;
		this.requestee = requestee;
		status = RequestStatus.PENDING;
	}
	
	public String getId() {
		return id;
	}

	public RequestStatus getStatus() {
		return status;
	}
	
}
