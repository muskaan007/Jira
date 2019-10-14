package com.jira;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class JiraClient {

	private static final String JIRA_URL = "https://agile-jira.pearson.com";
	private static final String JIRA_ADMIN_USERNAME = "VBorsAm";
	private static final String JIRA_ADMIN_PASSWORD = "Dad@1996";

	public static void main(String[] args) throws Exception {

		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		URI uri = null;
		try {
			uri = new URI(JIRA_URL);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME,
				JIRA_ADMIN_PASSWORD);

		//System.out.println(getFeatureIdFromTeam(client));
		//getAllStoryIdsOfFeatureId(client);
		//System.out.println(storyCount(client));
		//getAllStoryIdsStatus(client);
		//System.out.println(storyPoints(client) );
		
		System.out.println("STORY COUNT IN CURRENT SPRINT");
		openSprint(client).forEach((k,v)->System.out.println("Item : " + k + " Count : " + v+" "));
		
		System.out.println("STORY POINTS SUM IN CURRENT SPRINT \n");
		storyPoints(client).forEach((k,v)->System.out.println("Item : " + k + " Count : " + v+" "));
		

		System.out.println("Example complete. Now exiting.");
		client.close();
	}

	static Map<String, Set<String>> getFeatureIdFromTeam(JiraRestClient client) {

		Map<String, Set<String>> teamFeatures = new HashMap<>();
		Set<String> teamFeatureList = new HashSet<String>();

		Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(
				"project = \"Global Learning Platform\" AND issuetype = Story AND Sprint in openSprints() ORDER BY status DESC, cf[16230] ASC, component ASC");

		List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();

		//get team features
		for (Issue issue : res) {
			String teamName = null;

			//getting team name
			Iterator<BasicComponent> itr = issue.getComponents().iterator();
			while (itr.hasNext()) {
				teamName = itr.next().getName();
			}

			//saving team Fearture id into its key set
			if (teamFeatures.get(teamName) == null) {
				teamFeatureList = new HashSet<String>();
				if(issue.getFieldByName("Theme").getValue()!=null)
					teamFeatureList.add(issue.getFieldByName("Theme").getValue().toString());
			} else {
				teamFeatureList = teamFeatures.get(teamName.toString());
				if(issue.getFieldByName("Theme").getValue()!=null)
					teamFeatureList.add(issue.getFieldByName("Theme").getValue().toString());
			}

			teamFeatures.put(teamName, teamFeatureList);
		}
		return teamFeatures;
	}

	@SuppressWarnings("null")
	public static void getAllStoryIdsOfFeatureId(JiraRestClient client) {

		Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(
				"project = \"Global Learning Platform\" AND issuetype = Story AND Sprint in openSprints() ",101,0,null);

		List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();

		//Map<feature id, List of story id>
		Map<String,Set<String>>  mapFeatureIds = new HashMap<>();
		//	List<String> storyId=new ArrayList<String>();
		Set<String>storyIds = new HashSet<String>();
		int c=0;

		for (Issue issue : res) {
			//getting feature id name
			
			if(issue.getFieldByName("Theme").getValue() != null) {
			c+=1;
				String featureId= issue.getFieldByName("Theme").getValue().toString();
				
				if (!mapFeatureIds.containsKey(featureId)) {
					//new feature id
					storyIds.add(issue.getKey());
				}
				else {
					//feature id already present in the list
					storyIds=mapFeatureIds.get(featureId);
					storyIds.add(issue.getKey());
				}
					mapFeatureIds.put(featureId,storyIds);

		}
		}
		System.out.println("count= "+c);
		System.out.println("List : "+mapFeatureIds);
	}
	
	public static void getAllStoryIdsStatus(JiraRestClient client) {

		Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(
				"project = \"Global Learning Platform\" AND issuetype = Story AND Sprint in openSprints() ",101,0,null);

		List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();

		//Map<feature id, story id and status>
		Map<String,Set<Map<String,String>>>  mapFeatureIds = new HashMap<>();
		//	List<String> storyId=new ArrayList<String>();
		Map<String,String> mapStoryStatus=new HashMap<String, String>();
		Set<Map<String, String>>storyIds = new HashSet<>();
		int c=0;

		for (Issue issue : res) {
			//getting feature id name
			
			if(issue.getFieldByName("Theme").getValue() != null) {
			//c+=1;
				String featureId= issue.getFieldByName("Theme").getValue().toString();
				c+=1;
				
				if (!mapFeatureIds.containsKey(featureId)) {
					//new feature id
					mapStoryStatus.put(issue.getKey(),issue.getStatus().getName());
				}
				else {
					//feature id already present in the list
					
					storyIds=mapFeatureIds.get(featureId);
					
					mapStoryStatus.put(issue.getKey(),issue.getStatus().getName());
				
				}
				storyIds.add(mapStoryStatus);
					mapFeatureIds.put(featureId,storyIds);

		}
		}
		
		System.out.println("List : "+mapFeatureIds);
		System.out.println("count  = "+c);
	}




public static Map<String, ThemePojo> storyCount(JiraRestClient client) {
	int a= 0;
	Map<String, ThemePojo> featureStory = new HashMap<>();
	ThemePojo themePojo = null;
	//String sp="Sprint 34";

	Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(
			"project = \"Global Learning Platform\" AND issuetype  = Story AND Theme != null AND Subtype IN (\"QA Only\",Refactoring,\"Tech Debt\",\"User Story\",\"Tech Story\")",101,0,null);

	List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();

	for (Issue issue : res) {

		
		if (issue.getFieldByName("Theme").getValue() != null) {
			a+=1;
			String featureId = issue.getFieldByName("Theme").getValue().toString();
			String storyStatus=issue.getStatus().getName().toString();

			if (!featureStory.containsKey(featureId)) {
				themePojo = new ThemePojo();
				themePojo.setTotal(themePojo.getTotal() + 1);
				
				if(storyStatus.equals("Open"))
					themePojo.setYetToBePickedUp(themePojo.getYetToBePickedUp() + 1);
				else if(storyStatus.equals("In Progress"))
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + 1);
				else if(storyStatus.equals("Done"))
					themePojo.setCompleted(themePojo.getCompleted()+1);
				
			} else {
				themePojo = featureStory.get(featureId);
				themePojo.setTotal(themePojo.getTotal() + 1);
				if(storyStatus.equals("Open"))
					themePojo.setYetToBePickedUp(themePojo.getYetToBePickedUp() + 1);
				else if(storyStatus.equals("In Progress"))
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + 1);
				else if(storyStatus.equals("Done"))
					themePojo.setCompleted(themePojo.getCompleted()+1);
			}

			featureStory.put(featureId, themePojo);
		}
	}
	
	System.out.println("total Stories : " + a+"\n\n");
	return featureStory;
}

