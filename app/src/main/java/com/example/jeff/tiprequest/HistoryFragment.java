package com.example.jeff.tiprequest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    RecyclerView rv;
    private LinearLayoutManager linearLayoutManager;
    private RVAdapter adapter;
    List<ReceiptRecord> items;
    private DatabaseReference databaseReference;
    String accountID = UserInfo.getAccountID();
    ImageView ivEdit;
    boolean editMode = false;
    boolean updatedRecords = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);
        items = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        ivEdit = rootView.findViewById(R.id.ivEdit);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(editMode);
                if (editMode) {
                    ivEdit.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                } else {
                    ivEdit.setColorFilter(ContextCompat.getColor(getContext(), R.color.silver));
                }
                editMode = !editMode;
                adapter = new RVAdapter(items, editMode);
                rv.setAdapter(adapter);
            }
        });
        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(accountID)) {
                    items.clear();
                    refreshReceipts(dataSnapshot);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                items.clear();
                refreshReceipts(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                items.clear();
                refreshReceipts(dataSnapshot);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return rootView;
    }

    private void refreshReceipts(DataSnapshot dataSnapshot) {
        for (DataSnapshot item: dataSnapshot.getChildren()) {
            System.out.println(item.toString());
            ReceiptRecord rec = convertToReceipt(item);
            items.add(rec);
        }
        updatedRecords = true;

        adapter = new RVAdapter(items, false);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    private ReceiptRecord convertToReceipt(DataSnapshot item) {
        double sub = Double.parseDouble(item.child("subtotal").getValue().toString());
        double total = Double.parseDouble(item.child("total").getValue().toString());
        double tipPercent = Double.parseDouble(item.child("tipPercent").getValue().toString());
        double tipAmount  = Double.parseDouble(item.child("tipAmount").getValue().toString());
        String time = (String) item.child("time").getValue().toString();
        String location = (String) item.child("location").getValue().toString();
        double lat = 0.0;
        double lon = 0.0;
        for (DataSnapshot val : item.child("locLatLng").getChildren()) {
            if (val.getKey().equals("latitude")) {
                lat = Double.parseDouble(val.getValue().toString());
            } else {
                lon = Double.parseDouble(val.getValue().toString());
            }
        }
        LatLng latLng = new LatLng(lat, lon);
        String key = item.getKey();
        ReceiptRecord rec = new ReceiptRecord(key, sub, total, tipPercent, tipAmount, location, latLng, time);
        return rec;
    }
}
