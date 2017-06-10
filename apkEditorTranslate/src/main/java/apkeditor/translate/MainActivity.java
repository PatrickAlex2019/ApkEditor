package apkeditor.translate;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.btn_view_free).setOnClickListener(this);
        findViewById(R.id.btn_view_pro).setOnClickListener(this);
    }

    private void viewInMarket(String pkgName) {
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id=" + pkgName)));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_view_free:
                viewInMarket("com.gmail.heagoo.apkeditor");
                break;
            case R.id.btn_view_pro:
                viewInMarket("com.gmail.heagoo.apkeditor.pro");
                break;
        }
    }
}
