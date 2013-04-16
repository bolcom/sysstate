package nl.unionsoft.sysstate.factorybeans;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
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
import org.springframework.beans.factory.FactoryBean;

public class HttpClientFactoryBean implements FactoryBean<DefaultHttpClient> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientFactoryBean.class);

    private final DefaultHttpClient defaultHttpClient;

    public HttpClientFactoryBean (final int connectionTimeoutMillis, final int socketTimeoutMillis) throws NoSuchAlgorithmException, KeyManagementException {
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        final BasicHttpParams basicHttpParams = new BasicHttpParams();
        setConnectionTimeout(connectionTimeoutMillis, basicHttpParams);
        setSocketTimeout(socketTimeoutMillis, basicHttpParams);
        setTrustManager(connectionManager);
        defaultHttpClient = new DefaultHttpClient(connectionManager, basicHttpParams);
        defaultHttpClient.setRedirectStrategy(new DefaultRedirectStrategy());
        // defaultHttpClient.getCredentialsProvider().setCredentials(
        // new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
        // new UsernamePasswordCredentials("harry", "Potter"));
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

    public HttpClientFactoryBean () throws KeyManagementException, NoSuchAlgorithmException {
        this(0, 0);
    }

    public DefaultHttpClient getObject() throws Exception {
        return defaultHttpClient;
    }

    public Class<?> getObjectType() {
        return DefaultHttpClient.class;
    }

    public boolean isSingleton() {
        return true;
    }



}
