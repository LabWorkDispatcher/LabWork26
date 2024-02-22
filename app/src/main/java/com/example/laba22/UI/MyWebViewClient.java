package com.example.laba22.UI;

import static com.example.laba22.data.Constants.APP_LINK_VARIABLE_KEY;
import static com.example.laba22.data.Constants.APP_PREFERENCE_FILE_KEY;
import static com.example.laba22.data.Constants.APP_TOAST_MESSAGE_LOAD_ATTEMPT_FINISH;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.laba22.databinding.ActivityMainBinding;

public class MyWebViewClient extends WebViewClient {
    private final ActivityMainBinding binding;
    private final Context context;

    public MyWebViewClient(ActivityMainBinding binding, Context context) {
        this.binding = binding;
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.webView.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        binding.progressBar.setVisibility(View.GONE);
        binding.webView.setVisibility(View.VISIBLE);
        Toast.makeText(context, APP_TOAST_MESSAGE_LOAD_ATTEMPT_FINISH, Toast.LENGTH_SHORT).show();
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        sPreferences.edit().putString(APP_LINK_VARIABLE_KEY, url).apply();
        //Log.d("APP_DEBUGGER", "Put a link into shared preferences: " + sPreferences.getString(context.getString(R.string.app_link_variable), "") );
    }
}
