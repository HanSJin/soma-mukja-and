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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

    Map field;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // 정보 받아오는 graph api
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
//                            info.setText("email : " + object.optString("email"));
//                            info.append("\nname : " + object.optString("name"));
//                            info.append("\ngender : " + object.optString("gender"));
//
//                            info.append("\n\n위와 같이, 페이스북 정보를 받을 수 있으나 사용하지않습니다. \n본 서비스를 이용하시려면 \"Not a member\"를 클릭해주세요.");

                            User n_user = new User();
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
                            //social_id,social_type,push_token, device_type, app_version, nickname,about_me,age, gender,job,location, password
                            connectCreateUser(n_user);

                            editor.putString("social_id", n_user.social_id);

                            editor.commit();

                            //tempAge = object.optString("age_range");
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
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

        facebookLoginButton = (LoginButton) view.findViewById(R.id.BT_facebook);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        facebookLoginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        info = (TextView) view.findViewById(R.id.info);

        Log.i("makejin6", prefs.getString("social_id",""));

        if(!prefs.getString("social_id","").equals("")){
            Map field = new HashMap();
            field.put("social_id", prefs.getString("social_id",""));
            connectSigninUser(field);
        }else if(isLoggedIn()){
            Map field = new HashMap();
            field.put("social_id", AccessToken.getCurrentAccessToken().getUserId());
            connectSigninUser(field);
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
        loginButton.setReadPermissions("public_profile", "user_friends","email");
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
