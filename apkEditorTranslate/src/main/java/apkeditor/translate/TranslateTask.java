package apkeditor.translate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gmail.heagoo.apkeditor.translate.TranslateItem;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TranslateTask extends
		AsyncTask<Void, ArrayList<TranslateItem>, Boolean> {

	private List<TranslateItem> untranslated;
	private WeakReference<TranslateActivity> activityRef;

	Random r = new Random();
	private String userAgent;

    public TranslateTask(List<TranslateItem> untranslated,
            TranslateActivity activity) {
		this.untranslated = untranslated;
		this.activityRef = new WeakReference<TranslateActivity>(activity);
	}

	@Override
	protected void onPreExecute() {
		this.userAgent = getUserAgent(activityRef.get());
	}

	@Override
	protected void onPostExecute(Boolean result) {
		activityRef.get().translateCompleted();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Random r = new Random(System.currentTimeMillis());
		final int maxItems = 10 + r.nextInt(5);
		final int maxChars = 900;

		String code = activityRef.get().getGoogleLangCode();
		Translator translator = new Translator(userAgent, code);

		List<TranslateItem> todo = new ArrayList<TranslateItem>();
		for (TranslateItem item : untranslated) {
			if (this.isCancelled()) {
				return false;
			}

			// Multiple lines
			if (item.originValue.contains("\n")) {
				if (!todo.isEmpty()) {
					doTranslate(translator, todo);
				}

				todo.add(item);
				doTranslate(translator, todo);
			} else if (todo.size() >= maxItems
					|| (getTotalCharaters(todo) + item.originValue.length()) > maxChars) {
				doTranslate(translator, todo);
				todo.add(item);
			} else {
				todo.add(item);
			}
		}

		if (this.isCancelled()) {
			return todo.isEmpty();
		}

		// Remaining
		if (!todo.isEmpty()) {
			doTranslate(translator, todo);
		}

		return true;
	}

	// Compute how many chars
	private int getTotalCharaters(List<TranslateItem> todo) {
		int totalLen = 0;
		for (TranslateItem item : todo) {
			totalLen += item.originValue.length();
		}
		return totalLen;
	}

	private void doTranslate(Translator translator, List<TranslateItem> todo) {
		// No string need to be translated
		if (todo.isEmpty()) {
			return;
		}

		// Manually sleep awhile, so that I am not taken as a robot
		try {
			Thread.sleep(300 + r.nextInt(500));
		} catch (InterruptedException e) {
		}

		if (this.isCancelled()) {
			return;
		}

		translator.translate(todo);
		checkResult(todo);
		todo.clear();
	}

	// Check how many items are successfully translated
	@SuppressWarnings("unchecked")
	private void checkResult(List<TranslateItem> translatedItems) {
		ArrayList<TranslateItem> copied = new ArrayList<TranslateItem>();
		copied.addAll(translatedItems);
		this.publishProgress(copied);
	}

	@Override
	protected void onProgressUpdate(ArrayList<TranslateItem>... args) {
		ArrayList<TranslateItem> translated = args[0];
		activityRef.get().updateView(translated);
	}

	private static String getUserAgent(Context ctx) {
		String ua = null;

		WebView webview = new WebView(ctx);
		webview.layout(0, 0, 0, 0);
		WebSettings settings = webview.getSettings();
		if (settings != null) {
			ua = settings.getUserAgentString();
		}
		if (ua != null) {
			String strNT = " NX";
			StringBuffer sb = new StringBuffer();
			sb.append("Mozilla/5.0");
			sb.append(" (Windows");
			strNT = strNT.replace('X', 'T');
			sb.append(strNT.concat(" 6.1; WOW64) "));
			sb.append("AppleWebKit/537.11 (KHTML, like Gecko) ");
			sb.append("Chrome/23.0.1271.97 ");
			sb.append("Safari/537.11");
			ua = sb.toString();
		}

		return ua;
	}
}