package controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import models.FriendRequest;
import models.utils.MorphiaObject;
import models.utils.RequestStatus;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

public class FriendRequestController extends Controller {
	
	public static Result createRequest() {
		String userId = session().get("id");
		String profileUserId = request().body().asJson().get("user").asText();
		
		Logger.debug(userId);
		Logger.debug(profileUserId);
		
		if (checkFriendRequest(userId, profileUserId) != null) {
			return badRequest();
		}
		
		FriendRequest request = new FriendRequest(userId, profileUserId);
		
		if (saveToDatabase(request) != null) {
			return ok();
		} else {
			return badRequest();
		}
	}
	
	public static Result acceptRequest() {
		String userId = session().get("id");
		String profileUserId = request().getQueryString("user");
		String requestId = checkFriendRequest(userId, profileUserId, "requestee");
		
		if (requestId == null) {
			return badRequest();
		}
		
		Query<FriendRequest> query = MorphiaObject.datastore
				.createQuery(FriendRequest.class)
				.filter("_id", new ObjectId(requestId));
		UpdateOperations<FriendRequest> ops = MorphiaObject.datastore
				.createUpdateOperations(FriendRequest.class)
				.set("status", RequestStatus.ACCEPTED);
		MorphiaObject.datastore.update(query, ops);
		
		return ok();
	}
	
	public static Result declineRequest() {
		String userId = session().get("id");
		String profileUserId = request().getQueryString("user");
		String requestId = checkFriendRequest(userId, profileUserId, "requestee");
		
		if (requestId == null) {
			return badRequest();
		}
		
		Query<FriendRequest> query = MorphiaObject.datastore
				.createQuery(FriendRequest.class)
				.filter("_id", new ObjectId(requestId));
		UpdateOperations<FriendRequest> ops = MorphiaObject.datastore
				.createUpdateOperations(FriendRequest.class)
				.set("status", RequestStatus.DECLINED);
		MorphiaObject.datastore.update(query, ops);
		
		return ok();
	}

	private static String checkFriendRequest(String userId,
			String profileUserId, String type) {
		Query<FriendRequest> query;
		
		if (type.equals("requester")) {
			query = MorphiaObject.datastore
					.createQuery(FriendRequest.class).field("requester")
					.equal(userId).field("requestee").equal(profileUserId);
		} else if (type.equals("requestee")) {
			query = MorphiaObject.datastore
					.createQuery(FriendRequest.class).field("requester")
					.equal(profileUserId).field("requestee").equal(userId);
		} else {
			return null;
		}
		
		FriendRequest request = query.get();
		
		return request != null ? request.getId() : null;
	}

	private static String checkFriendRequest(String userId,
			String profileUserId) {
		String requesterRequest = checkFriendRequest(userId, profileUserId, "requester");
		String requesteeRequest = checkFriendRequest(userId, profileUserId, "requestee");
		
		if (requesterRequest != null) {
			return requesterRequest;
		} else if (requesteeRequest != null) {
			return requesteeRequest;
		} else {
			return null;
		}
	}
	
	public FriendRequest getRequestById(String requestId) {
		return MorphiaObject.datastore.createQuery(FriendRequest.class)
				.filter("_id", new ObjectId(requestId)).get();
	}
	
	public static String saveToDatabase(FriendRequest request) {
		String requestId = MorphiaObject.datastore.save(request)
				.getId().toString();
		return requestId;
	}
	
}
