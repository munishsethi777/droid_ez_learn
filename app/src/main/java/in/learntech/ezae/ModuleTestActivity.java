package in.learntech.ezae;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ModuleTestActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.learntech.ezae.R.layout.activity_module_test);
        webView = (WebView)findViewById(in.learntech.ezae.R.id.module_web_View);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        webView.loadUrl("http://ready2godemos.s3-ap-south-1.amazonaws.com/CustomerDemos/my_aircel_way_output/story_html5.html");
    }
}
