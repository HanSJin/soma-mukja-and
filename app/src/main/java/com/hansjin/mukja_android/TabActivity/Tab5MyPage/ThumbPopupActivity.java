package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ThumbPopupActivity extends Activity {

    Button BT_take_a_picture;
    Button BT_choose_a_picture;
    Button BT_take_from_facebook;
    Button BT_close_up_a_picture;
    Button BT_remove_a_picture;
    Button BT_close;
    private String imagepath=null;
    String uploadFileName=null;

    SharedPreferences prefs;

    private int TAKE_CAMERA = 1;					// 카메라 리턴 코드값 설정
    private int TAKE_GALLERY = 2;				// 앨범선택에 대한 리턴 코드값 설정
    String path;
    String image_url;
    User n_user;

    LinearLayout LL_popup_thumb;
    RelativeLayout RR_take_from_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb_popup);
        prefs = getSharedPreferences("TodayFood",0);

        BT_take_a_picture = (Button) findViewById(R.id.BT_take_a_picture);
        BT_choose_a_picture = (Button) findViewById(R.id.BT_choose_a_picture);

        BT_close_up_a_picture = (Button) findViewById(R.id.BT_close_up_a_picture);
        BT_remove_a_picture = (Button) findViewById(R.id.BT_remove_a_picture);
        BT_close = (Button) findViewById(R.id.BT_close);

        LL_popup_thumb = (LinearLayout) findViewById(R.id.LL_popup_thumb);
        RR_take_from_facebook = (RelativeLayout) findViewById(R.id.RR_take_from_facebook);

        BT_take_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_CAMERA);
            }
        });
        BT_choose_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TAKE_GALLERY);
            }
        });

        BT_close_up_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });
        BT_remove_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });
        BT_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(SharedManager.getInstance().getMe().social_type.equals("facebook")){
            //BT_take_from_facebook.setVisibility(View.GONE);
            BT_take_from_facebook = (Button) findViewById(R.id.BT_take_from_facebook);
            BT_take_from_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map field = new HashMap();
                    field.put("thumbnail_url",
                            "http://graph.facebook.com/"+SharedManager.getInstance().getMe().social_id + "/picture?type=normal");
                    field.put("thumbnail_url_small",
                            "http://graph.facebook.com/"+SharedManager.getInstance().getMe().social_id + "/picture?type=small");

                    connectUpdateUserImage_Facebook(SharedManager.getInstance().getMe()._id, field);
                }
            });
        }else{
            LL_popup_thumb.removeView(RR_take_from_facebook);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == TAKE_CAMERA){
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                imagepath = getPath(getImageUri(getApplicationContext(), bm));

                uploadFile1(SharedManager.getInstance().getMe());
            }else if(requestCode == TAKE_GALLERY){
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);

                uploadFile1(SharedManager.getInstance().getMe());
            }

        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void uploadFile1(final User user) {
        File file = new File(imagepath);
        RequestBody ubody = RequestBody.create(MediaType.parse("image/*"), file);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.fileUploadWrite_User(user._id, ubody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        Tab5MyPageFragment.IV_profile.invalidate();
                        finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().getMe().thumbnail_url = response.thumbnail_url;
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    void connectUpdateUserImage_Facebook(final String user_id, final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.updateUserImage_Facebook(user_id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        Tab5MyPageFragment.IV_profile.invalidate();
                        finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(com.hansjin.mukja_android.Model.User response) {
                        if (response != null) {
                            Log.i("makejin", "response "+response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
