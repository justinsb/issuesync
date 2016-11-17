package com.justinsb.issuesync.model.github;

import com.google.gson.annotations.SerializedName;

public class Comment {
  public String url;

  @SerializedName("html_url")
  public String htmlUrl;

  @SerializedName("issue_url")
  public String issueUrl;

  public Integer id;

  public User user;

  @SerializedName("created_at")
  public String createdAt;

  @SerializedName("updated_at")
  public String updatedAt;

  public String body;
}
