package apkeditor.translate;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;

public class SSLSocketFactoryEx extends org.apache.http.conn.ssl.SSLSocketFactory {
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public SSLSocketFactoryEx(KeyStore paramKeyStore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(paramKeyStore);
		TrustManager local1 = new X509TrustManager() {
			public void checkClientTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			public void checkServerTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		this.sslContext.init(null, new TrustManager[] { local1 }, null);
	}

	public static DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore localKeyStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			localKeyStore.load(null, null);
			SSLSocketFactoryEx localSSLSocketFactoryEx = new SSLSocketFactoryEx(
					localKeyStore);
			localSSLSocketFactoryEx
					.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(localBasicHttpParams,
					HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(localBasicHttpParams, "UTF-8");
			SchemeRegistry localSchemeRegistry = new SchemeRegistry();
			localSchemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			localSchemeRegistry.register(new Scheme("https",
					localSSLSocketFactoryEx, 443));
			DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient(
					new ThreadSafeClientConnManager(localBasicHttpParams,
							localSchemeRegistry), localBasicHttpParams);
			return localDefaultHttpClient;
		} catch (Exception localException) {
		}
		return new DefaultHttpClient();
	}

	public Socket createSocket() throws IOException {
		return this.sslContext.getSocketFactory().createSocket();
	}

	public Socket createSocket(Socket paramSocket, String paramString,
			int paramInt, boolean paramBoolean) throws IOException,
			UnknownHostException {
		return this.sslContext.getSocketFactory().createSocket(paramSocket,
				paramString, paramInt, paramBoolean);
	}
}