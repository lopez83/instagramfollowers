package com.olopez.instagramfollowers.model;

import org.json.JSONObject;

public class User {

	private String id;
	private String profilePicture;
	private String username;
	private String fullName;
	private String website;
	private String bio;
	private boolean followedByMe;

	public User() {
		super();
	}

	public User(JSONObject json) {
		id = json.optString("id");
		profilePicture = json.optString("profile_picture");
		username = json.optString("username");
		fullName = json.optString("fullName");
		website = json.optString("website");
		bio = json.optString("bio");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
	
	public boolean isFollowedByMe() {
		return followedByMe;
	}

	public void setFollowedByMe(boolean followedByMe) {
		this.followedByMe = followedByMe;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof User))
			return false;
		User that = (User) obj;
		if (this.id.equalsIgnoreCase(that.id)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}