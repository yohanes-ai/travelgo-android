package com.qreatiq.travelgo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qreatiq.travelgo.adapters.CreateTourAdapter;
import com.qreatiq.travelgo.adapters.CreateTourPackageAdapter;
import com.qreatiq.travelgo.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTourActivity extends AppCompatActivity {

    ArrayList<String> daysArray=new ArrayList<>();
    ArrayList<String> locationArray=new ArrayList<>();

    EditText start,end;
    Calendar calendarStart=Calendar.getInstance(),calendarEnd=Calendar.getInstance();

    Spinner many_days,location;
    RequestQueue queue;
    BottomSheetDialog bottomSheetDialog;
    Uri filePath;
    ImageView imageView;

    RecyclerView list;
    CreateTourAdapter adapter;
    ArrayList<JSONObject> array=new ArrayList<>();

    RecyclerView listPackage;
    CreateTourPackageAdapter adapter1;
    ArrayList<JSONObject> array1=new ArrayList<>();

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Bundle bundle;

    int CREATE_TOUR=3,EDIT_TOUR=4,pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

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

        bundle=getIntent().getExtras();
        start=(EditText) findViewById(R.id.start_date);
        end=(EditText) findViewById(R.id.end_date);
        location=(Spinner) findViewById(R.id.location);
        queue = Volley.newRequestQueue(this);
        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if(!bundle.isEmpty())
            setTitle("Edit Package");

        getLocation();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateTourActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendarStart.set(Calendar.YEAR, year);
                                calendarStart.set(Calendar.MONTH, month);
                                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                showDate(start,year, month+1, dayOfMonth);
                            }
                        },
                        calendarStart.get(Calendar.YEAR),
                        calendarStart.get(Calendar.MONTH),
                        calendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateTourActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendarEnd.set(Calendar.YEAR, year);
                                calendarEnd.set(Calendar.MONTH, month);
                                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                showDate(end,year, month+1, dayOfMonth);
                            }
                        },
                        calendarEnd.get(Calendar.YEAR),
                        calendarEnd.get(Calendar.MONTH),
                        calendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        list=(RecyclerView) findViewById(R.id.list);
        adapter=new CreateTourAdapter(array);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new CreateTourAdapter.ClickListener() {
            @Override
            public void onItemClick(int position) {
                array.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        listPackage=(RecyclerView) findViewById(R.id.listPackage);
        adapter1=new CreateTourPackageAdapter(array1);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        listPackage.setLayoutManager(mLayoutManager1);
        listPackage.setItemAnimator(new DefaultItemAnimator());
        listPackage.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        listPackage.setAdapter(adapter1);

        adapter1.setOnItemClickListener(new CreateTourPackageAdapter.ClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onLongItemClick(int position) {
                pos=position;
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == 1) {
            Intent in=new Intent(this,CreateTourPackageActivity.class);
            editor.putString("tour_package",array1.get(pos).toString());
            editor.commit();
            in.putExtra("id",pos);
            startActivityForResult(in,EDIT_TOUR);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.submit) {
            saveData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logLargeString(String str) {
        if(str.length() > 3000) {
            Log.i("data", str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i("data", str); // continuation
        }
    }

    public void saveData(){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Saving");
        dialog.show();

        JSONObject json = new JSONObject();
        try {
            JSONArray arr=new JSONArray();
            for(int x=0;x<array.size();x++)
                arr.put(x,array.get(x));

            JSONArray arr1=new JSONArray();
            for(int x=0;x<array1.size();x++)
                arr1.put(x,array1.get(x));

            json.put("start_date",start.getText().toString());
            json.put("end_date",end.getText().toString());
            json.put("location",locationArray.get(location.getSelectedItemPosition()));
            json.put("photo",arr.toString());
            json.put("tour_package",arr1.toString());
            json.put("user_id",prefs.getString("user_id",""));
            if(!bundle.isEmpty())
                json.put("id",bundle.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        logLargeString(json.toString());


        String url = Constant.Companion.getC_URL()+"savePackage.php";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, json
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CoordinatorLayout layout=(CoordinatorLayout) findViewById(R.id.layout);
                String message="";
                if (error instanceof NetworkError) {
                    message="Network Error";
                }
                else if (error instanceof ServerError) {
                    message="Server Detect Error";
                }
                else if (error instanceof AuthFailureError) {
                    message="Authentication Detect Error";
                }
                else if (error instanceof ParseError) {
                    message="Parse Detect Error";
                }
                else if (error instanceof NoConnectionError) {
                    message="Connection Missing";
                }
                else if (error instanceof TimeoutError) {
                    message="Server Detect Timeout Reached";
                }
                Snackbar snackbar=Snackbar.make(layout,message,Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public void createTour(View v){
        startActivityForResult(new Intent(this,CreateTourPackageActivity.class),CREATE_TOUR);
    }

    private void showDate(EditText v,int year, int month, int day) {
        v.setText(day+"/"+month+"/"+year);
    }

    private void getData(String id){
        String url = Constant.Companion.getC_URL()+"getPackageSpecific.php?id="+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    for(int x=0;x<response.getJSONArray("data").length();x++) {
                        JSONObject data=response.getJSONArray("data").getJSONObject(x);
                        start.setText(data.getString("date_start"));
                        end.setText(data.getString("date_end"));
                        location.setSelection(locationArray.indexOf(data.getString("location")));

                        for(int y=0;y<data.getJSONArray("photo").length();y++){
                            JSONObject data1=data.getJSONArray("photo").getJSONObject(x);
                            JSONObject json=new JSONObject();
                            try {
                                json.put("data",Constant.Companion.getC_URL_IMAGES()+"package/"+data1.getString("urlPhoto"));
                                json.put("id",data1.getString("id"));
                                json.put("link",true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            array.add(json);
                            adapter.notifyDataSetChanged();
                        }

                        for(int y=0;y<data.getJSONArray("detail").length();y++){
                            JSONObject data1=data.getJSONArray("detail").getJSONObject(y);
                            JSONObject json = new JSONObject();
                            json.put("name",data1.getString("name"));
                            json.put("description",data1.getString("description"));
                            json.put("price",data1.getString("price"));
                            json.put("image",Constant.Companion.getC_URL_IMAGES()+"tour_pack/"+data1.getString("url_photo"));
                            json.put("link",true);
                            json.put("id",data1.getString("id"));
                            Log.d("data",json.toString());

                            array1.add(json);
                            adapter1.notifyDataSetChanged();
                        }
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

    public void deleteData(){
        JSONObject json = new JSONObject();
        try {
            json.put("id",bundle.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = Constant.Companion.getC_URL()+"deletePackage.php";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, json
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CoordinatorLayout layout=(CoordinatorLayout) findViewById(R.id.layout);
                String message="";
                if (error instanceof NetworkError) {
                    message="Network Error";
                }
                else if (error instanceof ServerError) {
                    message="Server Detect Error";
                }
                else if (error instanceof AuthFailureError) {
                    message="Authentication Detect Error";
                }
                else if (error instanceof ParseError) {
                    message="Parse Detect Error";
                }
                else if (error instanceof NoConnectionError) {
                    message="Connection Missing";
                }
                else if (error instanceof TimeoutError) {
                    message="Server Detect Timeout Reached";
                }
                Snackbar snackbar=Snackbar.make(layout,message,Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    private void getLocation(){
        String url = Constant.Companion.getC_URL()+"getLocation.php";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    for(int x=0;x<response.getJSONArray("location").length();x++) {
                        JSONObject loc=response.getJSONArray("location").getJSONObject(x);
                        locationArray.add(loc.getString("name"));
                    }
                    ArrayAdapter adapter=new ArrayAdapter(CreateTourActivity.this,R.layout.support_simple_spinner_dropdown_item,locationArray);
                    location.setAdapter(adapter);


                    if(!bundle.isEmpty()){
                        getData(bundle.getString("id"));
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

    public void addMedia(View v){
        bottomSheetDialog=new BottomSheetDialog(this);
        View view = View.inflate(this, R.layout.list_attach_item, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
//        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),1);
    }

    public void deletePackage(View v){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Are You Sure?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteData();
                        dialog.dismiss();
                        // Do stuff if user accepts
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Do stuff when user neglects.
                    }
                })
                .create();
        dialog.show();
    }

    public void camera(View v){
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),1);
    }

    public void gallery(View v){
        Intent in =new Intent(Intent.ACTION_PICK);
        in.setType("image/*");
        startActivityForResult(in,0);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,70, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                filePath = data.getData();
                bitmap=(Bitmap) data.getExtras().get("data");

                JSONObject json=new JSONObject();
                try {
                    json.put("data",BitMapToString(bitmap));
                    json.put("link",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                array.add(json);
                adapter.notifyDataSetChanged();

                bottomSheetDialog.hide();
            }
            else if(requestCode==0){

                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                    JSONObject json=new JSONObject();
                    try {
                        json.put("data",BitMapToString(bitmap));
                        json.put("link",false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    array.add(json);
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
                bottomSheetDialog.hide();
            }
            else if(requestCode==CREATE_TOUR){
                try {
                    JSONObject json = new JSONObject(data.getStringExtra("tour_package"));
                    json.put("image",prefs.getString("image",""));
                    array1.add(json);
                    adapter1.notifyDataSetChanged();

                    editor.remove("image");
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(requestCode==EDIT_TOUR){
                try {
                    JSONObject json = new JSONObject(data.getStringExtra("tour_package"));
                    json.put("image",prefs.getString("image",""));
                    array1.set(pos,json);
                    adapter1.notifyItemChanged(pos);

                    editor.remove("image");
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
