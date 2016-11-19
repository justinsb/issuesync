package com.justinsb.issuesync;

import com.justinsb.issuesync.model.github.Issue;
import com.justinsb.issuesync.model.proto.IssuesProtos;
import com.justinsb.issuesync.store.Store;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class App {
  public static void main(String[] args) throws IOException, InterruptedException {
    File homedir = new File(System.getenv("HOME"));

    Properties properties = new Properties();
    try (FileInputStream fis = new FileInputStream(new File(homedir, ".issuesync"))) {
      properties.load(fis);
    }

    File basedir = new File(homedir, "issuesync/data");
    Store store = new Store(basedir);

    IssuesProtos.Project project = IssuesProtos.Project.newBuilder().setId("github.com:kubernetes:kubernetes").build();

    String auth = properties.getProperty("github.auth");
    try (GithubApiClient api = new GithubApiClient(auth)) {
      String owner = "kubernetes";
      String repo = "kubernetes";

//      GithubApiClient.Request<List<Issue>> issues = api.listIssues(owner, repo);
//      issues.sort = "updated";
//      issues.state = "all";
//
//      while (true) {
//        for (Issue issue : issues.execute()) {
//          IssuesProtos.Issue i = GithubMapper.mapToProto(issue);
//
//          boolean changed = store.write(project, i);
//
//          if (changed) {
////            for (Comment githubComment : api.listComments(owner, repo, issue.number)) {
////              IssuesProtos.Comment comment = GithubMapper.mapToProto(githubComment);
////              store.write(project, i, comment);
////            }
//          }
//        }
//        issues.page = issues.page + 1;
//        Thread.sleep(5000);
//      }

      // First bulk-fetch - fetch all issues
      GithubApiClient.Request<List<Issue>> request = api.listIssues(owner, repo);
      request.sort = "created";
      request.state = "all";
      request.direction= "asc";

      while (true) {
        List<Issue> issues = request.execute();
        if (issues.isEmpty()) {
          break;
        }
        for (Issue issue : issues) {
          IssuesProtos.Issue i = GithubMapper.mapToProto(issue);

          boolean changed = store.write(project, i);

          if (changed) {
//            for (Comment githubComment : api.listComments(owner, repo, issue.number)) {
//              IssuesProtos.Comment comment = GithubMapper.mapToProto(githubComment);
//              store.write(project, i, comment);
//            }
          }
        }
        request.page = request.page + 1;

        // TODO: Smart sleep
        Thread.sleep(1000);
      }
    }
  }
}
