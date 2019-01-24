package com.qreatiq.travelgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour_package);

        imageView = (ImageView) findViewById(R.id.image);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        price = (EditText) findViewById(R.id.price);

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

        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        editor = prefs.edit();

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            setTitle("Edit Tour Package");
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
                json.put("name",name.getText().toString());
                json.put("description",description.getText().toString());
                json.put("price",price.getText().toString());
                json.put("image","");

                if(!image.equals("")) {
                    editor.putString("image", image);
                    editor.commit();
                }
                flag=true;

                onBackPressed();
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
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
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
