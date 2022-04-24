package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private String mParam1;
    private String mParam2;

    private View view;

    SharedPreferences sp;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        view = inflater.inflate(R.layout.fragment_login, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sp = getContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        ImageButton backbutton = view.findViewById(R.id.imagebuttonback);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent itnHome = new Intent(getActivity(), MainActivity.class);
                startActivity(itnHome);
            }
        });

        ImageButton loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Loading..");
                pDialog.show();

                EditText username = view.findViewById(R.id.storageItemName);
                EditText password = view.findViewById(R.id.InputPassword);
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                String url = "http://192.168.0.105:4990/api/users/login";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, response);

                                String type = null;
                                String userToken = null;
                                String userID = null;

                                try {
                                    JSONObject jsonobject = new JSONObject(response);
                                    type = jsonobject.getString("type");
                                    userToken = jsonobject.getString("userToken");
                                    userID = jsonobject.getString("_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                pDialog.hide();
                                if(type.matches("wrong_username")){
                                    Toast.makeText(getActivity(), "ไม่พบผู้ใช้หรืออีเมลดังกล่าว", Toast.LENGTH_SHORT).show();
                                } else if(type.matches("wrong_password")){
                                    Toast.makeText(getActivity(), "รหัสผ่านของคุณไม่ถูกต้อง!", Toast.LENGTH_SHORT).show();
                                } else if(type.matches("success")){
                                    Toast.makeText(getActivity(), "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("Token", userToken);
                                    editor.putString("userID", userID);
                                    editor.commit();
                                    Log.i(TAG, userToken);
                                    Log.i(TAG, "userID: " + userID);

                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.navHostFragment, MainFragment.newInstance(null, null));
                                    transaction.commit();
                                }
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Error handling
                                Log.i(TAG, "onErrorResponse(): "+
                                        error.getMessage());

                                pDialog.hide();
                            }
                        })
                {
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", usernameText);
                        params.put("password", passwordText);
                        return params;
                    }
                };

                mQueue = Volley.newRequestQueue(getActivity());
                mQueue.add(stringRequest);
            }
        });

        ImageButton registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, RegisterFragment.newInstance(null, null));
                transaction.commit();
            }
        });
    }
}