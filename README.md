# apkeditor_plugin_translation
A sample translation plugin for APK Editor (Pro).

It only makes sense when APK Editor (Pro) is installed:

APK Editor: https://play.google.com/store/apps/details?id=com.gmail.heagoo.apkeditor

APK Editor Pro: https://play.google.com/store/apps/details?id=com.gmail.heagoo.apkeditor.pro

Please uncomment following lines in AndroidManifest.xml if built for free version:
  <intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <data android:mimeType="application/com.gmail.heagoo.apkeditor-translate" />
  </intent-filter>
