package nl.unionsoft.sysstate.plugins.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.ResourceDto;
import nl.unionsoft.sysstate.common.extending.ResourceManager;

@Service(HttpConstants.RESOURCE_MANAGER_NAME)
public class HttpClientResourceManager implements ResourceManager<HttpClient> {

    private static final String DEFAULT_CONN_TIMEOUT_MILLIS = "45000";
    private static final String DEFAULT_SOCK_TIMEOUT_MILLIS = "45000";
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientResourceManager.class);

    private Map<String, HttpClient> httpClients;

    public HttpClientResourceManager() {
        httpClients = new ConcurrentHashMap<String, HttpClient>();
    }

    @Override
    public HttpClient getResource(ResourceDto resource) {
        HttpClient result = httpClients.get(resource.getName());
        if (result == null) {
            result = createHttpClientNew(resource.getConfiguration());
            HttpClient old = httpClients.putIfAbsent(resource.getName(), result);
            if (old != null) {
                result = old;
            }
        }

        return result;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 60000)
    @SuppressWarnings("deprecation")
    public void closeIdleConnections() {
        for (Entry<String, HttpClient> entry : httpClients.entrySet()) {
            LOG.debug("Closing idle httpClient Connections for client '{}'", entry.getKey());
            HttpClient httpClient = entry.getValue();
            ClientConnectionManager clientConnectionManager = httpClient.getConnectionManager();
            clientConnectionManager.closeExpiredConnections();
            clientConnectionManager.closeIdleConnections(120, TimeUnit.SECONDS);
        }
    }

    private HttpClient createHttpClientNew(Map<String, String> configuration) {

        RequestConfig requestConfig = createRequestConfig(configuration);

        LayeredConnectionSocketFactory sslSocketFactory = createSslSocketFactory(configuration);

        HttpClientBuilder builder = HttpClientBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .setConnectionManager(createConnectionManager(sslSocketFactory))
                .setDefaultRequestConfig(requestConfig);

        getProxyHost(configuration).ifPresent(proxyHost -> builder.setProxy(proxyHost));

        return builder.build();

    }

    private HttpClientConnectionManager createConnectionManager(LayeredConnectionSocketFactory sslSocketFactory) {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", sslSocketFactory)
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
        return new PoolingHttpClientConnectionManager(registry);
    }

    private Optional<HttpHost> getProxyHost(Map<String, String> configuration) {
        String proxyHost = configuration.get("proxyHost");
        if (StringUtils.isNotBlank(proxyHost)) {
            int proxyPort = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("proxyPort"), "0"));
            return Optional.of(new HttpHost(proxyHost, proxyPort));
        }
        return Optional.empty();

    }

    private RequestConfig createRequestConfig(Map<String, String> configuration) {
        int connectionTimeoutMillis = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("connectionTimeoutMillis"), DEFAULT_CONN_TIMEOUT_MILLIS));
        int socketTimeoutMillis = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("socketTimeoutMillis"), DEFAULT_SOCK_TIMEOUT_MILLIS));
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeoutMillis)
                .setConnectionRequestTimeout(socketTimeoutMillis)
                .build();
    }

    private LayeredConnectionSocketFactory createSslSocketFactory(Map<String, String> configuration) {
        SSLContext sslcontext = getSslContext(configuration);
        return new SSLConnectionSocketFactory(sslcontext);
    }

    private SSLContext getSslContext(Map<String, String> configuration) {
        if (Boolean.valueOf(configuration.get("trustManagerAllowAll"))) {
            return createAllAllowingSslContext();
        }
        return SSLContexts.createDefault();
    }

    private SSLContext createAllAllowingSslContext() {
        final X509TrustManager trustManager = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to init sslContext, caught Exception", e);
        }
    }

    @Override
    public void update(ResourceDto resource) {
        httpClients.put(resource.getName(), createHttpClientNew(resource.getConfiguration()));
    }

    @Override
    public void remove(String name) {
        httpClients.remove(name);
    }

    @Override
    public List<ResourceDto> getDefaultResources() {
        List<ResourceDto> defaultResources = new ArrayList<>();
        ResourceDto resource = new ResourceDto();
        resource.setManager(HttpConstants.RESOURCE_MANAGER_NAME);
        resource.setName(HttpConstants.DEFAULT_RESOURCE);
        defaultResources.add(resource);
        return defaultResources;
    }

}
