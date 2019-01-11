package com.lexicon.androidtest.androidtest;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeFragment extends Fragment implements OnSearchDetailsListener, PortfolioFragment.FragmentLifecycle {

    private OnLaunchFragmentListener m_FragmentListener;

    private EditText m_NameEditText;
    private EditText m_AmountEditText;
    private EditText m_PriceEditText;
    private EditText m_DateEditText;

    String m_NameEditTextString = "";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_trade, parent, false);

        ScrollView scrollView = (ScrollView)view.findViewById(R.id.details_scroll_view);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        m_NameEditText = view.findViewById(R.id.name_edit_text);
        m_AmountEditText = view.findViewById(R.id.amount_edit_text);
        m_PriceEditText = view.findViewById(R.id.price_edit_text);

        final Calendar calender = Calendar.getInstance();
        m_DateEditText = view.findViewById(R.id.date_edit_text);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, monthOfYear);
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                m_DateEditText.setText(sdf.format(calender.getTime()));
            }

        };
        m_DateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) TradeFragment.this.getActivity();
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

                new DatePickerDialog(TradeFragment.this.getContext(), date, calender
                        .get(Calendar.YEAR), calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final ToggleButton toggleButton = view.findViewById(R.id.toggle_button);

        Button addPositionButton = view.findViewById(R.id.add_position_button);
        addPositionButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCompleted()) {
                    Toast.makeText(TradeFragment.this.getContext(),
                            "Form incomplete; cannot add position", Toast.LENGTH_SHORT).show();
                    return;
                }

                //add the data to trade history
                TradeHistoryDao tradeHistoryDao = ApplicationDatabase.getInstance(TradeFragment.this.getContext()).tradeHistoryDao();
                //it should be count() but for some reason that is not working
                int id = tradeHistoryDao.count();
                String fullName = m_NameEditText.getText().toString();
                String name = fullName.split(" ")[0];
                Matcher matcher = Pattern.compile("\\(([^)]+)\\)").matcher(fullName);
                boolean found = matcher.find();
                String symbol = "";
                if(found)
                    symbol = matcher.group(1);
                String quantity = m_AmountEditText.getText().toString();
                String price = m_PriceEditText.getText().toString();
                String stringDate = m_DateEditText.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                Date date = null;
                try {
                    date = sdf.parse(stringDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String averageBuyPrice = "N/A";

                //add the data to the portfolio
                PortfolioDao portfolioDao = ApplicationDatabase.getInstance(TradeFragment.this.getContext()).portfolioDao();
                PortfolioEntity entity = null;
                for(PortfolioEntity e : portfolioDao.getAll()) {
                    if(e.getSymbol().equals(symbol)) {
                        entity = e;
                        break;
                    }
                }
                if(entity != null) {
                    double existingQuantity = Double.parseDouble(entity.getQuantity());
                    double existingPrice = Double.parseDouble(entity.getPurchasePrice());
                    double currentValue = existingQuantity * existingPrice;
                    double newValue = Double.parseDouble(quantity) * Double.parseDouble(price);
                    boolean transactionType = !toggleButton.isChecked();
                    boolean delete = false;
                    if(transactionType) {
                        existingQuantity = existingQuantity + Double.parseDouble(quantity);
                        existingPrice = (currentValue + newValue) / existingQuantity;
                        DecimalFormat df = new DecimalFormat("#.##");
                        entity.setQuantity(df.format(existingQuantity));
                        entity.setPurchasePrice(df.format(existingPrice));
                    }
                    else {
                        DecimalFormat df = new DecimalFormat("#.##");
                        averageBuyPrice = df.format(currentValue / existingQuantity);
                        existingQuantity = Math.max(0, existingQuantity - Double.parseDouble(quantity));
                        if(existingQuantity == 0) {
                            delete = true;
                        }
                        //else {
                        existingPrice = currentValue / existingQuantity;
                        entity.setQuantity(df.format(existingQuantity));
                        entity.setPurchasePrice(df.format(existingPrice));
                       // }
                    }
                    if(delete) {
                        portfolioDao.delete(entity);
                    }
                    else {
                        portfolioDao.update(entity);
                    }
                }
                else {
                    if(!toggleButton.isChecked()) {
                        int id2 = portfolioDao.count();
                        portfolioDao.insert(new PortfolioEntity(id2, id2, name, symbol, price, quantity));
                    }
                }

                if(!toggleButton.isChecked()) {
                    tradeHistoryDao.insert(new TradeHistoryEntity(id, !toggleButton.isChecked(), name, symbol, price, price, quantity, date.getTime()));
                }
                else {
                    tradeHistoryDao.insert(new TradeHistoryEntity(id, !toggleButton.isChecked(), name, symbol, averageBuyPrice, price, quantity, date.getTime()));
                }

                HoldingsFragment fragment = (HoldingsFragment)FragmentRepository.getInstance().getFragment(HoldingsFragment.class.getName());
                fragment.setUpdate(true);

                //go back to holdings fragment
                PortfolioFragment portfolioPragment = (PortfolioFragment)TradeFragment.this.getParentFragment();
                ViewPager viewPager = portfolioPragment.getViewPager();
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                clearEditTexts();
                Toast.makeText(portfolioPragment.getContext(), "Successfully added position", Toast.LENGTH_SHORT).show();
            }
        });

        Button clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEditTexts();
                Toast.makeText(TradeFragment.this.getContext(), "Cleared fields", Toast.LENGTH_SHORT).show();
            }
        });

        m_NameEditText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) TradeFragment.this.getActivity();
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

                Bundle bundle = new Bundle();
                //searchdetailsfragment cannot find tradefragment because it is a child fragment
                bundle.putString("FragmentName", TradeFragment.class.getName());
                m_FragmentListener.onLaunchFragment(SearchDetailsFragment.class, bundle, true);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //doesn't work in onCreateView
        if(!m_NameEditTextString.equals("")) {
            m_NameEditText.setText(m_NameEditTextString);
            m_NameEditTextString = "";
        }
    }

    private boolean isCompleted() {
        if(m_NameEditText.getText().length() > 0 && m_AmountEditText.getText().length() > 0
                && m_PriceEditText.length() > 0 && m_DateEditText.length() > 0) {
            return true;
        }
        return false;
    }

    public void clearEditTexts() {
        m_NameEditText.getText().clear();
        m_AmountEditText.getText().clear();
        m_PriceEditText.getText().clear();
        m_DateEditText.getText().clear();
    }

    @Override
    public void onSearchResult(Fragment searchFragment, String name, String symbol) {
        m_NameEditTextString = name + " (" + symbol + ")";
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }
}