public static Map<String, ThemePojo> allStoryCount(JiraRestClient client) {
	int a= 0;
	Map<String, ThemePojo> featureStory = new HashMap<>();
	ThemePojo themePojo = null;
	String sp="Sprint 34";

	Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql(
			"project = \"Global Learning Platform\" AND issuetype  = Story AND Theme != null AND Subtype IN (\"QA Only\",Refactoring,\"Tech Debt\",\"User Story\",\"Tech Story\")",101,0,null);

	List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();

	for (Issue issue : res) {

		
		//if (issue.getFieldByName("Theme").getValue() != null) {
			a+=1;
			String featureId = issue.getFieldByName("Theme").getValue().toString();
			String storyStatus=issue.getStatus().getName().toString();

			if (!featureStory.containsKey(featureId)) {
				themePojo = new ThemePojo();
				themePojo.setTotal(themePojo.getTotal() + 1);
				
				
				
				if(storyStatus.equals("Open"))
					themePojo.setYetToBePickedUp(themePojo.getYetToBePickedUp() + 1);
				else if(storyStatus.equals("In Progress"))
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + 1);
				else if(storyStatus.equals("Done"))
					themePojo.setCompleted(themePojo.getCompleted()+1);
				
			} else {
				themePojo = featureStory.get(featureId);
				themePojo.setTotal(themePojo.getTotal() + 1);
				if(storyStatus.equals("Open"))
					themePojo.setYetToBePickedUp(themePojo.getYetToBePickedUp() + 1);
				else if(storyStatus.equals("In Progress"))
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + 1);
				else if(storyStatus.equals("Done"))
					themePojo.setCompleted(themePojo.getCompleted()+1);
			}

			featureStory.put(featureId, themePojo);	
	}
	
	System.out.println("total Stories : " + a+"\n\n");
	return featureStory;
}


