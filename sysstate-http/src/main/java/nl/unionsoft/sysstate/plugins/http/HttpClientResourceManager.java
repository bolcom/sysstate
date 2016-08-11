package nl.unionsoft.sysstate.plugins.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
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
            try {
                result = createHttpClient(resource.getConfiguration());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                throw new IllegalStateException("Could not create HttpClient for resource [" + resource + "]", e);
            }
            HttpClient old = httpClients.putIfAbsent(resource.getName(), result);
            if (old != null) {
                result = old;
            }
        }

        return result;
    }

    @Scheduled(cron = "* */1 * * * ?")
    public void closeIdleConnections() {
        for (Entry<String, HttpClient> entry : httpClients.entrySet()) {
            LOG.debug("Closing idle httpClient Connections for client '{}'", entry.getKey());
            HttpClient httpClient = entry.getValue();
            ClientConnectionManager clientConnectionManager = httpClient.getConnectionManager();
            clientConnectionManager.closeExpiredConnections();
            clientConnectionManager.closeIdleConnections(120, TimeUnit.SECONDS);
        }
    }

    private HttpClient createHttpClient(Map<String, String> configuration) throws KeyManagementException, NoSuchAlgorithmException {
        int connectionTimeoutMillis = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("connectionTimeoutMillis"), DEFAULT_CONN_TIMEOUT_MILLIS));
        int socketTimeoutMillis = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("socketTimeoutMillis"), DEFAULT_SOCK_TIMEOUT_MILLIS));
        int proxyPort = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("proxyPort"), "0"));
        String proxyHost = configuration.get("proxyHost");
        LOG.info("HttpClient settings are: connectionTimeoutMillis={},socketTimeoutMillis={}, proxyPort={}, proxyHost={}", new Object[] {
                connectionTimeoutMillis, socketTimeoutMillis, proxyPort, proxyHost });
        return createHttpClient(connectionTimeoutMillis, socketTimeoutMillis, proxyHost, proxyPort);
    }

    private DefaultHttpClient createHttpClient(int connectionTimeoutMillis, int socketTimeoutMillis, String proxyHost, int proxyPort)
            throws KeyManagementException, NoSuchAlgorithmException {

        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        final BasicHttpParams basicHttpParams = new BasicHttpParams();
        if (StringUtils.isNotEmpty(proxyHost) && proxyPort > 0) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            basicHttpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        setConnectionTimeout(connectionTimeoutMillis, basicHttpParams);
        setSocketTimeout(socketTimeoutMillis, basicHttpParams);
        setTrustManager(connectionManager);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager, basicHttpParams);
        defaultHttpClient.setRedirectStrategy(new DefaultRedirectStrategy());
        // defaultHttpClient.getCredentialsProvider().setCredentials(
        // new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
        // new UsernamePasswordCredentials("harry", "Potter"));
        return defaultHttpClient;
    }

    private void setConnectionTimeout(final int connectionTimeoutMillis, final BasicHttpParams basicHttpParams) {
        if (connectionTimeoutMillis > 0) {
            LOG.info("Setting connectionTimeoutMillis to '{}'", connectionTimeoutMillis);
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, connectionTimeoutMillis);
        }
    }

    private void setSocketTimeout(final int socketTimeoutMillis, final BasicHttpParams basicHttpParams) {
        if (socketTimeoutMillis > 0) {
            LOG.info("Setting socketTimeoutMillis to '{}'", socketTimeoutMillis);
            HttpConnectionParams.setSoTimeout(basicHttpParams, socketTimeoutMillis);
        }
    }

    private void setTrustManager(final ClientConnectionManager threadSafeClientConnManager) throws NoSuchAlgorithmException, KeyManagementException {
        final X509TrustManager trustManager = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { trustManager }, null);
        final SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext);
        final SchemeRegistry schemaRegistry = threadSafeClientConnManager.getSchemeRegistry();
        schemaRegistry.register(new Scheme("https", 443, sslSocketFactory));
    }

    @Override
    public void update(ResourceDto resource) {
        try {
            httpClients.put(resource.getName(), createHttpClient(resource.getConfiguration()));
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to create Http Client", e);
        }

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
