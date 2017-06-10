package apkeditor.translate;

import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.gmail.heagoo.apkeditor.translate.TranslateItem;

public class Translator {

	private WebBrowser browser;
	private String tkk;
	private String startUrl;

	// This is a temporary var
	private int jsonIndex = 0;

	private String targetLangCode;

	public Translator(String userAgent, String _target) {
		this.browser = new WebBrowser(userAgent);
		this.startUrl = "https://translate.google.com/m/translate";
		this.targetLangCode = _target;

		String mainPageContent = browser.get("main", startUrl, null);

		// Can not get the content of the main page
		if (mainPageContent == null) {
			return;
		}

		final String keyword = "tkk:'";
		int startPos = mainPageContent.indexOf(keyword);
		// ERROR
		if (startPos == -1) {
			return;
		}

		// Extract the tkk
		startPos += keyword.length();
		int endPos = mainPageContent.indexOf("'", startPos);
		this.tkk = mainPageContent.substring(startPos, endPos);
	}

	// javaScriptCode is like: ((function(){var a\x3d3253064315;var b\x3d-757794981;return 415855+\x27.\x27+(a+b)})())
	private static String unescapeTkkScript(String javaScriptCode) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < javaScriptCode.length(); i++) {
			char c = javaScriptCode.charAt(i);
			if (c == '\\' && (i + 3 < javaScriptCode.length())
					&& javaScriptCode.charAt(i + 1) == 'x') {
				char dc = decodeChar(javaScriptCode.charAt(i + 2),
						javaScriptCode.charAt(i + 3));
				sb.append(dc);
				i += 3;
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static char decodeChar(char c1, char c2) {
		int i1 = hex2Int(c1);
		int i2 = hex2Int(c2);
		return (char)(i1 * 16 + i2);
	}

	private static int hex2Int(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		} else if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		} else {
			return c - 'A' + 10;
		}
	}

	protected String encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}
	
	protected static String getToken(String tkk, String q) {
		String tkkScript = unescapeTkkScript(tkk);
		
		String javaScriptCode = "var Wl=function(a, b) {"
				+ "for (var c = 0; c < b.length - 2; c += 3) {"
				+ "var d = b.charAt(c + 2)"
				+ " , d = \"a\" <= d ? d.charCodeAt(0) - 87 : Number(d)"
				+ " , d = \"+\" == b.charAt(c + 1) ? a >>> d : a << d;"
				+ "a = \"+\" == b.charAt(c) ? a + d & 4294967295 : a ^ d"
				+ "}"
				+ "return a"
				+ "},"
				
				+ "Yl = function(b,a) {"
				+    "var c,d;"
				+    "d = b.split(\".\");"
				+    "b = Number(d[0]) || 0;"
				+    "for (var e = [], f = 0, g = 0; g < a.length; g++) {"
				+        "var k = a.charCodeAt(g);"
				+        "128 > k ? e[f++] = k : (2048 > k ? e[f++] = k >> 6 | 192 : (55296 == (k & 64512) && g + 1 < a.length && 56320 == (a.charCodeAt(g + 1) & 64512) ? (k = 65536 + ((k & 1023) << 10) + (a.charCodeAt(++g) & 1023),"
				+        "e[f++] = k >> 18 | 240,"
				+        "e[f++] = k >> 12 & 63 | 128) : e[f++] = k >> 12 | 224,"
				+        "e[f++] = k >> 6 & 63 | 128),"
				+        "e[f++] = k & 63 | 128)"
				+    "}"
				+    "a = b;"
				+    "for (f = 0; f < e.length; f++)"
				+        "a += e[f],"
				+        "a = Wl(a, \"+-a^+6\");"
				+    "a = Wl(a, \"+-3^+b+-f\");"
				+    "a ^= Number(d[1]) || 0;"
				+    "0 > a && (a = (a & 2147483647) + 2147483648);"
				+    "a = a % 1E6;"
				+    "return a.toString() + \".\" +" 
				+    "(a ^ b)"
				+"};";
		
		// Every Rhino VM begins with the enter()
		// This Context is not Android's Context
		Context rhino = Context.enter();

		// Turn off optimization to make Rhino Android compatible
		rhino.setOptimizationLevel(-1);
		try {
			Scriptable scope = rhino.initStandardObjects();

			// Note the forth argument is 1, which means the JavaScript source
			// has
			// been compressed to only one line using something like YUI
			Object ret = rhino.evaluateString(scope, tkkScript,
					"JavaScript", 1, null);

			rhino.evaluateString(scope, javaScriptCode,
					"JavaScript", 1, null);

			// Get the functionName defined in JavaScriptCode
			Object obj = scope.get("Yl", scope);

			if (obj instanceof Function) {
				Function jsFunction = (Function) obj;

				// Call the function with params
				Object jsResult = jsFunction.call(rhino, scope, scope, new Object[] {ret.toString(), q});

				// Parse the jsResult object to a String
				String result = Context.toString(jsResult);
				return result;
			}
		} finally {
			Context.exit();
		}
		
		return null;
	}

