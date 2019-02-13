package com.example.administrator.phoneinfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CellInfoWebViewActivity extends AppCompatActivity {

    private WebView webView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cellinfo_webview);
        MyApplication myApplication = (MyApplication) getApplicationContext();
        webView1=(WebView) findViewById(R.id.web_view_id);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
                view.loadUrl(request.getUrl().toString());
                return true;
            }

        });
        webView1.loadUrl("http://120.199.120.85:50080/wzgjgl_mobile/RedisQuery/CellQuery/CellQueryDetail.aspx?id="+myApplication.link_CellId+"&UserToken=A7866216BFA9EFC0");
    }
}
