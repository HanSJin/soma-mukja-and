package com.hansjin.mukja_android.Sign;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Splash.SplashActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
public class SignFragment extends Fragment {
    CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    TextView info;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    EditText ET_email;
    EditText ET_pw;

    Button BT_signin;
    Button BT_signup;

    User n_user;
    Map field = new HashMap();

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            // 정보 받아오는 graph api
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            Log.i("makejin","object " + object);

                            field.put("social_id", object.optString("id"));
                            try{
                                JSONArray jsonArray = response.getJSONObject().getJSONObject("friends").getJSONArray("data");
                                int len = jsonArray.length();
                                List<String> list = new ArrayList<>();
                                for(int idx=0;idx<len;idx++) {
                                    list.add(0,jsonArray.getJSONObject(idx).get("id").toString());
                                }
                                Log.i("makejin33", "list " + list);
                                field.put("friends", list);
                                Log.i("makejin33", "field " + field);
                                connectSigninUser(field);//이미 최초 로그인을 한 기록이 있어서 회원가입이 되있는 경우
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            //최초 로그인 => 회원가입
                            if(SharedManager.getInstance().getMe() == null) {
                                n_user = new User();
                                n_user.social_id = object.optString("id");
                                n_user.social_type = "facebook";
                                n_user.push_token = "random";
                                n_user.device_type = "android";
                                n_user.app_version = getAppVersion(getActivity());
                                n_user.nickname = object.optString("name");
                                n_user.about_me = "자기소개 글을 입력해주세요";
                                n_user.age = 0;
                                if(object.optString("gender").equals("male"))
                                    n_user.gender = false;
                                else
                                    n_user.gender = true;

                                n_user.job = "";
                                n_user.location = SplashActivity.cityName;
                                n_user.password = null;
                                try{
                                    JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                    int len = jsonArray.length();
                                    List<String> list = new ArrayList<>();
                                    for(int idx=0;idx<len;idx++) {
                                        list.add(0,jsonArray.getJSONObject(idx).get("id").toString());
                                    }
                                    Log.i("makejin33", "list " + list);
                                    n_user.friends = list;
                                    Log.i("makejin33", "field " + field);
                                }catch (Exception e){

                                }
                                connectCreateUser(n_user);
                            }


                            //tempAge = object.optString("age_range");
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,friends");
            request.setParameters(parameters);
            request.executeAsync();
        }


        @Override
        public void onCancel() {
            info.setText("Login attempt canceled.");
        }

        @Override
        public void onError(FacebookException e) {
            info.setText("Login attempt failed.");

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(SignActivity.context);
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        initInstance(view);

        return view;
    }

    public void initInstance(View view){
        prefs = getActivity().getSharedPreferences("TodayFood", Context.MODE_PRIVATE);
        editor = prefs.edit();

        BT_signin = (Button)view.findViewById(R.id.BT_signin);
        BT_signup = (Button)view.findViewById(R.id.BT_signup);

        ET_email = (EditText)view.findViewById(R.id.ET_email);
        ET_pw = (EditText)view.findViewById(R.id.ET_pw);

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // App code
                if(currentAccessToken == (null)){ //로그아웃된 상태
                    info.setText("");
                }
            }

        };

        accessTokenTracker.startTracking();

        info = (TextView) view.findViewById(R.id.info);


        if(isLoggedIn()){
            field.put("social_id", AccessToken.getCurrentAccessToken().getUserId());
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try{

                                JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                int len = jsonArray.length();
                                List<String> list = new ArrayList<>();
                                for(int idx=0;idx<len;idx++) {
                                    list.add(0,jsonArray.getJSONObject(idx).get("id").toString());
                                }
                                field.put("friends", list);
                                connectSigninUser(field);
                            }catch (Exception e){

                            }
                        }
                    }
            ).executeAsync();
        }
        else if(!prefs.getString("social_id","").equals("")){
            field.put("social_id", prefs.getString("social_id",""));
            connectSigninUser_NonFacebook(field);
        }


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {


                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }

        };


        BT_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempEmail = ET_email.getText().toString();
                String tempPassword = ET_pw.getText().toString();

                if(tempEmail.equals("") || tempPassword.equals("")){
                    Toast toast = Toast.makeText(getActivity(), "Write your E-mail and password", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                    toast.show();
                    return;
                }

                field = new HashMap();
                field.put("social_id", tempEmail);
                field.put("password", tempPassword);

                connectSigninUser_NonFacebook(field);
            }
        });

        BT_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment reg = new SignupNonFacebookFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_sign, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.BT_facebook);
        loginButton.setReadPermissions("public_profile", "user_friends","email", "user_birthday");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public String getAppVersion(Context context) {

        // application version
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }

        return versionName;
    }

    void connectCreateUser(User user) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signupUser(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<com.hansjin.mukja_android.Model.User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);

                            editor.putString("social_id", response.social_id);
                            editor.commit();

                            Intent intent = new Intent(getActivity(), TabActivity_.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void connectSigninUser(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        Log.i("makejin22", ""+field);
        conn.signinUser(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                            editor.putString("social_id", response.social_id);
                            editor.commit();
                            Intent intent = new Intent(getActivity(), TabActivity_.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    void connectSigninUser_NonFacebook(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signinUser_NonFacebook(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            if(response.social_id == null){
                                Toast.makeText(getActivity(), "ID 혹은 PW를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            SharedManager.getInstance().setMe(response);

                            editor.putString("social_id", response.social_id);
                            editor.commit();
                            Intent intent = new Intent(getActivity(), TabActivity_.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
