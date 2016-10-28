package com.hansjin.mukja_android.Splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.ResultActivity_;
import com.hansjin.mukja_android.Sign.SignActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.hansjin.mukja_android.Utils.VersionUpdate.MarketVersionChecker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

@EActivity(R.layout.activity_activity)
public class SplashActivity extends AppCompatActivity {
    SplashActivity activity;

    //currrent location start
    private LocationManager locationManager = null; // 위치 정보 프로바이더
    private LocationListener locationListener = null; //위치 정보가 업데이트시 동작

    private static final String TAG = "debug";
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    public static double lat = 0.0;
    public static double lon = 0.0;
    //currrent location end

    public static String cityName;

    String deviceVersion;
    String storeVersion;

    private BackgroundThread mBackgroundThread;


    @AfterViews
    void afterBindingView() {
        this.activity = this;

        //앱 출시 정상적으로 되면 테스트해보기
        //mBackgroundThread = new BackgroundThread();
        //mBackgroundThread.start();

        SharedPreferences prefs = getSharedPreferences("TodayFood", Context.MODE_PRIVATE);

        if(prefs.getString("social_id","").equals("")){
            Intent intent = new Intent(activity, SignActivity.class);
            startActivity(intent);
            finish();
        }else {
            Map field = new HashMap();
            field.put("social_id", prefs.getString("social_id", ""));
            Log.i("makejin", "prefs.getString(\"social_id\", \"\")" + prefs.getString("social_id", ""));
            connectSigninUser(field);
        }

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //GPS_PROVIDER: GPS를 통해 위치를 알려줌
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //NETWORK_PROVIDER: WI-FI 네트워크나 통신사의 기지국 정보를 통해 위치를 알려줌
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPSEnabled && isNetworkEnabled){
            Toast.makeText(getApplicationContext(), "내 위치정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
            locationListener = new MyLocationListener();

            //선택된 프로바이더를 사용해 위치정보를 업데이트
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);

        }else{
            alertbox("gps 상태!!", "당신의 gps 상태 : off");
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            // 패키지 네임 전달
            String storeVersion = MarketVersionChecker.getMarketVersionFast(getPackageName());

            // 디바이스 버전 가져옴
            try {
                deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            deviceVersionCheckHandler.sendMessage(deviceVersionCheckHandler.obtainMessage());
            // 핸들러로 메세지 전달
        }
    }

    private final DeviceVersionCheckHandler deviceVersionCheckHandler = new DeviceVersionCheckHandler(this);

    // 핸들러 객체 만들기
    private static class DeviceVersionCheckHandler extends Handler{
        private final WeakReference<SplashActivity> mainActivityWeakReference;
        public DeviceVersionCheckHandler(SplashActivity mainActivity) {
            mainActivityWeakReference = new WeakReference<SplashActivity>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                activity.handleMessage(msg);
                // 핸들메세지로 결과값 전달
            }
        }
    }

    public void handleMessage(Message msg) {
        //핸들러에서 넘어온 값 체크
        if (storeVersion.compareTo(deviceVersion) > 0) {
            // 업데이트 필요

            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light));
            alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
                    .setMessage("새로운 버전이 있습니다.\n보다 나은 사용을 위해 업데이트 해 주세요.")
                    .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 구글플레이 업데이트 링크

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

        } else {
            // 업데이트 불필요

        }
    }

    @UiThread
    void uiThread() {
        Intent intent = new Intent(activity, TabActivity_.class);
        startActivity(intent);
        finish();
    }

    @Background
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
                            uiThread();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    //gps 관련
    //현재 위치 정보를 받기위해 선택한 프로바이더에 위치 업데이터 요청! requestLocationUpdates()메소드를 사용함.
    private class MyLocationListener implements LocationListener {

        @Override
        //LocationListener을 이용해서 위치정보가 업데이트 되었을때 동작 구현
        public void onLocationChanged(Location loc) {
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            //좌표 정보 얻어 토스트메세지 출력
            /*Toast.makeText(getBaseContext(), "Location changed : Lat" + lat +
                    "Lng: " + lon, Toast.LENGTH_SHORT).show();
*/
            // 도시명 구하기
            cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try{
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if(addresses.size()>0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }catch(IOException e){
                e.printStackTrace();
            }
            String s = "현재, <" + cityName + ">에 계시군요 !";
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generatedchmethod stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

    }


    protected void alertbox(String title, String mymessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("your device's gps is disable")
                .setCancelable(false)
                .setTitle("**gps status**")
                .setPositiveButton("gps on", new DialogInterface.OnClickListener() {

                    //  폰 위치 설정 페이지로 넘어감
                    public void onClick(DialogInterface dialog, int id) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
