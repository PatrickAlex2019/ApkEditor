# apkeditor_plugin_translation
A sample translation plugin for APK Editor (Pro). In this project, src directory contains source code of the sample translation plugin; translate_debugger.zip is a patch to correct some spoiled variables (like "1 $%") which are wrongly translated by the translation plugin. (The patch is provided by a Russian friend)

It only makes sense when APK Editor (Pro) is installed:

APK Editor: https://play.google.com/store/apps/details?id=com.gmail.heagoo.apkeditor

APK Editor Pro: https://play.google.com/store/apps/details?id=com.gmail.heagoo.apkeditor.pro

Please uncomment following lines in AndroidManifest.xml if built for free version:
```
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="application/com.gmail.heagoo.apkeditor-translate" />
    </intent-filter>
```
To develop a translation plugin, you should create your own activity, which can get translation request and return the translation result. The translation request is sent in terms of TranslateItem which is defined as:

```
package com.gmail.heagoo.apkeditor.translate;
 
import java.io.Serializable;

public class TranslateItem implements Serializable {
 
    private static final long serialVersionUID = -3101805950698159689L;
    public String name;
    public String originValue;
    public String translatedValue;
 
    public TranslateItem(String _n, String _o) {
       this.name = _n;
       this.originValue = _o;
    }
 
    public TranslateItem(String _n, String _o, String _t) {
       this.name = _n;
       this.originValue = _o;
       this.translatedValue = _t;
    }
}
```

And, please refer to following code to get all the passed parameters:

```
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       // ...
       Intent intent = getIntent();
       Bundle bundle = intent.getExtras();
       // Target language code like "-de"
       this.targetLanguageCode = bundle.getString("targetLanguageCode");
       // Translated items are also passed, so that we can revise it
       this.translatedFilePath = bundle.getString("translatedList_file");
       this.translatedList = (List<TranslateItem>) readObjectFromFile(translatedFilePath);
       // Untranslated items, which are to be translated
       String path =bundle.getString("untranslatedList_file");
       this.untranslatedList = (List<TranslateItem>) readObjectFromFile(path);
       // ...
    }
 
    public static Object readObjectFromFile(String filePath) {
       Object result = null;
       File file = new File(filePath);
       ObjectInputStream objIn = null;
       try {
           objIn = new ObjectInputStream(new FileInputStream(file));
           result = objIn.readObject();
       } catch (IOException e) {
           e.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       } finally {
           closeWithoutThrow(objIn);
       }
       return result;
    }
```

After the translation, we should return back the result using following code:

```
    private void setResult(List<TranslateItem> stringValues) {
       Intent intent = new Intent();
       intent.putExtra("targetLanguageCode", this.targetLanguageCode);
       writeObjectToFile(this.translatedFilePath, stringValues);
       intent.putExtra("translatedList_file", this.translatedFilePath);
 
       this.setResult(RESULT_OK, intent);
    }
 
    public static void writeObjectToFile(String filePath, Object obj) {
       File file = new File(filePath);
       ObjectOutputStream objOut = null;
       try {
           objOut = new ObjectOutputStream(new FileOutputStream(file));
           objOut.writeObject(obj);
           objOut.flush();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           closeWithoutThrow(objOut);
       }
    }
```

As there may be thousands of TranslateItem, thus it is passed by file, not by itself.
