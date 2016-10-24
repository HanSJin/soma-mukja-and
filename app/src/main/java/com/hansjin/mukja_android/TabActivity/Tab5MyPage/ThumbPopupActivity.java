package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.app.Activity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb_popup);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        prefs = getSharedPreferences("TodayFood",0);

        BT_take_a_picture = (Button) findViewById(R.id.BT_take_a_picture);
        BT_choose_a_picture = (Button) findViewById(R.id.BT_choose_a_picture);
        BT_close_up_a_picture = (Button) findViewById(R.id.BT_close_up_a_picture);
        BT_remove_a_picture = (Button) findViewById(R.id.BT_remove_a_picture);
        BT_close = (Button) findViewById(R.id.BT_close);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == TAKE_CAMERA){
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);

                Log.e("imagepath : ", imagepath);
                Log.e("upload message : ", "Uploading file path:" + imagepath);

                // 현재시간을 얻는다.
                Long now = System.currentTimeMillis();

                // 원래 파일의 확장자를 얻기 위한 파싱 "." 뒤에 있는 확장자를 가져온다.

                String str = imagepath.substring(imagepath.indexOf("."));

                uploadFileName = prefs.getString("user_id","") + "_Profile.jpg";

                Bitmap bm = (Bitmap) data.getExtras().get("data");
                String image_url = "http://graph.facebook.com/" + SharedManager.getInstance().getMe().social_id + "/picture?width=78&height=78";
                Log.i("url", image_url);
                Glide.with(getApplicationContext()).load(bm).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(Tab5MyPageFragment.IV_profile);
                //Glide.with(context).load(image_url).into(itemViewHolder.IV_food);
                /*
                try {


                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    Tab5MyPageFragment.profile_image.setBackgroundColor(Color.TRANSPARENT);
                    Tab5MyPageFragment.profile_image.setImageDrawable(new RoundedAvatarDrawable(image_bitmap));

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                */
            }else if(requestCode == TAKE_GALLERY){

                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);
                Log.e("imagepath : ", imagepath);
                Log.e("upload message : ", "Uploading file path:" + imagepath);
                //TODO:임시데이터 넣음 user+현재시간으로 바꿀 것
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
                //image_url = Constants.IMAGE_BASE_URL+SharedManager.getInstance().getMe()._id+current_time+".png";
                image_url = "lmjing_"+current_time;;
                uploadFile1(SharedManager.getInstance().getMe());

                //Glide.with(getApplicationContext()).load(image_url).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(Tab5MyPageFragment.IV_profile);
/*
                Glide.with(getApplicationContext()).load(imagepath).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Tab5MyPageFragment.IV_profile.setImageBitmap(resource);
                    }
                });
*/


/*
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);
                Log.e("imagepath : ", imagepath);
                Log.e("upload message : ", "Uploading file path:" + imagepath);
                //TODO:임시데이터 넣음 user+현재시간으로 바꿀 것
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
                image_url = Constants.IMAGE_BASE_URL+SharedManager.getInstance().getMe()._id+current_time+".png";

                //path = getPathFromURI(data.getData());
                //Tab5MyPageFragment.IV_profile.setImageURI(data.getData());
                Glide.with(getApplicationContext()).load(imagepath).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Tab5MyPageFragment.IV_profile.setImageBitmap(resource);
                    }
                });
                File f = new File(imagepath);

                Log.i("Asd", path + " "+f);

                image_url = SharedManager.getInstance().getMe()._id+current_time;
                Future uploading = Ion.with(getApplicationContext())
                        .load(image_url)
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try {
                                    JSONObject jobj = new JSONObject(result.getResult());
                                    Toast.makeText(getApplicationContext(), jobj.optString("response"), Toast.LENGTH_SHORT).show();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });

*/
                /*
                try {
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //Tab5MyPageFragment.profile_image.setBackgroundColor(Color.TRANSPARENT);
                    Tab5MyPageFragment.profile_image.setImageDrawable(new RoundedAvatarDrawable(image_bitmap));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                */

            }

        }

    }
/*
    void UpdateUser() {
        n_user._id = SharedManager.getInstance().getMe()._id;
        n_user.author.author_nickname = SharedManager.getInstance().getMe().nickname;
        n_user.author.author_thumbnail_url = SharedManager.getInstance().getMe().thumbnail_url;
        n_user.author.author_thumbnail_url_small = SharedManager.getInstance().getMe().thumbnail_url_small;

        Map field = new HashMap();
        field.put("name", n_food.name);
        field.put("taste", n_food.taste);
        field.put("cooking", n_food.cooking);
        field.put("country", n_food.country);
        field.put("ingredient", n_food.ingredient);
        field.put("author", n_food.author);
        field.put("image_url", n_food.image_url);
        Log.d("hansjin", "Filed" + field.toString());

        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.foodPost(n_food)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            Toast.makeText(getApplicationContext(), "음식 업로드에 성공했습니다!", Toast.LENGTH_SHORT).show();
                            uploadFile1(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    */

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
                        //n_food.rate_person.add(0,n_food.newrate(SharedManager.getInstance().getMe()._id,rate_num));
                        //food_rate(food);
                        //setResult(Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT);
                        Glide.with(getApplicationContext()).load(image_url).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(Tab5MyPageFragment.IV_profile);
                       /* Glide.with(getApplicationContext()).load(imagepath).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                Tab5MyPageFragment.IV_profile.setImageBitmap(resource);
                            }
                        });*/
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

    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
