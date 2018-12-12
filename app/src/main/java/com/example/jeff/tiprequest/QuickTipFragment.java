package com.example.jeff.tiprequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class QuickTipFragment extends Fragment {

    private DatabaseReference databaseReference;

    private double tipPercent = .1;
    private double subtotal = 0.0;
    private double total = 0.0;
    private double tipAmount = 0.0;
    private String location = "";
    private LatLng locLatLng = new LatLng(0.0, 0.0);
    private static TextView tv_date;
    Boolean tipSelected = false;
    EditText etSub;
    EditText etTip;
    TextView tvPercent;
    TextView tvTotal;
    Button addBtn;
    Button calcBtn;
    ImageView receiptImage;

    String accountID = UserInfo.getAccountID();
    String accountName = UserInfo.getAccountName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quick_tip_fragment, container, false);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                location = place.getName().toString();
                locLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        receiptImage = (ImageView) rootView.findViewById(R.id.receiptImage);
        tvTotal = (TextView) rootView.findViewById(R.id.tvTotal);
        calcBtn = (Button) rootView.findViewById(R.id.calcBtn);

        // CALCULATE
        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
                updateTotalView();
            }
        });

        // ADD
        addBtn = (Button) rootView.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
                showAddDialog();
            }
        });
        etTip = (EditText) rootView.findViewById(R.id.etTip);
        etTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    double temp = Double.valueOf(s.toString());
                    tipPercent = temp * 0.01;
                    System.out.println(tipPercent);
                } else {
                    tipPercent = 0.1;
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        tvPercent = (TextView) rootView.findViewById(R.id.tvPercent);
        etSub = (EditText) rootView.findViewById(R.id.etSubtotal);
        etSub.setSelected(false);
        etSub.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = "";
                String text = s.toString();
                updateButtons();
                if (!text.equals(current)){
                    etSub.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");
                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2,BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
                    subtotal = parsed.doubleValue();
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed);

                    current = formatted;
                    etSub.setText(formatted);
                    etSub.setSelection(formatted.length());

                    etSub.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        final RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tipSelected = true;
                updateButtons();
                View radioButton = group.findViewById(checkedId);
                int position = group.indexOfChild(radioButton);
                switch (position) {
                    case 0:
                        tipPercent = .1;
                        etTip.setVisibility(View.GONE);
                        tvPercent.setVisibility(View.GONE);
                        break;
                    case 1:
                        tipPercent = .15;
                        etTip.setVisibility(View.GONE);
                        tvPercent.setVisibility(View.GONE);
                        break;
                    case 2:
                        tipPercent = .20;
                        etTip.setVisibility(View.GONE);
                        tvPercent.setVisibility(View.GONE);
                        break;
                    case 3:
                        tipPercent = .10;
                        etTip.setVisibility(View.VISIBLE);
                        tvPercent.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        updateButtons();
        return rootView;
    }

    private void updateButtons() {
        String text = etSub.getText().toString();
        if (!tipSelected || text.equals("$0.0")) {
            calcBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.silver));
            addBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.silver));
            calcBtn.setEnabled(false);
            addBtn.setEnabled(false);
            return;
        }
        calcBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.design_default_color_primary_dark));
        addBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.addGreen));
        calcBtn.setEnabled(true);
        addBtn.setEnabled(true);
    }

    private void updateTotalView() {
        String totalString = String.format("$%.2f", total);
        tvTotal.setText(totalString);
        tvTotal.setVisibility(View.VISIBLE);
        receiptImage.setVisibility(View.INVISIBLE);
    }

    private void calculate() {
        tipAmount = subtotal * tipPercent;
        total = subtotal + tipAmount;
        hideKeyboard(getActivity());
    }

    private void showAddDialog() {
        Context fragContext = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(fragContext);
        View view = layoutInflater.inflate(R.layout.add_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(fragContext);
        alertDialogBuilderUserInput.setView(view);

        final TextView tvSubtotal = (TextView) view.findViewById(R.id.tvSubtotal);
        String subString = String.format("$%.2f", subtotal);
        tvSubtotal.setText(subString);
        final TextView tvTip = (TextView) view.findViewById(R.id.tvTip10);
        String tipString = ((tipPercent*100) + "%");
        tvTip.setText(tipString);
        final TextView tvPlace = (TextView) view.findViewById(R.id.tvPlace);
        if (location.equals("")) {
            tvPlace.setText("Not Located");
        } else {
            tvPlace.setText(location);
        }
        final TextView tvDialogTotal = (TextView) view.findViewById(R.id.tvTotal10);
        String totalString = String.format("$%.2f", total);
        tvDialogTotal.setText(totalString);

        tv_date = (TextView) view.findViewById(R.id.tv_date);
        Date time = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM. dd, yyyy - h:mma");

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        String date =simpleDateFormat.format(time);
        tv_date.setText(date);


        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton( "confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        ReceiptRecord rec = new ReceiptRecord(subtotal, total, tipPercent, tipAmount, location, locLatLng, tv_date.getText().toString());
                        databaseReference.child(accountID).push().setValue(rec);
                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}