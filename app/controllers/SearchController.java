package controllers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import models.utils.FriendStatus;
import models.utils.MorphiaObject;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.mvc.Controller;
import play.mvc.Result;

public class SearchController extends Controller {

	public static Result searchUsers() {
		String currentUserId = session().get("id");
		String query = request().getQueryString("query");
		Query<User> userQuery = MorphiaObject.datastore.createQuery(User.class)
				.field("_id").notEqual(currentUserId);
		Map<User, Double> similarUsers = new HashMap<User, Double>();

		for (User user : userQuery) {
			double firstSimilarity = similarity(query, user.getFirstName()) * 0.5;
			double lastSimilarity = similarity(query, user.getLastName()) * 0.5;
			double fullSimilarity = similarity(query, user.getFullName());

			double similarity = firstSimilarity > lastSimilarity
					? firstSimilarity : lastSimilarity;
			similarity = fullSimilarity > similarity
					? fullSimilarity: similarity;

			if (similarity >= 0.3) {
				similarUsers.put(user, similarity);
			}
		}

		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ArrayNode searchResults = nodeFactory.arrayNode();

		for (User similarUser : similarUsers.keySet()) {
			ObjectNode searchResult = nodeFactory.objectNode();

			searchResult.put("id", similarUser.getId());
			searchResult.put("name", similarUser.getFullName());
			searchResult.put("similarity", similarUsers.get(similarUser));

			FriendStatus friendStatus = UserController.getFriendStatus(
					currentUserId, similarUser.getId());
			searchResult.put("friendStatus", friendStatus.toString());

			searchResults.add(searchResult);
		}

		return ok(searchResults);
	}

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {
		String longer = s1;
		String shorter = s2;

		if (s1.length() < s2.length()) {
			longer = s2;
			shorter = s1;
		}

		int longerLength = longer.length();

		if (longerLength == 0) {
			return 1.0;
		}

		return (longerLength - StringUtils.getLevenshteinDistance(longer,
				shorter)) / (double) longerLength;

	}

}
