package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.RoundedAvatarDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.concurrent.Future;


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
                Tab5MyPageFragment.profile_image.setBackgroundColor(Color.TRANSPARENT);
                Tab5MyPageFragment.profile_image.setImageDrawable(new RoundedAvatarDrawable(bm));
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

                // 현재시간을 얻는다.
                Long now = System.currentTimeMillis();

                // 원래 파일의 확장자를 얻기 위한 파싱 "." 뒤에 있는 확장자를 가져온다.

                String str = imagepath.substring(imagepath.indexOf("."));

                uploadFileName = prefs.getString("user_id","") + "_Profile.jpg";


                path = getPathFromURI(data.getData());
                Tab5MyPageFragment.profile_image.setImageURI(data.getData());

                File f = new File(path);

                Log.i("Asd", path + " "+f);

                Future uploading = Ion.with(getApplicationContext())
                        .load(Constants.API_BASE_URL+ "/upload")
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

        /*
        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);

            Log.e("imagepath : ", imagepath);
            Log.e("upload message : ", "Uploading file path:" + imagepath);

            // 현재시간을 얻는다.
            Long now = System.currentTimeMillis();

            // 원래 파일의 확장자를 얻기 위한 파싱 "." 뒤에 있는 확장자를 가져온다.

            String str = imagepath.substring(imagepath.indexOf("."));

            uploadFileName = prefs.getString("info_id","") + "_Profile.jpg";

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


        }
        */
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
