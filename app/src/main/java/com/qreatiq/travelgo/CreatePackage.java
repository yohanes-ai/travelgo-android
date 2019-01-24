package com.qreatiq.travelgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatePackage extends AppCompatActivity {

    Spinner spinner;
    RequestQueue queue;
    private ArrayList<String> arrayList;
    String url;
    String locationSelected;
    private JSONArray result;
    Button submitBtn;
    EditText dateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package);

        spinner = (Spinner)findViewById(R.id.cityName);
        arrayList = new ArrayList<String>();
        submitBtn = (Button)findViewById(R.id.submitBtn);

        queue = Volley.newRequestQueue(this);

        getLocationData();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationSelected = arrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getLocationData(){
        url = "https://3gomedia.com/travel-go/api/getLocation.php";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    for(int x=0;x<response.getJSONArray("location").length();x++) {
                        JSONObject loc=response.getJSONArray("location").getJSONObject(x);
                        arrayList.add(loc.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                spinner.setAdapter(new ArrayAdapter<String>(CreatePackage.this, R.layout.support_simple_spinner_dropdown_item, arrayList));
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