package dev.markusk.digitalbeam.collector.misc;

import dev.markusk.digitalbeam.collector.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class TorProxySelector extends ProxySelector {

  private static final Logger LOGGER = LogManager.getLogger();

  private final boolean useTorProxy;
  private final List<Proxy> proxies;

  private Proxy torProxy;

  public TorProxySelector(final boolean useTorProxy) {
    this.useTorProxy = useTorProxy;
    this.proxies = this.createProxyList();
  }

  @Override
  public List<Proxy> select(final URI uri) {
    return this.proxies;
  }

  @Override
  public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
    LOGGER.warn("Proxy not available!", ioe);
  }

  private List<Proxy> createProxyList() {
    if (this.useTorProxy) {
      final SocketAddress socketAddress = this.getSocketAddress();
      if (socketAddress == null) return List.of();
      this.torProxy = new Proxy(Proxy.Type.HTTP, socketAddress);
    }
    return this.torProxy != null ? List.of(this.torProxy) : List.of();
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
      LOGGER.warn("Could not parse port in proxy-address! No proxy will by used!");
    }
    return null;
  }

}
