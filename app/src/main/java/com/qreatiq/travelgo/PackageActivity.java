package com.qreatiq.travelgo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qreatiq.travelgo.adapters.FindTourAdapter;
import com.qreatiq.travelgo.adapters.PackageAdapter;
import com.qreatiq.travelgo.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PackageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PackageAdapter adapter;
    ArrayList<JSONObject> array=new ArrayList<>();

    RequestQueue queue;

    SharedPreferences user;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list);
        adapter=new PackageAdapter(array);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        user = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userID = user.getString("user_id", "Not Found");

        queue = Volley.newRequestQueue(this);

        getData();

        FloatingActionButton fab = findViewById(R.id.addPackage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PackageActivity.this, CreateTourActivity.class));
            }
        });

        adapter.setOnItemClickListener(new PackageAdapter.ClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    Intent intent=new Intent(PackageActivity.this,CreateTourActivity.class);
                    intent.putExtra("id",array.get(position).getString("id"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void getData(){
        String url = Constant.Companion.getC_URL()+"getPackageUser.php?id="+userID;
//        Log.d("data",url);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    Log.d("packageTour", response.toString());
                    for(int x=0;x<response.getJSONArray("package").length();x++) {
                        JSONObject package1=response.getJSONArray("package").getJSONObject(x);
//                        Log.d("packageTour", package1.toString());
                        NumberFormat formatter = new DecimalFormat("#,###");

                        JSONObject json=new JSONObject();
                        json.put("location",package1.getString("location"));
                        json.put("date",package1.getString("start_date")+" - "+package1.getString("end_date"));
                        json.put("id",package1.getString("id"));
                        array.add(json);
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
