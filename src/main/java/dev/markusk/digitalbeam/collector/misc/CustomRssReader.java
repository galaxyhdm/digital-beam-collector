package dev.markusk.digitalbeam.collector.misc;

import com.apptastic.rssreader.RssReader;
import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.Environment;
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
  private final HttpClient httpClient;

  private Proxy proxy;

  public CustomRssReader(final SslBuilder sslBuilder, final List<UserAgent> userAgents, boolean tor) {
    this.sslBuilder = sslBuilder;
    this.userAgents = userAgents;
    this.tor = tor;
    this.httpClient = this.createHttpClient();

    if (this.tor) {
      final SocketAddress socketAddress = this.getSocketAddress();
      if (socketAddress == null) return;
      this.proxy = new Proxy(Proxy.Type.HTTP, socketAddress);
    }
  }

  @Override
  protected CompletableFuture<HttpResponse<InputStream>> sendAsyncRequest(final String url) {
    final String userAgent = this.getRandomUserAgent();
    LOGGER.debug(String.format("New request for '%s' with user-agent: '%s'", url, userAgent));
    HttpRequest req = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(25))
        .header("Accept-Encoding", "gzip")
        .header("User-Agent", userAgent)
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
            return CustomRssReader.this.tor && CustomRssReader.this.proxy != null
                ? List.of(CustomRssReader.this.proxy)
                : List.of();
          }

          @Override
          public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
            LOGGER.warn("Proxy not available!", ioe);
          }
        }).build();
  }

  private SocketAddress getSocketAddress() {
    try {
      final String address = Environment.PROXY_ADDRESS;
      if (address.isEmpty()) return null;
      if (!address.contains(":")) return null;
      final String[] split = address.split(":");
      final String hostname = split[0];
      final int port = Integer.parseInt(split[1]);
      return new InetSocketAddress(hostname, port);
    } catch (NumberFormatException e) {
      LOGGER.warn("Could not pars port in proxy-address! No proxy will by used!");
    }
    return null;
  }

}
