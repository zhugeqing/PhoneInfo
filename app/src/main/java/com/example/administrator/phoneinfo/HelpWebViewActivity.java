package com.example.administrator.phoneinfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HelpWebViewActivity extends AppCompatActivity {

    private WebView webViewhelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help_web_view);
        MyApplication myApplication = (MyApplication) getApplicationContext();
        webViewhelp=(WebView) findViewById(R.id.web_view_help_id);
        webViewhelp.getSettings().setJavaScriptEnabled(true);
        webViewhelp.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
                view.loadUrl(request.getUrl().toString());
                return true;
            }

        });
        webViewhelp.loadUrl("http://120.199.120.85:50080/wzgjgl_mobile/cloudmap/help/cloudmap_help.htm");
    }
}
