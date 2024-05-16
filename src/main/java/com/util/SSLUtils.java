package com.util;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @author yuanmengfan(mf.yuan@qq.com) on 2025/11/9 23:51
 */
public class SSLUtils {

    private static final TrustManager TRUST_MANAGER = new X509TrustManager() {

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }

        public void checkClientTrusted(X509Certificate[] cert, String oauthType)
                throws java.security.cert.CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] cert, String oauthType)
                throws java.security.cert.CertificateException {
        }
    };

    private static  final HostnameVerifier ALL_HOSTNAME_VERIFIER = (hostname, session) -> true;


    public static void enableSSL() {
        try {
            // 安装所有信任的trust manager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { TRUST_MANAGER }, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            // 创建一个 HostnameVerifier 始终返回 true
            // 安装所有信任的 hostname verifier
            HttpsURLConnection.setDefaultHostnameVerifier(ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
