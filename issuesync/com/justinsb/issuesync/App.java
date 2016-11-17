package com.justinsb.issuesync;

import com.justinsb.issuesync.model.github.Comment;
import com.justinsb.issuesync.model.github.Issue;
import com.justinsb.issuesync.model.proto.IssuesProtos;

import java.io.IOException;
import java.util.List;

public class App {
  public static void main(String[] args) throws IOException {
    try (GithubApiClient api = new GithubApiClient()) {
      String owner = "kubernetes";
      String repo = "kubernetes";

      List<Issue> issues = api.listIssues(owner, repo);
      for (Issue issue : issues) {
        System.out.println("Issue: " + issue.id);
        for (Comment githubComment : api.listComments(owner, repo, issue.number)) {
          IssuesProtos.Comment.Builder b = IssuesProtos.Comment.newBuilder();
          b.setBody(githubComment.body);
          IssuesProtos.Comment comment = b.build();
          System.out.println("\tComment: " + comment);
        }

        break;
      }
    }
  }
}
