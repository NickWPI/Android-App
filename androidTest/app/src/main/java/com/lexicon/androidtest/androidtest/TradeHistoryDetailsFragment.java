package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TradeHistoryDetailsFragment extends Fragment {

    private OnLaunchFragmentListener m_FragmentListener;

    private RecyclerView m_RecyclerView;
    private TradeHistoryRecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    private TextView m_SymbolTextView;
    private TextView m_TotalValueTextView;
    private TextView m_CurrentPriceTextView;
    private TextView m_QuantityTextView;
    private TextView m_AverageCostTextView;
    private TextView m_DailyChangeTextView;
    private TextView m_TotalChangeTextView;

    private Socket m_Socket;
    private Emitter.Listener m_SocketListener;

    List<TradeHistoryEntity> m_Entities = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_trade_history_details, parent, false);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        final String symbol = bundle.getString("Symbol");
        final String name = bundle.getString("Name");
        final String quantity = bundle.getString("Quantity");
        final String averageCost = bundle.getString("Average Cost");

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        try {
            m_Socket = IO.socket("https://streamer.cryptocompare.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        m_Socket.connect();

        final String[] pair = symbol.split("-");
        String message = "{subs: [5~CCCAGG~" + pair[0] + "~" + pair[1] + "]}";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        m_Socket.emit("SubAdd", jsonObject);

        m_SymbolTextView = view.findViewById(R.id.symbol_text_view);
        m_TotalValueTextView = view.findViewById(R.id.total_value_text_view);
        m_CurrentPriceTextView = view.findViewById(R.id.price_text_view);
        m_QuantityTextView = view.findViewById(R.id.quantity_text_view);
        m_AverageCostTextView = view.findViewById(R.id.average_cost_text_view);
        m_DailyChangeTextView = view.findViewById(R.id.daily_change_text_view);
        m_TotalChangeTextView = view.findViewById(R.id.total_change_text_view);

        m_SymbolTextView.setText(symbol);
        m_QuantityTextView.setText("Quantity: " + quantity + " " + pair[0]);
        m_AverageCostTextView.setText("Average Cost: " + averageCost + " " + pair[1]);

        Cryptocurrency cryptocurrency = CryptocurrencyInfo.getInstance().getPrice(symbol);
        m_CurrentPriceTextView.setText("Current Price: " + DisplayFormat.formatString(cryptocurrency.getPrice()) + " " + pair[1]);
        m_DailyChangeTextView.setText("Daily Change: " + cryptocurrency.getPercentageGained24h());

        final Double open = Double.parseDouble(cryptocurrency.getPrice()) - Double.parseDouble(cryptocurrency.getPriceChange());
        final Double doubleAverageCost = Double.parseDouble(averageCost);

        Double price = Double.parseDouble(cryptocurrency.getPrice());
        Double totalChange = price - doubleAverageCost;
        Double totalPercentChange = (totalChange / doubleAverageCost) * 100;
        m_TotalChangeTextView.setText("Total Change: " + DisplayFormat.formatPercentString(String.valueOf(totalPercentChange)) + "%");

        Double totalValue = price * Double.parseDouble(quantity);
        m_TotalValueTextView.setText("Total Value: $"
                + DisplayFormat.formatString(String.valueOf(totalValue)));

        m_SocketListener = new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String message = (String)args[0];
                        String[] messageParts = message.trim().split("~");
                        if(messageParts.length > 4) {
                            String type = messageParts[0];
                            if (type.equals("5")) {
                                String changeType = messageParts[4];
                                if (changeType.equals("1") || changeType.equals("2")) {
                                    Map<String, String> contents = CCUnpacker.unpack(message);
                                    String price = contents.get("PRICE");
                                    String formattedPrice = DisplayFormat.formatString(price);
                                    m_CurrentPriceTextView.setText("Current Price: " + formattedPrice + " " + pair[1]);
                                    Double totalValue = Double.parseDouble(formattedPrice) * Double.parseDouble(quantity);
                                    m_TotalValueTextView.setText("Total Value: $"
                                            + DisplayFormat.formatString(String.valueOf(totalValue)));

                                    Double doublePrice = Double.parseDouble(price);

                                    Double priceChange = doublePrice - open;
                                    Double percentChange = (priceChange / doublePrice) * 100;
                                    m_DailyChangeTextView.setText("Daily Change: " + DisplayFormat.formatPercentString(String.valueOf(percentChange)) + "%");

                                    Double totalChange = doublePrice - doubleAverageCost;
                                    Double totalPercentChange = (totalChange / doubleAverageCost) * 100;
                                    m_TotalChangeTextView.setText("Total Change: " + DisplayFormat.formatPercentString(String.valueOf(totalPercentChange)) + "%");
                                }
                            }
                        }
                    }
                });
            }
        };
        m_Socket.on("m", m_SocketListener);

        TradeHistoryDao tradeHistoryDao = ApplicationDatabase.getInstance(this.getContext()).tradeHistoryDao();
        for(TradeHistoryEntity entity : tradeHistoryDao.getAll()) {
            if(entity.getSymbol().equals(symbol)) {
                m_Entities.add(entity);
            }
        }

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);
        m_Adapter = new TradeHistoryRecyclerViewAdapter(m_Entities);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(10));
        m_RecyclerView.setAdapter(m_Adapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
}
