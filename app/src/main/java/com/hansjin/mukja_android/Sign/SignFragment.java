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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandFragment;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageFragment;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.GetDeviceInfo;
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
    private Typeface yunGothicFont;
    // 새로운 컴포넌트들
    CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    TextView info;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    Intent fbLoginIntent;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    public User tempUser = new User();

    private LoginButton mButtonFacebookSignup;
    private Button mButtonNormalSignup;
    private Button tempFBButton;

    String tempId;
    String tempEmail;
    String tempName;
    Boolean tempGender; //male - false / female - true
    String device_type;
    String app_version;
    String about_me;
    Integer age;
    String job;
    String location;
    String thumbnail_url;
    String thumbnail_url_small;
    String access_ip;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i("asd", "zxc1-13");
            Log.i("asd", "zxc2");
            // 정보 받아오는 graph api
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            info.setText("email : " + object.optString("email"));
                            info.append("\nname : " + object.optString("name"));
                            info.append("\ngender : " + object.optString("gender"));
                            Log.e("aaa1", "" + object.optString("email") + " " +object.optString("id"));

                            Log.e("aaa", "" + response.getJSONObject());

                            /*
                            try {
                                info.append("\nage range : " + object.getString("age range"));
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                            info.append("\nage range : " + object.optString("age range"));
                            */
                            info.append("\n\n위와 같이, 페이스북 정보를 받을 수 있으나 사용하지않습니다. \n본 서비스를 이용하시려면 \"Not a member\"를 클릭해주세요.");

                            tempId = object.optString("id");
                            tempEmail = object.optString("email");
                            tempName = object.optString("name");
                            if(object.optString("gender").equals("male")){
                                tempGender = false;
                            }else{
                                tempGender = true;
                            }

//                            prefs = getActivity().getSharedPreferences("TodayFood", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = prefs.edit();


                            device_type = "android";
                            app_version = getAppVersion(getActivity());;
                            Log.i("asd", app_version);
                            about_me = "자기소개 글을 입력해주세요";
                            age = 24;
                            job = "";
                            location = ""; //위치정보 가져오기
                            thumbnail_url = "http://graph.facebook.com/" + tempId + "/picture?type=large";
                            thumbnail_url_small = "http://graph.facebookcom/" + tempId + "/picture?width=78&height=78";
                            //access_ip = GetDeviceInfo.getIPAddress(true);
                            access_ip = "";//server에서 구현
                            Log.i("asd", ""+GetDeviceInfo.getIPAddress(true));
                            Log.i("asd", ""+GetDeviceInfo.getIPAddress(false));

//                            User n_user = new User(tempId, tempName, tempGender, device_type, app_version, about_me, age, job, location,
//                                    thumbnail_url, thumbnail_url_small, access_ip);
                            //User(String social_id, String name, Boolean gender, String device_type, String app_version, String about_me, Integer age, String job, String location, String thumbnail_url, String thumbnail_url_small, String access_ip)
//                            connectCreateUser(n_user);


                            editor.putString("user_id", tempId);
                            editor.putString("user_name", tempName);
                            editor.putString("user_about_me", "자기소개를 입력해주세요");

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
            Log.i("asd", "zxc1-15");
        }

        @Override
        public void onError(FacebookException e) {
            Log.i("asd", "zxc1-16");
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

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // App code
                Log.i("eNuri", "Current Token : " + currentAccessToken);
                if(currentAccessToken == (null)){ //로그아웃된 상태
                    info.setText("");
                    //info.setTextColor(Color.red(1));
                }
            }

        };



        accessTokenTracker.startTracking();

        facebookLoginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        facebookLoginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        info = (TextView) view.findViewById(R.id.info);

//        if(isLoggedIn()){

        // TEST LOGIN - By HanSJin (Facebook API Open 안되서 내가 접근 못함 ..ㅠ)
            Map field = new HashMap();
            field.put("social_id", prefs.getString("social_id","793160210817466"));
            connectSigninUser(field);
            Intent intent = new Intent(getActivity(), TabActivity_.class);
            startActivity(intent);
            getActivity().finish();

//        }


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

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.facebook_login_button);
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

    public static String getAppVersion(Context context) {

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
                        /*
                        Fragment fragment = new SignAddFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.activity_sign, fragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                        */

                        Intent intent = new Intent(getActivity(), TabActivity_.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(com.hansjin.mukja_android.Model.User response) {
                        if (response != null) {
                            /*
                            tempUser.setUser(response);
                            Log.i("azxc",""+response.getUser_social_id());
                            Log.i("azxc",""+response.getUser_nickname());
                            Log.i("azxc",""+response.getUser_about_me());

                            Log.i("asd", tempUser.getUser_nickname() + " " + tempUser.getUser_about_me());
                            editor.commit();
                            */
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
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
