package th.ac.kmutnb.foodpetshop;

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
public class RegisterFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "my_app";

    private String mParam1;
    private String mParam2;

    private View view;

    public RegisterFragment() {
        // Required empty public constructor
    }
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        view = inflater.inflate(R.layout.fragment_register, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        ImageButton backbutton = view.findViewById(R.id.imagebuttonback);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, LoginFragment.newInstance(null, null));
                transaction.commit();
            }
        });

        ImageButton loginButton = view.findViewById(R.id.registerButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = view.findViewById(R.id.storageItemName);
                EditText email = view.findViewById(R.id.InputEmail);
                EditText phone = view.findViewById(R.id.InputPhone);
                EditText password = view.findViewById(R.id.InputPassword);
                EditText confirmpassword = view.findViewById(R.id.InputconfirmPassword);
                String usernameText = username.getText().toString();
                String emailText = email.getText().toString();
                String phoneText = phone.getText().toString();
                String passwordText = password.getText().toString();
                String confirmpasswordText = confirmpassword.getText().toString();

                if(usernameText.isEmpty() && emailText.isEmpty() && phoneText.isEmpty() && passwordText.isEmpty() && confirmpasswordText.isEmpty()){
                    Toast.makeText(getActivity(), "คุณจำเป็นต้องกรอกให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "http://154.202.2.5:4990/api/users/register";
                StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, response);

                                String type = null;
                                String message = null;

                                try {
                                    JSONObject jsonobject = new JSONObject(response);
                                    type = jsonobject.getString("type");
                                    message = jsonobject.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(type.matches("user_already")){
                                    Toast.makeText(getActivity(), "ชื่อผู้ใช้หรืออีเมลนี้มีผู้ใช้งานแล้ว", Toast.LENGTH_SHORT).show();
                                } else if(type.matches("wrong_password_notmatch")){
                                    Toast.makeText(getActivity(), "รหัสผ่านทั้งสองช่องของคุณไม่ตรงกัน", Toast.LENGTH_SHORT).show();
                                } else if(type.matches("success")){
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.navHostFragment, LoginFragment.newInstance(null, null));
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
                            }
                        })
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", usernameText);
                        params.put("email", emailText);
                        params.put("telephone", phoneText);
                        params.put("password", passwordText);
                        params.put("confirmpassword", confirmpasswordText);
                        params.put("address", String.valueOf("null"));
                        params.put("admin", String.valueOf(false));
                        return params;
                    }
                };


                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(stringRequest);
            }
        });
    }
}