package com.justinsb.issuesync.model.github;

import com.google.gson.annotations.SerializedName;

public class Milestone {

  public String url;

  @SerializedName("html_url")
  public String htmlUrl;

  @SerializedName("labels_url")
  public String labelsUrl;

  public Integer id;

  public Integer number;

  public String title;
  public String description;

  public User creator;

  @SerializedName("open_issues")
  public Integer openIssues;

  @SerializedName("closed_issues")
  public Integer closedIssues;

  public String state;

  @SerializedName("created_at")
  public String createdAt;

  @SerializedName("updated_at")
  public String updatedAt;

  @SerializedName("closed_at")
  public String closedAt;


  @SerializedName("due_on")
  public String dueOn;

}
