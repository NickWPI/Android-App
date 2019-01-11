package com.lexicon.androidtest.androidtest;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;

public class DisplayDetailsFragment extends Fragment implements OnSearchExchangeListener {
    private RecyclerView m_RecyclerView;
    private DisplayDetailsRecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    private WebView m_WebView;

    private Socket m_Socket;
    private Emitter.Listener m_SocketListener;
    boolean m_StatisticsSet = false;

    private ValueAnimator m_Animator;

    private String m_CurrentExchange = "CCCAGG";

    private TextView m_CurrentExchangeTextView;

    OnLaunchFragmentListener m_FragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLaunchFragmentListener");
        }
        //necessary for launching new fragments from this fragment
        //FragmentRepository.getInstance().addFragment(this.getClass().getName(), this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //FragmentRepository.getInstance().removeFragment(this.getClass().getName());
    }

    private void animateColorFlash(final TextView textView, int color) {
        if(m_Animator != null) {
            m_Animator.cancel();
            m_Animator.end();
            m_Animator.removeAllUpdateListeners();
        }
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), color, Color.BLACK);
        colorAnimation.setDuration(1000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();

        /*textView.clearAnimation();
        final ObjectAnimator colorAnimator = ObjectAnimator.ofObject(textView,
                "textColor", new ArgbEvaluator(), color, Color.BLACK);
        colorAnimator.setDuration(250);
        colorAnimator.start();*/
    }

    private void launchWebSocket(final View view, String tradingString, String exchange) {
        String[] pair = tradingString.split("-");
        String type = "2";
        if(exchange.equals("CCCAGG")) {
            type = "5";
        }
        String message = "{subs: [" + type + "~" + exchange + "~" + pair[0] + "~" + pair[1] + "]}";
        if(m_Socket == null) {
            try {
                m_Socket = IO.socket("https://streamer.cryptocompare.com/");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            m_Socket.connect();
        }
        if(!m_Socket.connected()) {
            m_Socket.connect();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        m_Socket.emit("SubAdd", jsonObject);

        Cryptocurrency cryptocurrency = CryptocurrencyInfo.getInstance().getPrice(tradingString, exchange);
        final float open = Float.parseFloat(cryptocurrency.getPrice()) - Float.parseFloat(cryptocurrency.getPriceChange());

        displayPriceInfo(view, cryptocurrency.getPrice(), cryptocurrency.getPriceChange(), cryptocurrency.getPercentageGained24h());

        m_SocketListener = new Emitter.Listener() {

            private float priceOpen = open;

            @Override
            public void call(final Object... args) {
                ((AppCompatActivity)view.getContext()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String message = (String)args[0];
                        Log.d("SOCKET MESSAGE", message);
                        String[] messageParts = message.trim().split("~");
                        //examine message type, if its a price update, then update price text
                        Log.d("WEBSOCKET", message);
                        if(messageParts.length > 4) {
                            String type = messageParts[0];
                            if(type.equals("5") || type.equals("2")) {
                                String changeType = messageParts[4];
                                int priceChangeType = -1;
                                if(changeType.equals("1")) {
                                    priceChangeType = 0;
                                }
                                if(changeType.equals("2")) {
                                    priceChangeType = 1;
                                }
                                //priceChangeType = Integer.parseInt(changeType);
                                if(priceChangeType > -1) {
                                    Map<String, String> contents = CCUnpacker.unpack(message);
                                    String price = contents.get("PRICE");
                                    price = DisplayFormat.formatString(price);
                                    TextView priceTextView = view.findViewById(R.id.details_price_text);
                                    priceTextView.setText(price);
                                    if(priceChangeType == 0) {
                                        animateColorFlash(priceTextView, Color.rgb(100, 221, 23));
                                    }
                                    else {
                                        animateColorFlash(priceTextView, Color.RED);
                                    }
                                    String open24hour = "";
                                    if(!m_StatisticsSet) {
                                        open24hour = contents.get("OPEN24HOUR");
                                        String low24hour = contents.get("LOW24HOUR");
                                        String high24hour = contents.get("HIGH24HOUR");
                                        boolean open = false;
                                        boolean high = false;
                                        boolean low = false;
                                        if (open24hour != null) {
                                            m_Adapter.setCryptocurrencyStatistic("Open", open24hour);
                                            priceOpen = Float.parseFloat(open24hour);
                                            open = true;
                                        }
                                        if (high24hour != null) {
                                            m_Adapter.setCryptocurrencyStatistic("High", high24hour);
                                            high = true;
                                        }
                                        if (low24hour != null) {
                                            m_Adapter.setCryptocurrencyStatistic("Low", low24hour);
                                            low = true;
                                        }
                                        if(open && high && low) {
                                            m_StatisticsSet = true;
                                        }
                                    }
                                    String volume24hour = contents.get("VOLUME24HOUR");
                                    String volume24hourTo = contents.get("VOLUME24HOURTO");
                                    m_Adapter.setCryptocurrencyStatistic("Volume", volume24hour);
                                    m_Adapter.setCryptocurrencyStatistic("Volume To", volume24hourTo);

                                    //change time stamp
                                    TextView timeStampTextView = view.findViewById(R.id.details_time_stamp_text);
                                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                                    timeStampTextView.setText("as of " + timeStamp.toString());

                                    //change the price change and percentage
                                    TextView changeTextView = view.findViewById(R.id.details_change_text);
                                    float change = Float.parseFloat(price) - priceOpen;
                                    Log.d("PRICEOPEN", String.valueOf(priceOpen));
                                    float percentageChange = (change / priceOpen) * 100;
                                    String sChange = "";
                                    String sPercentageChanged = "";
                                    //format decimal places
                                    DecimalFormat df = new DecimalFormat();
                                    if(change < 0.01) {
                                        df.applyPattern("#.####");
                                        df.setMinimumFractionDigits(4);
                                        sChange = df.format(change);
                                    } else {
                                        df.applyPattern("#.##");
                                        df.setMinimumFractionDigits(2);
                                        sChange = df.format(change);
                                    }
                                    //format to two decimal points
                                    DecimalFormat df2 = new DecimalFormat("#.##");
                                    df2.setMinimumFractionDigits(2);
                                    sPercentageChanged = df.format(percentageChange) + "%";
                                    int color = Color.GRAY;
                                    if(change > 0) {
                                        sChange = "+" + sChange;
                                        color = Color.rgb(100,221,23);
                                    }
                                    else if(change < 0){
                                        color = Color.RED;
                                    }
                                    changeTextView.setText(sChange + " (" + sPercentageChanged + ")");
                                    changeTextView.setTextColor(color);
                                }
                            }
                        }
                    }
                });
            }
        };
        m_Socket.on("m", m_SocketListener);
    }

    private void displayPriceInfo(View view, String price, String priceChange, String percentageChange) {
        //display price of coin
        TextView priceTextView = view.findViewById(R.id.details_price_text);
        priceTextView.setText(DisplayFormat.formatString(price));
        //display change and percentage
        final TextView changeTextView = view.findViewById(R.id.details_change_text);
        //boolean isNumber = percentageChange.matches("[0-9]+");
        float change = Float.parseFloat(percentageChange.replace("%", ""));
        int color = Color.GRAY;
        if (change > 0) {
            priceChange = "+" + priceChange;
            color = Color.rgb(100, 221, 23);
        } else if (change < 0) {
            //priceChange = "-" + priceChange;
            color = Color.RED;
        }
        changeTextView.setText(priceChange + " (" + percentageChange + ")");
        changeTextView.setTextColor(color);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_display_details, parent, false);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        final String currentCryptocurrency = bundle.getString("Symbol");
        final String currentCryptocurrencyFullName = bundle.getString("Name");
        final String currentPrice = bundle.getString("Price");
        final String percentageChange = bundle.getString("PercentChange");
        String priceChange = bundle.getString("PriceChange");

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setTitle(currentCryptocurrencyFullName);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        ScrollView scrollView = (ScrollView)view.findViewById(R.id.details_scroll_view);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        String[] pair = currentCryptocurrency.split("-");
        String toCurrency = "USD";
        if(pair.length > 1) {
            toCurrency = pair[1];
        }

        //display name of coin
        final String symbol = pair[0] + "-" + toCurrency;
        TextView detailsTextView = (TextView)view.findViewById(R.id.details_name_text);
        detailsTextView.setText(currentCryptocurrencyFullName + " (" + symbol + ")");
        //display price of coin
        /*TextView priceTextView = view.findViewById(R.id.details_price_text);
        priceTextView.setText(DisplayFormat.formatString(currentPrice));
        //display change and percentage
        final TextView changeTextView = view.findViewById(R.id.details_change_text);
        //boolean isNumber = percentageChange.matches("[0-9]+");
        float change = Float.parseFloat(percentageChange.replace("%", ""));
        int color = Color.GRAY;
        if (change > 0) {
            priceChange = "+" + priceChange;
            color = Color.rgb(100, 221, 23);
        } else if (change < 0) {
            //priceChange = "-" + priceChange;
            color = Color.RED;
        }
        changeTextView.setText(priceChange + " (" + percentageChange + ")");
            changeTextView.setTextColor(color);*/

        //display time stamp
        TextView timeStampTextView = view.findViewById(R.id.details_time_stamp_text);
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        timeStampTextView.setText("as of " + timeStamp.toString());

        //use this to set as default open since websocket doesn't update
        //this at a consistently fast rate
        //crash - priceChange was reported as an empty string
        final Float open = Float.parseFloat(currentPrice) - Float.parseFloat(priceChange);

        launchWebSocket(view, pair[0] + "-" + toCurrency, m_CurrentExchange);

        /*try {
            m_Socket = IO.socket("https://streamer.cryptocompare.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        m_Socket.connect();

        String message = "{subs: [5~CCCAGG~" + pair[0] + "~" + toCurrency + "]}";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        m_Socket.emit("SubAdd", jsonObject);
        m_SocketListener = new Emitter.Listener() {

            private Float priceOpen = open;

            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String message = (String)args[0];
                        Log.d("SOCKET MESSAGE", message);
                        String[] messageParts = message.trim().split("~");
                        //examine message type, if its a price update, then update price text
                        Log.d("WEBSOCKET", message);
                        if(messageParts.length > 4) {
                            String type = messageParts[0];
                            if(type.equals("5")) {
                                String changeType = messageParts[4];
                                int priceChangeType = -1;
                                if(changeType.equals("1")) {
                                    priceChangeType = 0;
                                }
                                if(changeType.equals("2")) {
                                    priceChangeType = 1;
                                }
                                //priceChangeType = Integer.parseInt(changeType);
                                if(priceChangeType > -1) {
                                    Map<String, String> contents = CCUnpacker.unpack(message);
                                    String price = contents.get("PRICE");
                                    //format decimal places
                                    /*DecimalFormat df3 = new DecimalFormat();
                                    float floatPrice = Float.parseFloat(price);
                                    if(floatPrice < 0.01) {
                                        df3.applyPattern("#.####");
                                        df3.setMinimumFractionDigits(4);
                                        price = df3.format(floatPrice);
                                    } else {
                                        df3.applyPattern("#.##");
                                        df3.setMinimumFractionDigits(2);
                                        price = df3.format(floatPrice);
                                    }*/
                                    /*price = DisplayFormat.formatString(price);
                                    TextView priceTextView = view.findViewById(R.id.details_price_text);
                                    priceTextView.setText(price);
                                    if(priceChangeType == 0) {
                                        animateColorFlash(priceTextView, Color.rgb(100, 221, 23));
                                    }
                                    else {
                                        animateColorFlash(priceTextView, Color.RED);
                                    }
                                    String open24hour = "";
                                    if(!m_StatisticsSet) {
                                        open24hour = contents.get("OPEN24HOUR");
                                        String low24hour = contents.get("LOW24HOUR");
                                        String high24hour = contents.get("HIGH24HOUR");
                                        boolean open = false;
                                        boolean high = false;
                                        boolean low = false;
                                        if (open24hour != null) {
                                            m_Adapter.setCryptocurrencyStatistic("Open", open24hour);
                                            priceOpen = Float.parseFloat(open24hour);
                                            open = true;
                                        }
                                        if (high24hour != null) {
                                            m_Adapter.setCryptocurrencyStatistic("High", high24hour);
                                            high = true;
                                        }
                                        if (low24hour != null) {
                                            m_Adapter.setCryptocurrencyStatistic("Low", low24hour);
                                            low = true;
                                        }
                                        if(open && high && low) {
                                            m_StatisticsSet = true;
                                        }
                                    }
                                    String volume24hour = contents.get("VOLUME24HOUR");
                                    String volume24hourTo = contents.get("VOLUME24HOURTO");
                                    m_Adapter.setCryptocurrencyStatistic("Volume", volume24hour);
                                    m_Adapter.setCryptocurrencyStatistic("Volume To", volume24hourTo);

                                    //change time stamp
                                    TextView timeStampTextView = view.findViewById(R.id.details_time_stamp_text);
                                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                                    timeStampTextView.setText("as of " + timeStamp.toString());

                                    //change the price change and percentage
                                    TextView changeTextView = view.findViewById(R.id.details_change_text);
                                    float change = Float.parseFloat(price) - priceOpen;
                                    Log.d("PRICEOPEN", String.valueOf(priceOpen));
                                    float percentageChange = (change / priceOpen) * 100;
                                    String sChange = "";
                                    String sPercentageChanged = "";
                                    //format decimal places
                                    DecimalFormat df = new DecimalFormat();
                                    if(change < 0.01) {
                                        df.applyPattern("#.####");
                                        df.setMinimumFractionDigits(4);
                                        sChange = df.format(change);
                                    } else {
                                        df.applyPattern("#.##");
                                        df.setMinimumFractionDigits(2);
                                        sChange = df.format(change);
                                    }
                                    //format to two decimal points
                                    DecimalFormat df2 = new DecimalFormat("#.##");
                                    df2.setMinimumFractionDigits(2);
                                    sPercentageChanged = df.format(percentageChange) + "%";
                                    int color = Color.GRAY;
                                    if(change > 0) {
                                        sChange = "+" + sChange;
                                        color = Color.rgb(100,221,23);
                                    }
                                    else if(change < 0){
                                        color = Color.RED;
                                    }
                                    changeTextView.setText(sChange + " (" + sPercentageChanged + ")");
                                    changeTextView.setTextColor(color);
                                }
                            }
                        }
                    }
                });
            }
        };
        m_Socket.on("m", m_SocketListener);*/

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.details_recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        m_Adapter = new DisplayDetailsRecyclerViewAdapter(currentCryptocurrency.split("-")[0]);
        m_Adapter.setCryptocurrencyStatistic("Open", String.valueOf(open));
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);
        m_RecyclerView.setNestedScrollingEnabled(false);

        Button exchangeButton = view.findViewById(R.id.exchange_button);
        exchangeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("FragmentName",DisplayDetailsFragment.class.getName());
                bundle.putString("Symbol", symbol);
                //price, daily change, total change
                m_FragmentListener.onLaunchFragment(SearchExchangeFragment.class, bundle, true);
            }
        });

        m_CurrentExchangeTextView = view.findViewById(R.id.details_exchange_text);

        //enable javascript on webview
        m_WebView = (WebView) view.findViewById(R.id.web_view);
        WebSettings webSettings = m_WebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        m_WebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getCurrentCryptocurrency() {
                return currentCryptocurrency;
            }
        }, "Android");
        m_WebView.loadUrl("about:blank");
        m_WebView.loadUrl("file:///android_asset/index.html");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //doesn't work in onCreateView
        m_CurrentExchangeTextView.setText(m_CurrentExchange);
    }

    @Override
    public void onPause() {
        super.onPause();
        m_Socket.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                m_FragmentListener.onPopFragmentBackstack(null, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        m_Socket.disconnect();
    }

    @Override
    public void onSearchExchangeResult(String result) {
        m_CurrentExchange = result;
    }
}
