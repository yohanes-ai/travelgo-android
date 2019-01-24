package com.qreatiq.travelgo;

import android.app.AlertDialog;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qreatiq.travelgo.objects.NumberTextWatcher;
import com.qreatiq.travelgo.utils.Constant;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class CreateTourPackageActivity extends AppCompatActivity {

    RequestQueue queue;
    BottomSheetDialog bottomSheetDialog;
    Uri filePath;
    ImageView imageView;

    EditText name,description,price;
    String image="",getImageUrl;

    JSONObject json = new JSONObject();
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    LinearLayout deleteContainer;
    Bundle bundle;

    boolean flag=false;

    View.OnFocusChangeListener listener=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus)
                hideKeyboard(v);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour_package);

        imageView = (ImageView) findViewById(R.id.image);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        price = (EditText) findViewById(R.id.price);
        deleteContainer = (LinearLayout) findViewById(R.id.deleteContainer);

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

        final NumberFormat formatter = new DecimalFormat("#,###");
        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        editor = prefs.edit();

        bundle=getIntent().getExtras();
        if(bundle!=null){
            setTitle("Edit Tour Package");
            deleteContainer.setVisibility(View.VISIBLE);
            try {
                json=new JSONObject(prefs.getString("tour_package","{}"));
                Log.d("data",json.toString());
                name.setText(json.getString("name"));
                description.setText(json.getString("description"));
                price.setText(json.getString("price"));
                if(!json.getString("image").equals("")) {
                    if(json.getBoolean("link"))
                        Picasso.get()
                                .load(json.getString("image"))
                                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                                .into(imageView);
                    else
                        imageView.setImageBitmap(StringToBitMap(json.getString("image")));
                    imageView.setVisibility(View.VISIBLE);
                    editor.putString("image", json.getString("image"));
                    editor.commit();
                }
                else{
                    json.put("link", false);
                    json.put("image", "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        price.addTextChangedListener(new NumberTextWatcher(price, "#,###"));

        CoordinatorLayout layout=(CoordinatorLayout) findViewById(R.id.layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            try {
                CoordinatorLayout layout=(CoordinatorLayout) findViewById(R.id.layout);
                if(name.getText().toString().isEmpty()) {
                    Snackbar snackbar=Snackbar.make(layout,"Name is empty",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else if(price.getText().toString().isEmpty()) {
                    Snackbar snackbar=Snackbar.make(layout,"End Date is empty",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else {
                    json.put("name", name.getText().toString());
                    json.put("description", description.getText().toString());
                    json.put("price", price.getText().toString().replace(".00","").replace(",",""));
                    json.put("image", "");

                    if (!image.equals("")) {
                        editor.putString("image", image);
                        editor.commit();
                    } else
                        json.put("link", false);
                    flag = true;

                    onBackPressed();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(flag) {
            Intent intent = new Intent();
            intent.putExtra("tour_package", json.toString());

            setResult(RESULT_OK, intent);
            finish();
        }
        else
            super.onBackPressed();
    }

    public void deletePackage(View v){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Are You Sure?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.putExtra("delete", true);
                        setResult(RESULT_OK, intent);
                        finish();
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

    public void addMedia(View v){
        bottomSheetDialog=new BottomSheetDialog(this);
        View view = View.inflate(this, R.layout.list_attach_item, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
//        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),1);
    }

    public void camera(View v){
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
    }

    public void gallery(View v){
        Intent in =new Intent(Intent.ACTION_PICK);
        in.setType("image/*");
        startActivityForResult(in,0);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,80, baos);
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if(resultCode==RESULT_OK){
            if(requestCode==1){
//                if (Build.VERSION.SDK_INT > 22)
//                    getImageUrl = ImagePath.getPath(this, filePath);
//                else
//                    //else we will get path directly
//                    getImageUrl = filePath.getPath();
//
//                Log.d("data",data.getExtras().toString());
                bitmap=(Bitmap) data.getExtras().get("data");

                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);

                image=BitMapToString(bitmap);
                try {
                    json.put("link",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode==0){

                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    image=BitMapToString(bitmap);
                    json.put("link",false);
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            bottomSheetDialog.hide();
        }
    }
}
