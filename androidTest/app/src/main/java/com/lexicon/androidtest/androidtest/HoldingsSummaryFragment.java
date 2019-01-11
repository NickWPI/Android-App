package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HoldingsSummaryFragment extends Fragment implements PortfolioFragment.FragmentLifecycle{
    private OnLaunchFragmentListener m_FragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLaunchFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holdings_summary, parent, false);
        WebView webview = view.findViewById(R.id.web_view);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        /*webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getCurrentCryptocurrency() {
                return currentCryptocurrency;
            }
        }, "Android");*/
        webview.loadUrl("about:blank");
        webview.loadUrl("file:///android_asset/holdings_summary/index.html");
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }
}
