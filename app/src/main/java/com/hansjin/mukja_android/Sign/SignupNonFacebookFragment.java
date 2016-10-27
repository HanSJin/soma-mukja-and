package com.hansjin.mukja_android.Sign;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Splash.SplashActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SignupNonFacebookFragment extends Fragment {
    SharedPreferences prefs;
    EditText ET_email, ET_name, ET_pw, ET_pw2;
    Button BT_signup;
    private RadioButton RB_gender_male;
    private RadioButton RB_gender_female;



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_non_facebook, container, false);

        prefs = getActivity().getSharedPreferences("Chat", 0);

        ET_email = (EditText)view.findViewById(R.id.ET_email);
        ET_name = (EditText)view.findViewById(R.id.ET_name);
        ET_pw = (EditText)view.findViewById(R.id.ET_pw);
        ET_pw2 = (EditText)view.findViewById(R.id.ET_pw2);

        BT_signup = (Button)view.findViewById(R.id.BT_signup);

        RB_gender_male = (RadioButton) view.findViewById(R.id.RB_gender_male);
        RB_gender_female = (RadioButton) view.findViewById(R.id.RB_gender_female);

        BT_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempEmail = ET_email.getText().toString();
                String tempName = ET_name.getText().toString();
                String tempPw = ET_pw.getText().toString();
                String tempPw2 = ET_pw2.getText().toString();
                boolean tempGender = false;

                if(tempEmail.equals("") || tempName.equals("") || tempPw.equals("") || tempPw2.equals("") || (!RB_gender_male.isChecked() && !RB_gender_female.isChecked()) ){
                    Toast toast = Toast.makeText(getActivity(), "Write your information all", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                    return;
                }

                SharedPreferences.Editor edit = prefs.edit();

                if(tempPw.equals(tempPw2)) {
                    edit.putString("social_id", tempEmail);
                    edit.putString("nickname", tempName);
                    edit.putString("password", tempPw);
                    edit.commit();

                    User n_user = new User();
                    n_user.social_id = tempEmail;
                    n_user.social_type = "normal";
                    n_user.push_token = "random";
                    n_user.device_type = "android";
                    n_user.app_version = getAppVersion(getActivity());
                    n_user.nickname = tempName;
                    n_user.about_me = "자기소개 글을 입력해주세요";
                    n_user.age = 0;
                    if(RB_gender_female.isChecked()){
                        tempGender = true;
                    }
                    n_user.gender = tempGender;
                    n_user.job = "";
                    n_user.location = SplashActivity.cityName;
                    n_user.password = tempPw;

                    connectCreateUser(n_user);
                }else{
                    Toast toast = Toast.makeText(getActivity(), "incorrect 1st PW and 2nd PW", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                    return;
                }
            }
        });

        return view;
    }

    void connectCreateUser(User user) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signupUser(user)
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
}