package apkeditor.translate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class WebBrowser {
	private MyCookieStore cookieStore = new MyCookieStore();
	HttpHost proxy = new HttpHost("xxx", 911, "http");
	private boolean useProxy = false;
	private boolean useSSL = false;

	private String userAgent = null;

	public WebBrowser() {
		this(false);
	}

	public WebBrowser(String userAgent) {
		this.userAgent = userAgent;
	}

	public WebBrowser(boolean paramBoolean) {
		this.useSSL = paramBoolean;
	}

	private DefaultHttpClient getHttpClient() {
		if (this.useSSL)
			return SSLSocketFactoryEx.getNewHttpClient();
		return new DefaultHttpClient();
	}

	private String getString(InputStream input) {
		int bufferSize = 256 * 1024;
		int readSize = 0;

		try {
			byte[] buffer = new byte[bufferSize];
			int maxStrSize = bufferSize - 1;
			while (readSize < maxStrSize) {
				int ret = input.read(buffer, readSize, maxStrSize - readSize);
				if (ret <= 0) {
					break;
				}
				readSize += ret;
			}

			if (readSize > 0) {
				String str = new String(buffer, 0, readSize, "UTF-8");
				return str;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String get(String tag, String strUrl, String referUrl) {
		String str = null;
		try {
			HttpGet httpGet = new HttpGet(strUrl);
			httpGet.getParams().setParameter("http.protocol.cookie-policy",
					"compatibility");
			DefaultHttpClient httpClient = getHttpClient();
			if (userAgent != null)
				httpGet.addHeader("User-Agent", userAgent);
			if (referUrl != null) {
				httpGet.addHeader("Referer", referUrl);
			}
			MyCookieStore cookieStore = this.cookieStore;
			str = null;
			if (cookieStore != null) {
				httpClient.setCookieStore(this.cookieStore);
			}
			if (this.useProxy) {
				httpClient.getParams().setParameter("http.route.default-proxy",
						this.proxy);
			}
			httpClient.getParams().setParameter("http.connection.timeout",
					Integer.valueOf(15000));
			httpClient.getParams().setParameter("http.socket.timeout",
					Integer.valueOf(15000));
			str = getString(httpClient.execute(httpGet).getEntity()
					.getContent());
			// Debug.dump(tag + ".html", str);
			List<Cookie> newCookies = httpClient.getCookieStore().getCookies();
			this.cookieStore.addCookies(newCookies);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			Debug.dump(tag + ".error", e.getMessage());
		}
		return str;
	}

	public MyCookieStore getCookieStore() {
		return this.cookieStore;
	}

	public String post(String tag, String uri, List<NameValuePair> paramList) {
		HttpPost httpPost = new HttpPost(uri);
		// httpPost.addHeader("Origin", "http://home.guahao.cn");
		httpPost.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=\"UTF-8\"");
		httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
		httpPost.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.99 Mobile Safari/537.36");
		httpPost.addHeader("Accept",
				"application/json, text/javascript, */*; q=0.01");
		// httpPost.addHeader("Referer",
		// "http://home.guahao.cn/expert/97bf49b0-ca4c-476d-b780-d2f0c2d4b126");
		httpPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		httpPost.addHeader("Accept-Language", "en-US,en;q=0.8");
		httpPost.addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		return post(tag, httpPost, paramList);
	}

	public String post(String tag, HttpPost httpPost,
			List<NameValuePair> paramList) {
		String str = null;
		try {
			int k = paramList.size();
			str = null;
			if (k > 0)
				httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
			if (!httpPost.containsHeader("Content-Type"))
				httpPost.addHeader("Content-Type",
						"application/x-www-form-urlencoded; charset=\"UTF-8\"");
			if (!httpPost.containsHeader("User-Agent") && userAgent != null) {
				httpPost.addHeader("User-Agent", userAgent);
			}
			httpPost.getParams().setParameter("http.protocol.cookie-policy",
					"compatibility");
			DefaultHttpClient localDefaultHttpClient = getHttpClient();
			MyCookieStore localMyCookieStore = this.cookieStore;
			str = null;
			if (localMyCookieStore != null)
				localDefaultHttpClient.setCookieStore(this.cookieStore);
			boolean bool = this.useProxy;
			str = null;
			if (bool)
				localDefaultHttpClient.getParams().setParameter(
						"http.route.default-proxy", this.proxy);
			localDefaultHttpClient.getParams().setParameter(
					"http.connection.timeout", Integer.valueOf(15000));
			localDefaultHttpClient.getParams().setParameter(
					"http.socket.timeout", Integer.valueOf(15000));
			//Log.d("DEBUG", "Will call httpClient.execute");
			HttpResponse localHttpResponse = localDefaultHttpClient
					.execute(httpPost);
			//Log.d("DEBUG", "After call httpClient.execute");
			str = getString(localHttpResponse.getEntity().getContent());
			//Log.d("DEBUG", "Response got!");
			Header[] arrayOfHeader = localHttpResponse.getAllHeaders();
			StringBuffer localStringBuffer = new StringBuffer();
			int i = arrayOfHeader.length;
			for (int j = 0;; j++) {
				if (j >= i) {
					Debug.dump(tag + ".header", localStringBuffer.toString());
					Debug.dump(tag + ".html", str);
					List<Cookie> newCookies = localDefaultHttpClient
							.getCookieStore().getCookies();
					this.cookieStore.addCookies(newCookies);
					return str;
				}
				Header localHeader = arrayOfHeader[j];
				localStringBuffer.append(localHeader.getName() + ": "
						+ localHeader.getValue() + "\n");
			}
		} catch (Exception localException) {
			Debug.dump(tag + ".error", localException.getMessage());
		}
		return str;
	}
}
