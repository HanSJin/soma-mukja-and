package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Constants.Constants;

public class Agreement extends AppCompatActivity {
    WebView webView;
    Button BT_X;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_webview);

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());//클릭시 새창 안뜸
        webView.loadUrl(Constants.API_BASE_URL+"/agreement");

        title = (TextView)findViewById(R.id.title);
        title.setText("오늘뭐먹지 이용약관");

        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("이용약관");

        BT_X = (Button) findViewById(R.id.BT_X);

        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
