
//package com.hansjin.mukja_android;



//@EActivity(R.layout.activity_user_activiy)
//public class UserActiviy extends AppCompatActivity {
    /*
    UserActiviy activity;
    public static String id = null;

    @ViewById(R.id.name_edit)
    EditText name_edit;
    @ViewById(R.id.result_txt)
    TextView result_txt;

    @ViewById
    public LinearLayout indicator;

    @Click(R.id.createUser)
    void createUser() {
        String name = name_edit.getText().toString();
        User n_user = new User(name);
        connectCreateUser(n_user);
    }


    @Click(R.id.createAllFoods)
    void createAllFoods() {
        connectCreateFoods();
    }

    @Click(R.id.Recommendation)
    void Recommendation(){
        //connectRecommendationCall();
    }


    void connectCreateUser(User user) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.createUser(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user_id",id);
                        editor.commit();
                        Intent intent = new Intent(UserActiviy.this, SplashActivity_.class);
                        startActivity(intent);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            id = response.getUser_id();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void connectCreateFoods() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.createAllItems()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        Toast.makeText(getApplicationContext(), "PIO 초기 데이터 생성 성공", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Result response) {
                        if (response != null) {
                            Log.i("pio","response : "+response.getResult());
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    */

    /*
    void connectRecommendationCall() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.recommendationResult(name_edit.getText().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<itemScores>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        Toast.makeText(getApplicationContext(), "PIO Create Items Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        Log.i("result","서버오류");
                        LoadingUtil.stopLoading(indicator);
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<itemScores> response) {
                        if (response != null) {
                            result_txt.setText(response.get(0).getItems());
                        } else {
                            result_txt.setText("결과 값이 없습니다");
                        }
                    }
                });
    }
    */
//}
//package com.hansjin.mukja_android;
//
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.hansjin.mukja_android.Model.Result;
//import com.hansjin.mukja_android.Model.User;
//import com.hansjin.mukja_android.Model.itemScores;
//import com.hansjin.mukja_android.Splash.SplashActivity_;
//import com.hansjin.mukja_android.Utils.Connections.CSConnection;
//import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
//import com.hansjin.mukja_android.Utils.Constants.Constants;
//import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
//
//import org.androidannotations.annotations.Click;
//import org.androidannotations.annotations.EActivity;
//import org.androidannotations.annotations.ViewById;
//
//import java.util.List;
//
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//@EActivity(R.layout.activity_user_activiy)
//public class UserActiviy extends AppCompatActivity {
//    UserActiviy activity;
//    public static String id = null;
//
//    @ViewById(R.id.name_edit)
//    EditText name_edit;
//    @ViewById(R.id.result_txt)
//    TextView result_txt;
//
//    @ViewById
//    public LinearLayout indicator;
//
//    @Click(R.id.createUser)
//    void createUser() {
//        String name = name_edit.getText().toString();
//        User n_user = new User(name);
//        connectCreateUser(n_user);
//    }
//
//
//    @Click(R.id.createAllFoods)
//    void createAllFoods() {
//        connectCreateFoods();
//    }
//
//    @Click(R.id.Recommendation)
//    void Recommendation(){
//        connectRecommendationCall();
//    }
//
//
//    void connectCreateUser(User user) {
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.createUser(user)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//                        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putString("user_id",id);
//                        editor.commit();
//                        Intent intent = new Intent(UserActiviy.this, SplashActivity_.class);
//                        startActivity(intent);
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            id = response.getUser_id();
//                        } else {
//                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    void connectCreateFoods() {
//        LoadingUtil.startLoading(indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.createAllItems()
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Result>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.stopLoading(indicator);
//                        Toast.makeText(getApplicationContext(), "PIO 초기 데이터 생성 성공", Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(Result response) {
//                        if (response != null) {
//                            Log.i("pio","response : "+response.getResult());
//                        } else {
//                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    void connectRecommendationCall() {
//        LoadingUtil.startLoading(indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.recommendationResult(name_edit.getText().toString())
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<itemScores>>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.stopLoading(indicator);
//                        Toast.makeText(getApplicationContext(), "PIO Create Items Success", Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        Log.i("result","서버오류");
//                        LoadingUtil.stopLoading(indicator);
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(List<itemScores> response) {
//                        if (response != null) {
//                            result_txt.setText(response.get(0).getItems());
//                        } else {
//                            result_txt.setText("결과 값이 없습니다");
//                        }
//                    }
//                });
//    }
//}
