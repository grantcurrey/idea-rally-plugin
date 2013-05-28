package com.dnat.idea.rally.connector

import com.rallydev.rest.RallyRestApi
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.ssl.TrustStrategy

import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class InsecureRallyRestApi extends RallyRestApi {
    public InsecureRallyRestApi(URI server, String userName, String password) {
        super(server, userName, password)
        SchemeRegistry registry = httpClient.getConnectionManager().getSchemeRegistry()
        registry.register(new Scheme("https", 443, buildSSLSocketFactory()))
    }

    private SSLSocketFactory buildSSLSocketFactory() {
        TrustStrategy ts = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true
            }
        };

        return new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
    }

}
