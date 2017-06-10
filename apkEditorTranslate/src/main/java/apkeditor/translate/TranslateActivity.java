package apkeditor.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.heagoo.apkeditor.translate.TranslateItem;
import com.gmail.heagoo.common.ActivityUtil;
import com.gmail.heagoo.common.IOUtils;

public class TranslateActivity extends Activity implements OnClickListener {

    // Standard code like "-zh-rCN"
    private String targetLanguageCode;
    private String translatedFilePath;
    private LinearLayout stringLayout;

    // All the string views
    private Map<String, EditText> etMap = new HashMap<String, EditText>();
    private LinearLayout translatingLayout;
    private LinearLayout translatedLayout;
    private TextView translatingMsg;
    private TextView translatedMsg;
    private Button stopOrSaveBtn;

    private boolean translateFinished = false;
    private boolean translationSaved = false;

    // Translate list includes items already translated
    private List<TranslateItem> translatedList;
    private List<TranslateItem> untranslatedList;
    private TranslateTask translatingTask;

    // How many items need to be translated
    private int numToTranslate = 0;
    private int numSucceed = 0;
    private int numFailed = 0;

    // Style control
    private boolean isFullScreen;
    private boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        this.isFullScreen = false;
        this.isDark = false;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            isFullScreen = ActivityUtil.getBoolParam(intent, "isFullScreen");
            isDark = ActivityUtil.getBoolParam(intent, "isDark");
        }

        if (isFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (isDark) {
            setTheme(android.R.style.Theme_Black_NoTitleBar);
            setContentView(R.layout.activity_autotranslate_dark);
        } else {
            setContentView(R.layout.activity_autotranslate);
        }

        initData(intent);

        initView(translatedList);

        // Start the translating
        this.startTranslating();
    }

    @SuppressWarnings("unchecked")
    private void initData(Intent intent) {
        if (intent.getExtras() != null) {
            this.targetLanguageCode = ActivityUtil.getParam(intent,
                    "targetLanguageCode");
            this.translatedFilePath = ActivityUtil.getParam(intent,
                    "translatedList_file");
            this.translatedList = (List<TranslateItem>) IOUtils
                    .readObjectFromFile(translatedFilePath);
            String path = ActivityUtil
                    .getParam(intent, "untranslatedList_file");
            this.untranslatedList = (List<TranslateItem>) IOUtils
                    .readObjectFromFile(path);
        }
    }

    private void initView(List<TranslateItem> strings) {
        this.stringLayout = (LinearLayout) this
                .findViewById(R.id.strings_layout);

        this.translatingLayout = (LinearLayout) this
                .findViewById(R.id.translating_layout);
        this.translatedLayout = (LinearLayout) this
                .findViewById(R.id.translated_layout);
        this.translatingMsg = (TextView) translatingLayout
                .findViewById(R.id.translating_msg);
        this.translatedMsg = (TextView) translatedLayout
                .findViewById(R.id.translated_msg);

        if (strings != null) {
            for (int i = 0; i < strings.size(); i++) {
                TranslateItem item = strings.get(i);
                View strView = createStringView(item);
                stringLayout.addView(strView);
            }
        }

        this.stopOrSaveBtn = (Button) this.findViewById(R.id.btn_stop_or_save);
        stopOrSaveBtn.setOnClickListener(this);
    }

    // Transfer modified files to parent activity
    private void setResult(List<TranslateItem> stringValues) {
        Intent intent = new Intent();
        intent.putExtra("targetLanguageCode", this.targetLanguageCode);
        IOUtils.writeObjectToFile(this.translatedFilePath, stringValues);
        intent.putExtra("translatedList_file", this.translatedFilePath);

        this.setResult(RESULT_OK, intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_stop_or_save) {
            if (this.translateFinished) {
                saveStringAsResource();
                this.translationSaved = true;
                this.finish();
            } else {
                stopTranslating();
            }
        }
    }

    private void showSaveDialog(final boolean bStartNewTranslation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_translation_title);
        builder.setMessage(R.string.save_translation_msg);
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // First save the translation and then do the
                        // translate
                        saveStringAsResource();
                        TranslateActivity.this.translationSaved = true;
                        if (bStartNewTranslation) {
                            // newTranslationTask();
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Show "x strings are discarded" and start a
                        // new translation
                        String msg = getResources().getString(
                                R.string.string_notsaved_msg);
                        msg = String.format(msg, numSucceed);
                        Toast.makeText(TranslateActivity.this, msg,
                                Toast.LENGTH_LONG).show();
                        // newTranslationTask();
                    }
                });
        builder.show();
    }

    // private void newTranslationTask() {
    // this.hide();
    //
    // ApkInfoActivity activity = activityRef.get();
    // activity.startNewTranslation();
    // }

    // Forcely stop it
    private void stopTranslating() {
        translatingTask.cancel(true);
        translateCompleted();
    }

    // To save the translation result
    private void saveStringAsResource() {
        // Prepare translated values (collect from the edit text)
        List<TranslateItem> stringValues = new ArrayList<TranslateItem>();
        for (Map.Entry<String, EditText> entry : etMap.entrySet()) {
            String name = entry.getKey();
            String translatedVal = entry.getValue().getText().toString();
            if (!"".equals(translatedVal)) {
                stringValues.add(new TranslateItem(name, null, translatedVal));
            }
        }

        // No translated string
        if (stringValues.isEmpty()) {
            // Toast.makeText(this, R.string.error_no_string_tosave,
            // Toast.LENGTH_LONG).show();
            return;
        }

        setResult(stringValues);
    }

    @SuppressLint("InflateParams")
    protected View createStringView(TranslateItem item) {

        int itemLayoutId = this.isDark ? R.layout.item_stringvalue_translate_dark
                : R.layout.item_stringvalue_translate;
        View stringView = getLayoutInflater()
                .inflate(itemLayoutId, null, false);

        TextView tv = (TextView) stringView.findViewById(R.id.string_name);
        tv.setText(item.name);

        tv = (TextView) stringView.findViewById(R.id.origin_value);
        tv.setText(item.originValue);

        EditText et = (EditText) stringView.findViewById(R.id.translated_value);
        etMap.put(item.name, et); // Record it
        if (item.translatedValue != null) {
            et.setText(item.translatedValue);
        }

        return stringView;
    }

    // Update views
    @SuppressLint("DefaultLocale")
    public void updateView(List<TranslateItem> items) {
        for (TranslateItem item : items) {
            View v = this.createStringView(item);
            this.stringLayout.addView(v);

            if (item.translatedValue != null) {
                this.numSucceed += 1;
            } else {
                this.numFailed += 1;
            }
        }

        String strTranslated = this.getString(R.string.translated);
        int total = this.numToTranslate - this.numFailed;
        String msg = String.format("%d / %d " + strTranslated, this.numSucceed,
                total);
        this.translatingMsg.setText(msg);
    }

    // Naturally completed, not by force stop
    public void translateCompleted() {
        this.translateFinished = true;
        this.translatingLayout.setVisibility(View.GONE);
        this.translatedLayout.setVisibility(View.VISIBLE);

        String msg = null;
        if (numToTranslate == 0) {
            msg = getString(R.string.error_no_string_totranslate);
        } else {
            msg = String.format(getString(R.string.translated_format),
                    numSucceed);
        }

        // Failed number
        if (numFailed > 0) {
            msg += String.format(", " + getString(R.string.failed_format),
                    numFailed);
        }

        // Untranslated number
        int untranslatedNum = numToTranslate - numSucceed - numFailed;
        if (untranslatedNum > 0) {
            msg += String.format(
                    ", " + getString(R.string.untranslated_format),
                    untranslatedNum);
        }

        this.translatedMsg.setText(msg);

        // Change the button text
        if (numSucceed > 0) {
            stopOrSaveBtn.setText(R.string.save_and_close);
        } else {
            stopOrSaveBtn.setText(R.string.close);
        }
    }

    private void startTranslating() {

        // Update the view
        this.translatingMsg.setText(R.string.translating);
        this.translatingLayout.setVisibility(View.VISIBLE);
        this.translatedLayout.setVisibility(View.GONE);
        this.stopOrSaveBtn.setText(R.string.stop);

        this.translationSaved = false;
        this.translateFinished = false;
        this.numToTranslate = (untranslatedList != null ? untranslatedList.size() : 0);
        this.numSucceed = 0;
        this.numFailed = 0;

        if (numToTranslate > 0) {
            this.translatingTask = new TranslateTask(untranslatedList, this);
            translatingTask.execute();
        } else {
            translateCompleted();
        }
    }

    // Get the google language code
    // Convert -zh-rCN to zh-CN
    public String getGoogleLangCode() {
        String code = this.targetLanguageCode.substring(1);
        int pos = code.indexOf("-");
        if (pos != -1) {
            code = code.substring(0, pos + 1) + code.substring(pos + 2);
        }
        return code;
    }

}
