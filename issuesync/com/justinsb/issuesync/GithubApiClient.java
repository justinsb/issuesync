package com.justinsb.issuesync;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.justinsb.issuesync.model.github.Comment;
import com.justinsb.issuesync.model.github.Issue;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class GithubApiClient implements Closeable {
  private static final Logger log = LoggerFactory.getLogger(GithubApiClient.class);

  final CloseableHttpClient httpClient;

  static final Gson gson = new Gson();

  final URI baseURI;
  private final Header authHeader;

  public GithubApiClient(String auth) {
    this.baseURI = URI.create("https://api.github.com/");
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(200);
    cm.setDefaultMaxPerRoute(20);
//// Increase max connections for localhost:80 to 50
//    HttpHost localhost = new HttpHost("locahost", 80);
//    cm.setMaxPerRoute(new HttpRoute(localhost), 50);

//    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//    List<String> authTokens = Splitter.on(':').splitToList(auth);

//    credentialsProvider.setCredentials(new AuthScope(HttpHost.create("https://api.github.com")),
//        new UsernamePasswordCredentials(authTokens.get(0), authTokens.get(1)));

//    this.creds = new UsernamePasswordCredentials(authTokens.get(0), authTokens.get(1));

    this.authHeader = new BasicHeader("Authorization", "Basic " + BaseEncoding.base64().encode(auth.getBytes(Charsets.US_ASCII)));

    this.httpClient = HttpClients.custom()
        .setConnectionManager(cm)
//        .setDefaultCredentialsProvider(credentialsProvider)
        .build();
  }

  public Request<List<Issue>> listIssues(String owner, String repo) throws IOException {
    Request<List<Issue>> request = new Request<List<Issue>>() {

      @Override
      public List<Issue> execute() throws IOException {
        URI uri = baseURI.resolve("repos/" + owner + "/" + repo + "/issues");
        URIBuilder uriBuilder = new URIBuilder(uri);
        addParameters(uriBuilder);

        String json = doGet(uriBuilder);

        Type issueListType = new TypeToken<ArrayList<Issue>>() {
        }.getType();
        List<Issue> issues = gson.fromJson(json, issueListType);
        return issues;
      }
    };

    return request;
  }

  public List<Comment> listComments(String owner, String repo, Integer number) throws IOException {
    HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + owner + "/" + repo + "/issues/" + number + "/comments");

    String json = doGet(httpGet);

    Type commentListType = new TypeToken<ArrayList<Comment>>() {
    }.getType();
    List<Comment> comments = gson.fromJson(json, commentListType);
    return comments;
  }

  private String doGet(URIBuilder uriBuilder) throws IOException {
    HttpGet httpGet;
    try {
      httpGet = new HttpGet(uriBuilder.build());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Error building uri", e);
    }
    return doGet(httpGet);
  }

  private String doGet(HttpGet httpGet) throws IOException {
    log.info("Executing request " + httpGet.getRequestLine());

    httpGet.setHeader(new BasicHeader("Accept", "application/vnd.github.v3+json"));
    httpGet.setHeader(new BasicHeader("User-Agent", "https://github.com/justinsb/issuesync"));
    httpGet.setHeader(authHeader);

    // Create a custom response handler
    ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

      @Override
      public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        Header rateLimitLimitHeader = response.getFirstHeader("X-RateLimit-Limit");
        String rateLimitLimit = rateLimitLimitHeader != null ? rateLimitLimitHeader.getValue() : null;

        Header rateLimitRemainingHeader = response.getFirstHeader("X-RateLimit-Remaining");
        String rateLimitRemaining = rateLimitRemainingHeader != null ? rateLimitRemainingHeader.getValue() : null;

        Header rateLimitResetHeader = response.getFirstHeader("X-RateLimit-Reset");
        String rateLimitReset = rateLimitResetHeader != null ? rateLimitResetHeader.getValue() : null;

        log.info("Rate limit limit={}, remaining={}, reset={}", rateLimitLimit, rateLimitRemaining, rateLimitReset);

        if (!isNullOrEmpty(rateLimitReset)) {
          long rateLimitResetLong = Long.valueOf(rateLimitReset);
          long now = System.currentTimeMillis() / 1000L;
          long remaining = rateLimitResetLong - now;
          log.info("Resets in {}", Duration.of(remaining, ChronoUnit.SECONDS));
        }

        int status = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();
        String body = entity != null ? EntityUtils.toString(entity) : null;

        if (status >= 200 && status < 300) {
          return body;
        } else {
          log.warn("Unexpected response {}: {}", response.getStatusLine(), body);
          throw new ClientProtocolException("Unexpected response status: " + response.getStatusLine());
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

  public abstract class Request<T> {
    public int page = 1;
    public int perPage = 100;

    public String sort = null;
    public String state = null;
    public String direction = null;

    public abstract T execute() throws IOException;

    protected void addParameters(URIBuilder b) {
      b.setParameter("page", Integer.toString(page));
      b.setParameter("per_page", Integer.toString(perPage));

      if (sort != null) {
        b.setParameter("sort", sort);
      }
      if (state != null) {
        b.setParameter("state", state);
      }
      if (direction != null) {
        b.setParameter("direction", direction);
      }
    }
  }
}
