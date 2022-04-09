package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageSlider imageSlider;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private RecyclerView recyclerViewListitem;
    private ListItemAdapter listitemAdapter;

    private ArrayList<ListItemModel> listitem = new ArrayList<>();

    private RecyclerView recyclerView;
    private StaticRvAdapter staticRvAdapter;

    public MainFragment() {

    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //SlideImage
        imageSlider = (ImageSlider) view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> images = new ArrayList<>();
        images.add(new SlideModel(R.drawable.picslide_1, null));
        images.add(new SlideModel(R.drawable.picslide_2, null));
        images.add(new SlideModel(R.drawable.picslide_3, null));
        images.add(new SlideModel(R.drawable.picslide_4, null));
        imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);

        //Categorys
        ArrayList<StatisRvModel> item = new ArrayList<>();
        item.add(new StatisRvModel(R.drawable.cat, "อาหารแมว", "Cat"));
        item.add(new StatisRvModel(R.drawable.dog, "อาหารหมา", "Dog"));
        item.add(new StatisRvModel(R.drawable.bird, "อาหารนก", "Bird"));
        item.add(new StatisRvModel(R.drawable.fish, "อาหารปลา", "Fish"));
        item.add(new StatisRvModel(R.drawable.hamster, "อาหารหนูแฮมเตอร์", "Hamster"));
        item.add(new StatisRvModel(R.drawable.hedgehog, "อาหารเม่น", "Hedgehog"));
        item.add(new StatisRvModel(R.drawable.sugar, "อาหารชูก้าร์ไกรเดอร์", "Sugar"));

        staticRvAdapter = new StaticRvAdapter(item);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_listitemcategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(staticRvAdapter);

        //itemlist
        jsonParse("http://154.202.2.5:4990/api/items/getitem");
        return view;
    }

    public void jsonParse(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        listitem.clear();

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
                                Log.d(TAG,"gson "+ dataitem.getItemname());
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
                        Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.hide();
                    }
                });  // Request

        mQueue = Volley.newRequestQueue(getActivity());
        jsRequest.setTag(REQUEST_TAG);
        mQueue.add(jsRequest);
    }

    public void displayListview(){
        recyclerViewListitem = (RecyclerView) getView().findViewById(R.id.rv_listitem);
        listitemAdapter = new ListItemAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}