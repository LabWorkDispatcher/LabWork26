package com.example.laba22.UI;

import static com.example.laba22.data.Constants.APP_LINK_VARIABLE_KEY;
import static com.example.laba22.data.Constants.APP_TOAST_MESSAGE_LOAD_ATTEMPT_START;
import static com.example.laba22.data.Constants.APP_TOAST_MESSAGE_USER_EXIT_ATTEMPT;
import static com.example.laba22.data.Constants.APP_TOAST_MESSAGE_USER_PRESSED_BACK;
import static com.example.laba22.data.Constants.APP_TOAST_MESSAGE_USER_PRESSED_FORWARD;
import static com.example.laba22.data.Constants.APP_TOAST_MESSAGE_USER_PRESSED_RELOAD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.laba22.R;
import com.example.laba22.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.app_loading_text_default1));
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        binding.webView.setWebViewClient(new MyWebViewClient(binding, this));

        if (!checkConnection()) {
            showDialog();
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String link = b.getString(APP_LINK_VARIABLE_KEY);
            Log.d("APP_DEBUGGER", "Found a link: " + link);
            binding.etUrl.setText(link);
        } else {
            Log.d("APP_DEBUGGER", "Didn't find a link.");
        }

        binding.btnGo.setOnClickListener(view -> {
            String url = binding.etUrl.getText().toString().trim();
            if (!url.startsWith("https://") || !url.startsWith("http://")) {
                url = "https://" + url;
            }

            Toast.makeText(this, APP_TOAST_MESSAGE_LOAD_ATTEMPT_START, Toast.LENGTH_SHORT).show();
            binding.webView.loadUrl(url);
        });

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setTitle(getResources().getString(R.string.app_loading_text_default2));
                binding.progressBar.setProgress(newProgress);
                progressDialog.show();
                if (newProgress == 100) {
                    setTitle(binding.webView.getTitle());
                    progressDialog.dismiss();
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        binding.webView.setDownloadListener((s, s1, s2, s3, l) -> {
            if (s != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(s));
                startActivity(i);
            }
        });
    }

    private void showDialog() {
        showDialogCommon(getResources().getString(R.string.app_warning_text_no_internet), "ReestablishConnection", "Close", false);
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            Toast.makeText(this, APP_TOAST_MESSAGE_USER_EXIT_ATTEMPT, Toast.LENGTH_SHORT).show();
            showDialogCommon(getResources().getString(R.string.app_warning_text_exit), "Yes", "No", true);
        }
    }

    private void showDialogCommon(String message, String text1, String text2, boolean shouldCloseOnAnswer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton(text2, (dialogInterface, i) -> dialogInterface.cancel());

        if (!shouldCloseOnAnswer) {
            builder.setPositiveButton(text1, (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        } else {
            builder.setPositiveButton(text1, (dialogInterface, i) -> finish());
        }

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navPrev:
                Toast.makeText(this, APP_TOAST_MESSAGE_USER_PRESSED_BACK, Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.navNext:
                Toast.makeText(this, APP_TOAST_MESSAGE_USER_PRESSED_FORWARD, Toast.LENGTH_SHORT).show();
                if (binding.webView.canGoForward()) {
                    binding.webView.goForward();
                }
                break;
            case R.id.navReload:
                Toast.makeText(this, APP_TOAST_MESSAGE_USER_PRESSED_RELOAD, Toast.LENGTH_SHORT).show();
                //checkConnection();
                binding.webView.reload();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) )
                    {
                        return true;
                    }
                }
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}