package com.justinsb.issuesync;

import com.justinsb.issuesync.model.github.Comment;
import com.justinsb.issuesync.model.github.Issue;
import com.justinsb.issuesync.model.github.User;
import com.justinsb.issuesync.model.proto.IssuesProtos;

public class GithubMapper {
  public static IssuesProtos.Issue mapToProto(Issue gh) {
    IssuesProtos.Issue.Builder b = IssuesProtos.Issue.newBuilder();
    b.setId(gh.number.toString());
    b.setTitle(gh.title);

    b.setCreatedById(gh.user.id);
    for (User u : gh.assignees) {
      b.addAssigneeId(u.id);
    }
    b.setCreatedAt(gh.createdAt);
    b.setUpdatedAt(gh.updatedAt);

    if (gh.body != null) {
      b.setBody(gh.body);
    }

//    public String url;
//    public Integer id;
//    public List<Label> labels;
//    public String state;
//    public Boolean locked;
//    public User assignee;
//    public Milestone milestone;
//    public Integer comments;
//    public String closedAt;

    return b.build();
  }

  public static IssuesProtos.Comment mapToProto(Comment gh) {
    IssuesProtos.Comment.Builder b = IssuesProtos.Comment.newBuilder();
    b.setId(gh.id.toString());
    b.setBody(gh.body);
    b.setCreatedById(gh.user.id);
    b.setCreatedAt(gh.createdAt);
    b.setUpdatedAt(gh.updatedAt);

    return b.build();
  }
}
