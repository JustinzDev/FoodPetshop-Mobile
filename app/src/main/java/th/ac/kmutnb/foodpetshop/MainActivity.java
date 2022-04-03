package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.squareup.picasso.Picasso;

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

    private RecyclerView recyclerViewListitem;
    private ListItemAdapter listitemAdapter;

    private ArrayList<ListItemModel> listitem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<StatisRvModel> item = new ArrayList<>();
        item.add(new StatisRvModel(R.drawable.cat, "อาหารแมว"));
        item.add(new StatisRvModel(R.drawable.dog, "อาหารหมา"));
        item.add(new StatisRvModel(R.drawable.bird, "อาหารนก"));
        item.add(new StatisRvModel(R.drawable.fish, "อาหารปลา"));
        item.add(new StatisRvModel(R.drawable.hamster, "อาหารหนูแฮมเตอร์"));
        item.add(new StatisRvModel(R.drawable.hedgehog, "อาหารเม่น"));
        item.add(new StatisRvModel(R.drawable.sugar, "อาหารชูก้าร์ไกรเดอร์"));

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

        String url = "http://192.168.0.105:4000/api/items/getitem";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading..");
        pDialog.show();

        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Gson gson = new Gson();

                        JSONObject jsObj;   // = null;
                        for (int i=0; i < response.length(); i++ ) {
                            try {
                                jsObj = response.getJSONObject(i);
                                ListItemModel dataitem = gson.fromJson(String.valueOf(jsObj), ListItemModel.class);
                                listitem.add(dataitem);
//                                Log.d(TAG,"gson "+ dataitem.getItemname());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (listitem.size() > 0){
                            displayListview();
                        }

                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.toString());
                        Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        pDialog.hide();
                    }
                });  // Request

        mQueue = Volley.newRequestQueue(this);
        jsRequest.setTag(REQUEST_TAG);
        mQueue.add(jsRequest);
    }

    public void displayListview(){
        recyclerViewListitem = findViewById(R.id.rv_listitem);
        listitemAdapter = new ListItemAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}