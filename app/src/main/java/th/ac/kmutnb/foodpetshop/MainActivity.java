package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "my_app";

    public static final String REQUEST_TAG = "myrequest";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.slidemenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.itemcart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                String Token = sp.getString("Token", "");
                authToken("http://154.202.2.5:4990/api/users/auth_token", Token, view);
            }
        });


        NavigationView navigationView = findViewById(R.id.navigationView);
        Menu nav_Menu = navigationView.getMenu();

        //home
        nav_Menu.findItem(R.id.mainFragment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                finish();
                startActivity(getIntent());
                return true;
            }
        });

        //logout
        nav_Menu.findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                SharedPreferences preferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                preferences.edit().remove("Token").commit();
                finish();
                startActivity(getIntent());
                return true;
            }
        });
//        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    public void authToken(String url, String Token, View view){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String userName = null;
                        String userID = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            userID = jsonobject.getString("_id");
                            userName = jsonobject.getString("username");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(userName != null && userID != null){
                            AppCompatActivity activity = (AppCompatActivity) view.getContext();
                            FragmentManager manager = activity.getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.navHostFragment, UserCartFragment.newInstance(userID, null));
                            transaction.commit();
                        } else {
                            SharedPreferences preferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                            preferences.edit().remove("Token").commit();
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
                params.put("token", Token);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(this);
        mQueue.add(stringRequest);
    }
}