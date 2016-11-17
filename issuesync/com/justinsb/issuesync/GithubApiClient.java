package com.justinsb.issuesync;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.justinsb.issuesync.model.github.Comment;
import com.justinsb.issuesync.model.github.Issue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GithubApiClient implements Closeable {
  final CloseableHttpClient httpClient;

  static final Gson gson = new Gson();

  public GithubApiClient() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(200);
    cm.setDefaultMaxPerRoute(20);
//// Increase max connections for localhost:80 to 50
//    HttpHost localhost = new HttpHost("locahost", 80);
//    cm.setMaxPerRoute(new HttpRoute(localhost), 50);

    this.httpClient = HttpClients.custom()
        .setConnectionManager(cm)
        .build();
  }

  public List<Issue> listIssues(String owner, String repo) throws IOException {
    HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + owner + "/" + repo + "/issues?sort=updated&state=all");

    String json = doGet(httpGet);

    Type issueListType = new TypeToken<ArrayList<Issue>>() {}.getType();
    List<Issue> issues = gson.fromJson(json, issueListType);
    return issues;
  }


  public List<Comment> listComments(String owner, String repo, Integer number ) throws IOException {
    HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + owner + "/" + repo + "/issues/" + number + "/comments");

    String json = doGet(httpGet);

    Type commentListType = new TypeToken<ArrayList<Comment>>() {}.getType();
    List<Comment> comments = gson.fromJson(json, commentListType);
    return comments;
  }

  private String doGet(HttpGet httpGet) throws IOException {
    System.out.println("Executing request " + httpGet.getRequestLine());

    // Create a custom response handler
    ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

      @Override
      public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
          HttpEntity entity = response.getEntity();
          return entity != null ? EntityUtils.toString(entity) : null;
        } else {
          throw new ClientProtocolException("Unexpected response status: " + status);
        }
      }

    };
    String json = httpClient.execute(httpGet, responseHandler);
    return json;
  }

  @Override
  public void close() throws IOException {
    this.httpClient.close();
  }
}
