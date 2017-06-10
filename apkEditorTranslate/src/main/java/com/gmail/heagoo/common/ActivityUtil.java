package com.gmail.heagoo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;

public class ActivityUtil {

	public static Bundle attachParam(Intent intent, String key, String value) {
		Bundle bundle = new Bundle();
		bundle.putString(key, value);
		intent.putExtras(bundle);
		return bundle;
	}

	public static Bundle attachParam(Intent intent, String key, boolean value) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(key, value);
		intent.putExtras(bundle);
		return bundle;
	}

	public static Bundle attachParam(Intent intent, String key, ArrayList<String> value) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(key, value);
		intent.putExtras(bundle);
		return bundle;
	}

	public static Bundle attachParam2(Intent intent, String key, ArrayList<Integer> value) {
		Bundle bundle = new Bundle();
		bundle.putIntegerArrayList(key, value);
		intent.putExtras(bundle);
		return bundle;
	}

	public static Bundle attachParam(Intent intent, String key, int value) {
		Bundle bundle = new Bundle();
		bundle.putInt(key, value);
		intent.putExtras(bundle);
		return bundle;
	}

	public static Bundle attachBoolParam(Intent intent, String key, boolean value) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(key, value);
		intent.putExtras(bundle);
		return bundle;
	}

	public static String getParam(Intent intent, String key) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return bundle.getString(key);
		}
		return null;
	}

	public static boolean getBoolParam(Intent intent, String key) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return bundle.getBoolean(key, false);
		}
		return false;
	}

	public static int getIntParam(Intent intent, String key) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return bundle.getInt(key, 0);
		}
		return 0;
	}

	public static ArrayList<String> getStringArray(Intent intent, String key) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return bundle.getStringArrayList(key);
		}
		return null;
	}

	public static ArrayList<Integer> getIntArray(Intent intent, String key) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return bundle.getIntegerArrayList(key);
		}
		return null;
	}

	public static void attachParam(Intent intent, String key,
								   Map<String, String> mapValue) {
		Bundle bundle = new Bundle();
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		for (String strKey : mapValue.keySet()) {
			String strVal = mapValue.get(strKey);
			keyList.add(strKey);
			valueList.add(strVal);
		}
		bundle.putStringArrayList(key + "_keys", keyList);
		bundle.putStringArrayList(key + "_values", valueList);
		intent.putExtras(bundle);
	}

	public static Map<String, String> getMapParam(Intent intent, String key) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Map<String, String> mapVal = new HashMap<String, String>();
			ArrayList<String> strKeys = bundle
					.getStringArrayList(key + "_keys");
			ArrayList<String> strValues = bundle.getStringArrayList(key
					+ "_values");
			if (strKeys != null && strValues != null) {
				for (int i = 0; i < strKeys.size(); i++) {
					mapVal.put(strKeys.get(i), strValues.get(i));
				}
				return mapVal;
			}
		}
		return null;
	}
}
