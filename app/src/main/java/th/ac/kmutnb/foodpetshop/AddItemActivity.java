package th.ac.kmutnb.foodpetshop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonupload, buttonchoose;
    private int IMG_REQUEST = 21;

    private Bitmap bitmap;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        imageView = findViewById(R.id.itemImage);
        buttonupload = findViewById(R.id.buttonupload);
        buttonchoose = findViewById(R.id.buttonchoose);

        Button backstep = findViewById(R.id.backstep);
        backstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnHome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(itnHome);
                return;
            }
        });

        buttonchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMG_REQUEST);
            }
        });

        buttonupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText itemnametxt = findViewById(R.id.storageItemName);
                EditText itemamountxt = findViewById(R.id.storageItemAmount);
                EditText itemprice = findViewById(R.id.storageItemPrice);
                EditText itemcategory = findViewById(R.id.storageItemCategory);
                EditText itemdetail = findViewById(R.id.storageItemDetail);

                String itemName = itemnametxt.getText().toString();

                String itemAmountEx = itemamountxt.getText().toString();
                int itemAmount = Integer.valueOf(itemAmountEx);

                String itemPriceEx = itemprice.getText().toString();
                double itemPrice = Double.valueOf(itemPriceEx);

                String itemCategory = itemcategory.getText().toString();

                String itemDetail = itemdetail.getText().toString();

                if(!itemName.isEmpty() && !itemAmountEx.isEmpty() && !itemPriceEx.isEmpty() && !itemCategory.isEmpty() && !itemDetail.isEmpty()) {
                    addItemToStorage("http://154.202.2.5:4990/api/items/additemstorage", itemName, itemAmount, itemPrice, itemCategory, itemDetail);
                }
            }
        });
    }

    public void addItemToStorage(String url, String itemName, int itemAmount, double itemPrice, String itemCategory, String itemDetail){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String message = null;
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            message = jsonobject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(message != null){
                            uploadImage("http://154.202.2.5/foodpetshop/upload.php", message, itemCategory);
                            Intent itnHome = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(itnHome);
                            return;
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemname", itemName);
                params.put("itemamount", String.valueOf(itemAmount));
                params.put("itemprice", String.valueOf(itemPrice));
                params.put("itemcategory", itemCategory);
                params.put("itemdetail", itemDetail);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(this);
        mQueue.add(stringRequest);
    }

    public void uploadImage(String url, String itemid, String itemCategory){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        String message = null;
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            message = jsonobject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                String imageData = ImagetoString();
                params.put("image", imageData);
                params.put("category", itemCategory);
                params.put("_id", itemid);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(this);
        mQueue.add(stringRequest);
    }

    public String ImagetoString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 75, byteArrayOutputStream);
        byte[] imgInByte = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imgInByte, Base64.DEFAULT);

        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}