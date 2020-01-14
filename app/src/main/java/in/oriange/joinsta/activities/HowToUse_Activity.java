package in.oriange.joinsta.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.oriange.joinsta.R;

public class HowToUse_Activity extends AppCompatActivity {

    private Context context;
    private WebView webview;
    private ProgressBar spi;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_touse);

        init();
        setDefaults();
        setUpToolbar();
    }

    private void init() {
        context = HowToUse_Activity.this;

        webview = findViewById(R.id.webview);
        spi = findViewById(R.id.progressBar);
        tv = findViewById(R.id.textView1);
    }

    private void setDefaults() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String uri) {
                spi.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                view.loadUrl(uri);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (spi.isShown()) {
                    spi.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                }
            }
        });
        String s = "https://docs.google.com/gview?embedded=true&url=http://joinsta.in/images/JoinstaHowtouse.pdf";
        webview.loadUrl(s);
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
