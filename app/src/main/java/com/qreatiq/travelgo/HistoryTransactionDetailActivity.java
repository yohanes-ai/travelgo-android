package com.qreatiq.travelgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qreatiq.travelgo.adapters.HistoryTransactionAdapter;
import com.qreatiq.travelgo.adapters.HistoryTransactionDetailAdapter;
import com.qreatiq.travelgo.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryTransactionDetailActivity extends AppCompatActivity {

    RecyclerView list;
    HistoryTransactionDetailAdapter adapter;
    ArrayList<JSONObject> array=new ArrayList<>();
    RequestQueue queue;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView tanggal,lokasi;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction_detail);

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

        queue = Volley.newRequestQueue(this);
        tanggal = (TextView) findViewById(R.id.tanggal);
        lokasi = (TextView) findViewById(R.id.lokasi);
        list=(RecyclerView) findViewById(R.id.list);
        adapter=new HistoryTransactionDetailAdapter(array);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        list.setAdapter(adapter);

        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        editor = prefs.edit();
        bundle = getIntent().getExtras();

        getData();
    }

    private void getData(){
        String url = Constant.Companion.getC_URL()+"getInvoiceDetail.php?id="+bundle.getString("id","");

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data=response.getJSONObject("data");
                    lokasi.setText(data.getString("location"));
                    tanggal.setText(data.getString("tanggal"));
                    for(int x=0;x<data.getJSONArray("detail").length();x++) {
                        array.add(data.getJSONArray("detail").getJSONObject(x));
                    }
                    Log.d("data",array.toString());
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
