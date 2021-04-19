package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.adapters.ProductImagesDataAdapters;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.ProductImages;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCourse extends AppCompatActivity {


    EditText titleET, descET,expectedHoursET, perHourET;
    Spinner categorySpinner;
    SwitchMaterial courseStatusSwitch;

    ArrayList<ProductImages> productImages = new ArrayList<>();


    public static final int GALLERY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        titleET = findViewById(R.id.title);
        descET = findViewById(R.id.desc);
        categorySpinner = findViewById(R.id.spinnerCategory);
        expectedHoursET = findViewById(R.id.hours);
        perHourET = findViewById(R.id.cost);
        courseStatusSwitch = findViewById(R.id.statusswitch);


        loadImagesView();

        findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addCourse();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    void loadImagesView() {
        //https://javapapers.com/android/android-image-gallery-example-app-using-glide-library/

        findViewById(R.id.addImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewImgs);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 30);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ArrayList<ProductImages> imageUrlList = productImages;
        ProductImagesDataAdapters dataAdapter = new ProductImagesDataAdapters(AddCourse.this, imageUrlList);
        recyclerView.setAdapter(dataAdapter);
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    uploadImageFile(getPath(selectedImage));
                    break;
            }

    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public boolean uploadImageFile(String imagePath) {

        byte[] audioBytes = getByteArr(imagePath);
        final String imageString = Base64.encodeToString(audioBytes, Base64.DEFAULT);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Uploading the Image...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://stageed.com/fileUpload.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    pDialog.hide();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(AddCourse.this, "Uploaded Successful", Toast.LENGTH_LONG).show();

                        productImages.add(new ProductImages(-1,jsonObject.getString("path")));
                        loadImagesView();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.hide();
                Toast.makeText(getApplicationContext(), "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("uploadedFile", imageString);
                parameters.put("type", "jpg");
                return parameters;
            }
        };

        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        return true;
    }

    byte[] getByteArr(String path) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public void removeImageFromList(String removeKey) {
        for(int i=0;i<productImages.size();i++)
        {
            if(productImages.get(i).url.equals(removeKey))
            {
                productImages.remove(i);
            }
        }
        loadImagesView();
    }


    private void addCourse() throws JSONException {
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/createCourse";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Uploading Course...");
        pDialog.show();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", titleET.getText().toString());
        jsonObject.put("description", descET.getText().toString());
        jsonObject.put("category",categorySpinner.getSelectedItem().toString());
        jsonObject.put("hours_expected", Integer.parseInt(expectedHoursET.getText().toString().equals("")?"0":expectedHoursET.getText().toString()));
        jsonObject.put("students_capacity",1);
        jsonObject.put("charge_per_hour",Integer.parseInt(perHourET.getText().toString().equals("")?"0":perHourET.getText().toString()));
        jsonObject.put("status", courseStatusSwitch.isEnabled()?"active":"hidden");
        jsonObject.put("tutor_id", new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id"));
        jsonObject.put("video_link", ((EditText)findViewById(R.id.video_link)).getText().toString());

        JSONArray jsonArray=new JSONArray();

        for (ProductImages pi:productImages)
        {
            jsonArray.put(new JSONObject().put("imageLink",pi.url));
        }
        jsonObject.put("images",jsonArray);

        JSONArray jsonArray1 = new JSONArray();
        String[] days = {"monday", "tuesday", "wednesday", "thursday","friday","saturday","sunday"};
        for (String day: days){
            JSONObject ob = new JSONObject();
            ob.put("day",day);
            switch (day)
            {
                case "monday":
                    ob.put("time", ((EditText)findViewById(R.id.mon1)).getText().toString() + "-" + ((EditText)findViewById(R.id.mon2)).getText().toString());
                    break;
                case "tuesday":
                    ob.put("time", ((EditText)findViewById(R.id.tue1)).getText().toString() + "-" + ((EditText)findViewById(R.id.tue2)).getText().toString());
                    break;
                case "wednesday":
                    ob.put("time", ((EditText)findViewById(R.id.wed1)).getText().toString() + "-" + ((EditText)findViewById(R.id.wed2)).getText().toString());
                    break;
                case "thursday":
                    ob.put("time", ((EditText)findViewById(R.id.thu1)).getText().toString() + "-" + ((EditText)findViewById(R.id.thu1)).getText().toString());
                    break;
                case "friday":
                    ob.put("time", ((EditText)findViewById(R.id.fri1)).getText().toString() + "-" + ((EditText)findViewById(R.id.fri2)).getText().toString());
                    break;
                case "saturday":
                    ob.put("time", ((EditText)findViewById(R.id.sat1)).getText().toString() + "-" + ((EditText)findViewById(R.id.sat2)).getText().toString());
                    break;
                case "sunday":
                    ob.put("time", ((EditText)findViewById(R.id.sun1)).getText().toString() + "-" + ((EditText)findViewById(R.id.sun1)).getText().toString());
                    break;
            }
            jsonArray1.put(ob);
        }
        jsonObject.put("time_slot",jsonArray1);

        final String requestBody = jsonObject.toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    pDialog.hide();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(AddCourse.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(AddCourse.this, "Error Occurred", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.hide();
                Toast.makeText(getApplicationContext(), "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to send
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }


        };
        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

}