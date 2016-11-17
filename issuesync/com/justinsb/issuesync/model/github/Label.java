package com.justinsb.issuesync.model.github;

import com.google.gson.annotations.SerializedName;

public class Label {

  public Integer id;

  public String url;
  public String name;

  public String color;

  @SerializedName("default")
  public Boolean isDefault;

}
