package com.justinsb.issuesync.store;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.protobuf.Message;
import com.justinsb.issuesync.model.proto.IssuesProtos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Store {
  final File basedir;

  public Store(File basedir) {
    this.basedir = basedir;
  }

  public void write(IssuesProtos.Project project, IssuesProtos.Issue issue, IssuesProtos.Comment comment) throws IOException {
    File path = new File(basedir, project.getId() + "/issues/" + issue.getId() + "/comments/" + comment.getId() + ".yaml");
    writeFile(path, comment);
  }

  public boolean write(IssuesProtos.Project project, IssuesProtos.Issue issue) throws IOException {
    File path = new File(basedir, project.getId() + "/issues/" + issue.getId() + ".yaml");
    return writeFile(path, issue);
  }

  private boolean writeFile(File dest, Message msg) throws IOException {
    String yaml = SimpleYaml.build(msg);

    dest.getParentFile().mkdirs();

    byte[] existing;
    try {
      existing = Files.toByteArray(dest);
    } catch (FileNotFoundException e) {
      existing = null;
    }

    byte[] yamlBytes = yaml.getBytes(Charsets.UTF_8);
    if (existing != null && Arrays.equals(yamlBytes, existing)) {
      return false;
    }
    Files.write(yamlBytes, dest);
    return true;
  }

}
