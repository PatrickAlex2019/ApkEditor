package apkeditor.translate;

import java.io.Serializable;
import java.util.Date;

public class CookieInfo implements Serializable {
	private static final long serialVersionUID = -1906450774713846916L;
	private String domain;
	private Date expiryDate;
	private String name;
	private String value;

	public Date getCookieDate() {
		return this.expiryDate;
	}

	public String getCookieDomain() {
		return this.domain;
	}

	public String getCookieName() {
		return this.name;
	}

	public String getCookieValue() {
		return this.value;
	}

	public void setCookieDate(Date paramDate) {
		this.expiryDate = paramDate;
	}

	public void setCookieDomain(String paramString) {
		this.domain = paramString;
	}

	public void setCookieName(String paramString) {
		this.name = paramString;
	}

	public void setCookieValue(String paramString) {
		this.value = paramString;
	}
}