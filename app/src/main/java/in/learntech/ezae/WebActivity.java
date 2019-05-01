package in.learntech.ezae;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.learntech.ezae.R.layout.activity_web_view);
        LinearLayout linearLayout = (LinearLayout)findViewById(in.learntech.ezae.R.id.layout_web);
        String url = "http://docs.google.com/gview?url=http://www.ezae.in/docs/moduledocs/Book1.xlsx&embedded=true";
        WebView webView = new WebView(getApplicationContext());//(WebView) findViewById(R.id.webView);
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        linearLayout.addView(webView);
    }
}
