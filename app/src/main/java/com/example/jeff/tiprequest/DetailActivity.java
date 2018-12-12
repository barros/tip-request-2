package com.example.jeff.tiprequest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;
    private LatLng latLng;
    private double lat1,lon1;
    double subtotal;
    double total;
    double tipPercent;
    double tipAmount;
    String location;
    String time;
    ReceiptRecord receipt;
    TextView tvSubtotal;
    TextView tvTip;
    TextView tvTotal;
    TextView tvPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.lightBlue2, getTheme()));
        }
        setContentView(R.layout.detail_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        tvSubtotal = (TextView) findViewById(R.id.tvSubtotal);
        tvTip = (TextView) findViewById(R.id.tvTip);
        tvTotal = (TextView) findViewById(R.id.tvTip10);
        tvPlace = (TextView) findViewById(R.id.tvPlace);
        Intent arts = getIntent();
        Bundle bundle = arts.getExtras();

        subtotal = bundle.getDouble("subtotal");
        total = bundle.getDouble("total");
        tipPercent = bundle.getDouble("tipPercent");
        tipAmount = bundle.getDouble("tipAmount");
        lat1 = bundle.getDouble("lat");
        lon1 = bundle.getDouble("lon");
        location = bundle.getString("location");
        time = bundle.getString("time");

        String subString = String.format("$%.2f", subtotal);
        tvSubtotal.setText(subString);

        String totalString = String.format("$%.2f", total);
        tvTotal.setText(totalString);

        String tipString = ((tipPercent*100) + "%");
        tvTip.setText(tipString);

        if (!location.equals("")) {
            tvPlace.setText(location);
        }
        System.out.println("LON AND LAT : " + lon1 + " & " + lat1);
        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.the_map);
        mf.getMapAsync(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnMapLoadedCallback(this);
        UiSettings mapSettings;
        mapSettings = map.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
    }
    @Override
    public void onMapLoaded() {
        getMoreInfo();
        if (lat1!=0.0 && lon1!=0.0){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    public void getMoreInfo() {
        if (lat1!=0.0 && lon1!=0.0) {
            latLng = new LatLng(lat1, lon1);
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14), 3000, null);
        }
    }
}
