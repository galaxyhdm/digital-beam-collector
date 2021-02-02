package dev.markusk.digitalbeam.collector.misc;

import com.apptastic.rssreader.RssReader;
import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class CustomRssReader extends RssReader {

  private static final Logger LOGGER = LogManager.getLogger(Collector.class);
  private static final Random RANDOM = new SecureRandom();

  private final SslBuilder sslBuilder;
  private final boolean useTorProxy;
  private final List<UserAgent> userAgents;
  private final HttpClient httpClient;

  public CustomRssReader(final SslBuilder sslBuilder, final List<UserAgent> userAgents, boolean useTorProxy) {
    this.sslBuilder = sslBuilder;
    this.userAgents = userAgents;
    this.useTorProxy = useTorProxy;
    this.httpClient = this.createHttpClient();
  }

  @Override
  protected CompletableFuture<HttpResponse<InputStream>> sendAsyncRequest(final String url) {
    final String userAgent = this.getRandomUserAgent();
    LOGGER.debug(String.format("New request for '%s' with user-agent: '%s'", url, userAgent));
    HttpRequest req = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(15))
        .header("Accept-Encoding", "gzip")
        .header("User-Agent", userAgent)
        .GET()
        .build();

    return this.httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofInputStream());
  }

  private String getRandomUserAgent() {
    return this.userAgents.get(RANDOM.nextInt(this.userAgents.size())).getUserAgent();
  }

  private HttpClient createHttpClient() {
    return HttpClient.newBuilder()
        .sslContext(this.sslBuilder.getSslContext())
        .connectTimeout(Duration.ofSeconds(15))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .proxy(new TorProxySelector(this.useTorProxy)).build();
  }

}
