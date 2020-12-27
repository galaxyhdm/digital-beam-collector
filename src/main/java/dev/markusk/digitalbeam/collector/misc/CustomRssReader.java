package dev.markusk.digitalbeam.collector.misc;

import com.apptastic.rssreader.RssReader;
import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.model.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
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
  private final boolean tor;
  private final List<UserAgent> userAgents;

  private HttpClient httpClient;

  public CustomRssReader(final SslBuilder sslBuilder, final List<UserAgent> userAgents, boolean tor) {
    this.sslBuilder = sslBuilder;
    this.tor = tor;
    this.userAgents = userAgents;
    this.httpClient = this.createHttpClient();
  }

  @Override
  protected CompletableFuture<HttpResponse<InputStream>> sendAsyncRequest(final String url) {
    HttpRequest req = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(25))
        .header("Accept-Encoding", "gzip")
        .header("User-Agent", this.getRandomUserAgent())
        .GET()
        .build();

    HttpClient client = this.httpClient;

    if (client == null) {
      client = this.createHttpClient();
    }

    return client.sendAsync(req, HttpResponse.BodyHandlers.ofInputStream());
  }

  private String getRandomUserAgent() {
    return this.userAgents.get(RANDOM.nextInt(this.userAgents.size())).getUserAgent();
  }

  private HttpClient createHttpClient() {
    return HttpClient.newBuilder()
        .sslContext(this.sslBuilder.getSslContext())
        .connectTimeout(Duration.ofSeconds(25))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .proxy(new ProxySelector() {
          @Override
          public List<Proxy> select(final URI uri) {
            return CustomRssReader.this.tor
                ? List.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8118)))
                : List.of();
          }

          @Override
          public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
            LOGGER.warn("Proxy not available!", ioe);
          }
        }).build();
  }

}
