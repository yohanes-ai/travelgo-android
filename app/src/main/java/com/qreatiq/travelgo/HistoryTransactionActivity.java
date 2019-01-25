package com.qreatiq.travelgo;

import android.content.Context;
import android.content.Intent;
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
import com.qreatiq.travelgo.adapters.CreateTourAdapter;
import com.qreatiq.travelgo.adapters.HistoryTransactionAdapter;
import com.qreatiq.travelgo.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryTransactionActivity extends AppCompatActivity {

    RecyclerView list;
    HistoryTransactionAdapter adapter;
    ArrayList<JSONObject> array=new ArrayList<>();
    RequestQueue queue;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView no_history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction);

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
        no_history = (TextView) findViewById(R.id.no_history);

        list=(RecyclerView) findViewById(R.id.list);
        adapter=new HistoryTransactionAdapter(array);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        list.setAdapter(adapter);

        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        editor = prefs.edit();

        adapter.setOnItemClickListener(new CreateTourAdapter.ClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    Intent in=new Intent(HistoryTransactionActivity.this,HistoryTransactionDetailActivity.class);
                    in.putExtra("id",array.get(position).getString("id"));
                    startActivity(in);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getData();
    }

    private void getData(){
        String url = Constant.Companion.getC_URL()+"getInvoice.php?id="+prefs.getString("user_id","");
        Log.d("data",url);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getJSONArray("data").length()>0) {
                        for (int x = 0; x < response.getJSONArray("data").length(); x++)
                            array.add(response.getJSONArray("data").getJSONObject(x));
                        adapter.notifyDataSetChanged();
                        no_history.setVisibility(View.GONE);
                    }
                    else{
                        no_history.setVisibility(View.VISIBLE);
                    }


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
