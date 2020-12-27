package dev.markusk.digitalbeam.collector.misc;

import dev.markusk.digitalbeam.collector.Collector;
import dev.markusk.digitalbeam.collector.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class SslBuilder {

  private static final Logger LOGGER = LogManager.getLogger(Collector.class);

  private final SSLContext sslContext;

  public SslBuilder() {
    this.sslContext = Environment.CERT_URL.isEmpty() ? this.buildSimpleSslContext() : this.buildSslContext();
  }

  private SSLContext buildSslContext() {
    LOGGER.debug("Building SSLContext...");
    SSLContext context;
    try {
      final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      final Certificate certificate;
      LOGGER.debug("Loading letsencrypt certificate...");
      try (final InputStream stream = new URL(Environment.CERT_URL).openStream()) { // TODO: 27.12.20 make env var
        certificate = certificateFactory.generateCertificate(stream);
      }
      keyStore.load(null, null);
      keyStore.setCertificateEntry("ca", certificate);

      LOGGER.debug("Creating TrustManagerFactory...");
      final TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);

      context = SSLContext.getInstance("TLSv1.3");
      context.init(null, trustManagerFactory.getTrustManagers(), null);
      return context;
    } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | KeyManagementException e) {
      LOGGER.error("Error while creating ssl-context", e);
      return null;
    }
  }

  private SSLContext buildSimpleSslContext() {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.3");
      context.init(null, null, null);
      return context;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      LOGGER.error("Error while creating ssl-context", e);
    }
    return null;
  }

  public SSLContext getSslContext() {
    return this.sslContext;
  }
}
