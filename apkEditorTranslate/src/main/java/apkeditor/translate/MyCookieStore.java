package apkeditor.translate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

public class MyCookieStore implements CookieStore {

	private List<CookieInfo> cookieList = (List<CookieInfo>) getCookieFile("CookieFile");
	File file = null;

	public MyCookieStore() {
		if (this.cookieList == null)
			this.cookieList = new ArrayList<CookieInfo>();
	}

	private List<CookieInfo> getCookieFile(String filename) {
		return new ArrayList<CookieInfo>();
	}

	@SuppressWarnings("unused")
	private void dumpCookieList() {
		Iterator<CookieInfo> localIterator = this.cookieList.iterator();
		while (true) {
			if (!localIterator.hasNext())
				return;
			CookieInfo localCookieInfo = (CookieInfo) localIterator.next();
			Debug.log("\tName: " + localCookieInfo.getCookieName(),
					new Object[0]);
			Debug.log("\tValue: " + localCookieInfo.getCookieValue(),
					new Object[0]);
			Debug.log("\tDomain: " + localCookieInfo.getCookieDomain(),
					new Object[0]);
			Debug.log("\tDate: " + localCookieInfo.getCookieDate(),
					new Object[0]);
		}
	}

	// Update the cookie
	private void updateCookieList(CookieInfo cookieInfo) {
		
		String name = cookieInfo.getCookieName();
		
		for (CookieInfo ci : this.cookieList) {
			// The same name already exists
			if (ci.getCookieName().equals(name)) {
				ci.setCookieDate(cookieInfo.getCookieDate());
				ci.setCookieDomain(cookieInfo.getCookieDomain());
				ci.setCookieName(cookieInfo.getCookieName());
				ci.setCookieValue(cookieInfo.getCookieValue());
				return;
			}
		}
		
		this.cookieList.add(cookieInfo);
	}

	// Add one cookie to the store
	public void addCookie(Cookie cookie) {
		if (cookie == null)
			return;

		if (this.cookieList == null) {
			this.cookieList = new ArrayList<CookieInfo>();
		}

		CookieInfo cookieInfo = new CookieInfo();
		if (cookie.getExpiryDate() != null)
			cookieInfo.setCookieDate(cookie.getExpiryDate());
		cookieInfo.setCookieName(cookie.getName());
		cookieInfo.setCookieValue(cookie.getValue());
		cookieInfo.setCookieDomain(cookie.getDomain());

		updateCookieList(cookieInfo);
	}

	public void addCookies(List<Cookie> cookies) {
		Iterator<Cookie> it = cookies.iterator();
		while (it.hasNext()) {
			addCookie((Cookie) it.next());
		}
	}

	public void clear() {
	}

	public boolean clearExpired(Date paramDate) {
		return false;
	}

	public Cookie convertCookie(CookieInfo paramCookieInfo) {
		BasicClientCookie localBasicClientCookie = new BasicClientCookie(
				paramCookieInfo.getCookieName(),
				paramCookieInfo.getCookieValue());
		localBasicClientCookie.setDomain(paramCookieInfo.getCookieDomain());
		localBasicClientCookie.setExpiryDate(paramCookieInfo.getCookieDate());
		return localBasicClientCookie;
	}

	public List<Cookie> getCookies() {
		ArrayList<Cookie> ret = new ArrayList<Cookie>();
		
		if (this.cookieList != null) {
			for (int i = 0; i < this.cookieList.size(); i++) {
				CookieInfo cookie = (CookieInfo) cookieList.get(i);
				if (cookie != null) {
					ret.add(convertCookie(cookie));
				}
			}
		}

		return ret;
	}

	public void saveCookies() {
		if (this.cookieList != null)
			putCookie("CookieFile", this.cookieList);
	}

	private void putCookie(String string, List<CookieInfo> cookieList2) {
		// TODO Auto-generated method stub
	}
}
