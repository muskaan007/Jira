package com.jira;

import java.util.List;
import java.util.Set;

public class StoryIdList {
	
	private Set<String> storyId;

	@Override
	public String toString() {
		return "StoryIdList [storyId=" + storyId + "]";
	}

	public StoryIdList(Set<String> storyId) {
		super();
		this.storyId = storyId;
	}

	public StoryIdList() {
		// TODO Auto-generated constructor stub
	}

	public Set<String> getStoryId() {
		return storyId;
	}

	public void setStoryId(Set<String> storyId) {
		this.storyId = storyId;
	}

	public void add(String key) {
		
		// TODO Auto-generated method stub
		
	}

}
