package com.justinsb.issuesync.model.github;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Issue {
  public String url;

  @SerializedName("repository_url")
  public String repositoryUrl;

  @SerializedName("labels_url")
  public String labelsUrl;

  @SerializedName("comments_url")
  public String commentsUlr;

  @SerializedName("events_url")
  public String eventsUrl;

  @SerializedName("html_url")
  public String htmlUrl;

  public Integer id;

  public Integer number;

  public String title;

  public User user;

  public List<Label> labels;

  public String state;

  public Boolean locked;

  public User assignee;

  public List<User> assignees;

  public Milestone milestone;

  public Integer comments;


  @SerializedName("created_at")
  public String createdAt;

  @SerializedName("updated_at")
  public String updatedAt;

  @SerializedName("closed_at")
  public String closedAt;

  public String body;

}
