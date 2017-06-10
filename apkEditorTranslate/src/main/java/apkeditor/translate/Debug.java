package apkeditor.translate;

import android.util.Log;

public class Debug {
	public static void dumpClassName(Object paramObject) {
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = paramObject.getClass().getName();
		log("** Class Name: %", arrayOfObject);
	}

	public static void log(String paramString, Object... args) {
		//Log.d("DEBUG", String.format(paramString, args) + "\n");
	}
	
	public static void dump(String name, String content) {
		//Log.d("DEBUG", name + ": ****\n" + content);
	}
}