public static Map<String, ThemePojo> openSprint(JiraRestClient client) {
	int a= 0;
	int c=0;
	Map<String, ThemePojo> featureStory = new HashMap<>();
	ThemePojo themePojo=null;

	Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql("project = \"Global Platform ART\"  and labels = PI10Committed and status = Develop");

	List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();
	List<String> featureIds=new ArrayList<String>();

	for (Issue issue : res) {
		//System.out.println(issue.getKey());
		featureIds.add(issue.getKey());
	}
	String s=featureIds.toString().substring(1,10*featureIds.size() - 1);
	System.out.println("feature id list : " +s);
	
	Promise<SearchResult> searchJqlPromise1 = client.getSearchClient().searchJql("project = \"Global Learning Platform\" AND Component != null AND Theme in ("+s+") and Sprint in openSprints() AND Subtype in (\"User Story\",\"QA Only\",\"Refactoring\", \"Tech Story\", \"Tech Debt\")",100,0,null);

	List<Issue> res1 = (List<Issue>) searchJqlPromise1.claim().getIssues();
	
for (Issue issue : res1) {
		
			a+=1;
			String featureId = issue.getFieldByName("Theme").getValue().toString();
			String storyStatus=issue.getStatus().getName().toString();
		
			if (!featureStory.containsKey(featureId)) {
				themePojo = new ThemePojo();
				themePojo.setTotal(themePojo.getTotal() + 1);
				
				if(storyStatus.equals("Done") || storyStatus.equals("Closed"))
					{themePojo.setCompleted(themePojo.getCompleted()+1);
					c+=1;
					}
				else
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + 1);	
				
			} else {
				themePojo = featureStory.get(featureId);
				themePojo.setTotal(themePojo.getTotal() + 1);
				
				if(storyStatus.equals("Done") || storyStatus.equals("Closed") || storyStatus.equals("Cancel"))
					{themePojo.setCompleted(themePojo.getCompleted()+1);
					c+=1;
					}
				else
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + 1);
			}

			featureStory.put(featureId, themePojo);	
	}
	
	System.out.println("total Stories : " + a+"\n");
	return featureStory;

}

public static Map<String, ThemePojo> storyPoints(JiraRestClient client) {
	int a= 0;
	int c=0;
	Map<String, ThemePojo> featureStory = new HashMap<>();
	ThemePojo themePojo=null;

	Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql("project = \"Global Platform ART\"  and labels = PI10Committed and status = Develop");

	List<Issue> res = (List<Issue>) searchJqlPromise.claim().getIssues();
	List<String> featureIds=new ArrayList<String>();

	for (Issue issue : res) {
		//System.out.println(issue.getKey());
		featureIds.add(issue.getKey());
	}
	String s=featureIds.toString().substring(1,10*featureIds.size() - 1);
	System.out.println("feature id list : " +s);
	
	Promise<SearchResult> searchJqlPromise1 = client.getSearchClient().searchJql("project = \"Global Learning Platform\" AND Component != null AND Theme in ("+s+") and Sprint in openSprints() AND Subtype in (\"User Story\",\"QA Only\",\"Refactoring\", \"Tech Story\", \"Tech Debt\")",100,0,null);

	List<Issue> res1 = (List<Issue>) searchJqlPromise1.claim().getIssues();
	
for (Issue issue : res1) {
		
			//a+=1;
			String featureId = issue.getFieldByName("Theme").getValue().toString();
			String storyStatus=issue.getStatus().getName().toString();
			
			if(issue.getFieldByName("Story Points").getValue()!=null)
			{
			
			Double sp = (Double) issue.getFieldByName("Story Points").getValue();
			int storyPoint=sp.intValue();
			a+=storyPoint;
			
			if (!featureStory.containsKey(featureId)) {
				themePojo = new ThemePojo();
				themePojo.setTotal(themePojo.getTotal()+storyPoint);
				
				if(storyStatus.equals("Done") || storyStatus.equals("Closed") || storyStatus.equals("Cancel"))	
				{	themePojo.setCompleted(themePojo.getCompleted()+storyPoint);
					c+=1;}
				else
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + storyPoint);
			} else {
				themePojo = featureStory.get(featureId);
				themePojo.setTotal(themePojo.getTotal() + storyPoint);
				
				if(storyStatus.equals("Done") || storyStatus.equals("Closed") || storyStatus.equals("Cancel"))
					{
					themePojo.setCompleted(themePojo.getCompleted()+storyPoint);
					c+=1;
					}
				else
					
					themePojo.setPickedUpInCurrentSprint(themePojo.getPickedUpInCurrentSprint() + storyPoint);
			}
			}

			featureStory.put(featureId, themePojo);	
			}
	
	
	System.out.println("total Story points in current sprint: " + a+"\n");
	return featureStory;

}
	}
