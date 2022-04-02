package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageSlider imageSlider;
    ProgressDialog pDialog;
    private static final String TAG = "my_app";
    public static final String REQUEST_TAG = "myrequest";
    private RequestQueue mQueue;

    private RecyclerView recyclerView;
    private StaticRvAdapter staticRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<StatisRvModel> item = new ArrayList<>();
        item.add(new StatisRvModel(R.drawable.rvicon1, "อาหารแมว"));
        item.add(new StatisRvModel(R.drawable.rvicon1, "อาหารหมา"));
        item.add(new StatisRvModel(R.drawable.rvicon1, "อาหารนก"));
        item.add(new StatisRvModel(R.drawable.rvicon1, "อาหารปลา"));
        item.add(new StatisRvModel(R.drawable.rvicon1, "อาหารอื่นๆ"));

        recyclerView = findViewById(R.id.rv_1);
        staticRvAdapter = new StaticRvAdapter(item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(staticRvAdapter);



        //SlideImage
        imageSlider = findViewById(R.id.image_slider);

        ArrayList<SlideModel> images = new ArrayList<>();
        images.add(new SlideModel(R.drawable.imgslide1, null));
        images.add(new SlideModel(R.drawable.imgslide2, null));
        images.add(new SlideModel(R.drawable.imgslide3, null));

        imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);

//        String APIURL = "http://192.168.0.105:4000/api/items/getitem";
//
//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//
//        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, APIURL, null,
//            new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray response) {
//                    JSONObject jsObj;
//                    for (int i=0; i < response.length(); i++ ) {
//                        try {
//                            jsObj = response.getJSONObject(i);
//                            String id = jsObj.getString("_id");
//                            String itemname = jsObj.getString("itemname");
//                            String itemdetail = jsObj.getString("itemdetail");
//                            int itemprice = jsObj.getInt("itemprice");
//                            int itemamount = jsObj.getInt("itemamount");
//                            int itempopular = jsObj.getInt("itempopular");
//                            String itemimg = jsObj.getString("itemimg");
//                            Log.d(TAG,id + " , " + itemname + " , " + itemdetail + " , " + itemprice + " , " + itemamount + " , " + itempopular + " , " + itemimg);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    pDialog.hide();
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // Error handling
//                    Log.d(TAG, "onErrorResponse(): "+ error.getMessage());
//                    pDialog.hide();
//                }
//            });  // stringRequest
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(jsRequest);
    }
}