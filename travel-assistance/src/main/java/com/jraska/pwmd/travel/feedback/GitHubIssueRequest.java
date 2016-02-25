package com.jraska.pwmd.travel.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class GitHubIssueRequest {
  @SerializedName("title")
  @Expose
  public String title;
  @SerializedName("body")
  @Expose
  public String body;
  @SerializedName("assignee")
  @Expose
  public String assignee;
  @SerializedName("milestone")
  @Expose
  public Integer milestone;
  @SerializedName("labels")
  @Expose
  public List<String> labels = new ArrayList<String>();
}
