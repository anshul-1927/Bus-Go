package dev.datvt.busfun.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import dev.datvt.busfun.R;
import dev.datvt.busfun.utils.TransparentProgressDialog;
import dev.datvt.busfun.utils.Variables;

public class NewsActivity extends Activity {

    private WebView webView;
    private String link;
    private TransparentProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);
        Bundle bundle = getIntent().getExtras();
        setTitle(R.string.app_name);
        link = bundle.getString(Variables.LINK);
        webView = (WebView) findViewById(R.id.wvNews);

        webView.getSettings().setSupportZoom(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebViewClient(new ReadNewWebViewClient());
        progressDialog = new TransparentProgressDialog(this, R.drawable.ic_loading);
        progressDialog.show();
        new NewsTask().execute();
    }

    class NewsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(link);
                }
            });

            return null;
        }
    }

    class ReadNewWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            super.onPageFinished(view, url);
        }
    }
}