//	protected static void getToken_Old(String tkk, String a) {
//		String originStr = decodeTkk(tkk);
//		String[] d = new String[2];
//		int dotPos = originStr.charAt('.');
//		if (dotPos != -1) {
//			d[0] = originStr.substring(0, dotPos);
//			d[1] = originStr.substring(dotPos + 1);
//		}
//		
//		int c = Integer.valueOf(tkk);
//		int b = Integer.valueOf(d[0]);
//		int g = 0;
//		long new_c = 0;
//		StringBuffer d = new StringBuffer();
//		for (; g < a.length(); g++) {
//			int k = (int) a.charAt(g);
//			if (k < 128) {
//				d.append((char) k);
//			} else {
//				if (k < 2048) {
//					d.append((char) ((k >> 6) | 192));
//				} else {
//					d.append((char) ((k >> 12) | 224));
//					d.append((char) ((k >> 6) & 63 | 128));
//				}
//				d.append((char) (k & 63 | 128));
//			}
//		}
//		for (int i = 0; i < d.length(); i++) {
//			c += (int) d.charAt(i);
//			c += c << 10;
//			c ^= c >>> 6;
//		}
//		c += c << 3;
//		c ^= c >>> 11;
//		c = c + (c << 15) & 0xffffffff;
//		if (c < 0) {
//			new_c = (c & 0x7fffffff) + 0x80000000L;
//		} else {
//			new_c = c;
//		}
//		new_c %= 1E6;
//		return String.valueOf(new_c) + "|" + String.valueOf(new_c ^ b);
//	}

	public void translate(List<TranslateItem> items) {
		// This may be caused by main page loading fail
		if (tkk == null) {
			return;
		}

		// Concat the string
		StringBuffer queryBuf = new StringBuffer();
		for (TranslateItem item : items) {
			queryBuf.append(item.originValue);
			queryBuf.append('\n');
		}
		queryBuf.deleteCharAt(queryBuf.length() - 1);
		String q = queryBuf.toString();

		String url = "https://translate.google.com/translate_a/single?"
				+ "client=webapp&sl=auto&tl="
				+ targetLangCode
				+ "&hl=en&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&otf=1&ssel=0&tsel=0&kc=1&tk="
				+ encode(getToken(tkk, q)) + "&q=" + encode(q);
		Debug.log("url=%s", url);
		String content = browser.get("translate", url, startUrl);

		// Succeed
		if (content != null && content.startsWith("[[[\"")) {
			int position = content.indexOf("]]");
			if (position != -1) {
				parseContent(content.substring(1, position + 2), items);
			}
		}
	}

	// Parse the content and save result to items
	private void parseContent(String str, List<TranslateItem> items) {
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONArray values = (JSONArray) jsonParser.nextValue();
			// Only one item, all the translation content should save to it
			if (items.size() == 1) {
				items.get(0).translatedValue = extractAllValueFromJson(values);
//				Log.d("DEBUG",
//						items.get(0).originValue + " ---> "
//								+ items.get(0).translatedValue);
			} else {
				int srcNum = items.size();
				this.jsonIndex = 0;
				for (int i = 0; i < srcNum; i++) {
					TranslateItem item = items.get(i);
					item.translatedValue = extractOneItemFromJson(values);
//					Log.d("DEBUG", item.originValue + " ---> "
//							+ item.translatedValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// In Json, one item may be splited into several arrays
	// jsonIndex will be used
	private String extractOneItemFromJson(JSONArray values)
			throws JSONException {
		StringBuffer sb = new StringBuffer();
		while (jsonIndex < values.length() - 1) {
			JSONArray oneArray = (JSONArray) values.get(jsonIndex++);
			String curVal = (String) oneArray.get(0);
			if (curVal == null) {
				break;
			}

			sb.append(curVal);
			if (curVal.endsWith("\n")) {
				sb.deleteCharAt(sb.length() - 1);
				break;
			}
		}
		if (sb.length() > 0) {
			return sb.toString();
		}
		return null;
	}

	private String extractAllValueFromJson(JSONArray values)
			throws JSONException {
		int jsonIndex = 0;
		StringBuffer sb = new StringBuffer();
		while (jsonIndex < values.length() - 1) {
			JSONArray oneArray = (JSONArray) values.get(jsonIndex++);
			String curVal = (String) oneArray.get(0);
			if (curVal == null) {
				break;
			}

			sb.append(curVal);
		}
		if (sb.length() > 0) {
			return sb.toString();
		}
		return null;
	}
}
