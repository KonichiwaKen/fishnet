package models;

import models.utils.RequestStatus;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("FriendRequests")
public class FriendRequest {
	
	@Id
	public String id;
	public String requester;
	public String requestee;
	public RequestStatus status;
	
	public FriendRequest() {}
	
	public FriendRequest(String requester, String requestee) {
		this.requester = requester;
		this.requestee = requestee;
		status = RequestStatus.PENDING;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRequester() {
		return requester;
	}
	
	public String getRequestee() {
		return requestee;
	}

	public RequestStatus getStatus() {
		return status;
	}

}
