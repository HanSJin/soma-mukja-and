package com.hansjin.mukja_android.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageFragment;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.PredictionIO.PredictionIOLearnEvent;
import com.hansjin.mukja_android.Utils.RoundedAvatarDrawable;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.hansjin.mukja_android.Utils.TimeFormatter.TimeFormmater;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
TODO: 현재는 음식 명 입력하는대로 food데이터에 무조건 넣는데 나중에는 음식 명 통일해야 할 듯
예를 들어 현재: 김치 찌개 != 김치찌개 => 나중엔 음식명 치는데다가 김치만 치는 순간 자동완성으로 선택할 수 있게 현재 있는 음식 데이터에
해당 음식이 없을 경우에만 새로운 음식명으로 추가 할 수 있게끔..?
 */

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    RegisterActivity activity;

    String taste_list[] = new String[]{};
    String country_list[] = new String[]{};
    String cooking_list[] = new String[]{};

    List<String> category_list = new ArrayList<String>();

    Food n_food = new Food();
    Float rate_num = 10.0f;

    private String imagepath=null;
    SharedPreferences prefs;

    @ViewById
    Toolbar cs_toolbar;
    @ViewById
    EditText edit_name;
    @ViewById
    ImageView food_image;
    @ViewById
    Spinner taste_spinner;
    @ViewById
    Spinner country_spinner;
    @ViewById
    Spinner cooking_spinner;
    @ViewById
    EditText edit_ingredient;
    @ViewById
    RatingBar ratingBar;
    @ViewById
    TagFlowLayout category_result;
    @ViewById
    TagFlowLayout ingredient_result;

    @Click
    void btn_ingredient() {
        n_food.ingredient.add(edit_ingredient.getText().toString());
        addFlowChart(ingredient_result,n_food.ingredient.toArray(new String[n_food.ingredient.size()]));
        edit_ingredient.setText("");
    }

    @Click
    void food_image() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        prefs = getSharedPreferences("TodayFood",0);

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("음식 등록");

        //TODO: 카테고리 서버 연동 테스트
        //only android code test
        taste_list = new String[]{"[맛]","매콤","새콤","달콤"};
        country_list = new String[]{"[국가]","한식","중식","일식"};
        cooking_list = new String[]{"[조리방식]","조림","국","탕"};
        initSpinner(taste_spinner,taste_list,1);
        initSpinner(country_spinner,country_list,2);
        initSpinner(cooking_spinner,cooking_list,3);
        /*
        getTasteList();
        getCountryList();
        getCookingList();
        */
        set_rating();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.finish) {
            //현재 순서 : 빈칸 체크 -> 이미지 업로드 -> 음식 등록 -> 별점 평가
            check_blank();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);
                Log.e("imagepath : ", imagepath);
                Log.e("upload message : ", "Uploading file path:" + imagepath);
                //TODO:임시데이터 넣음 user+현재시간으로 바꿀 것
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmss");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
                n_food.image_url = "lmjing_"+current_time;
                //n_food.image = prefs.getString("info_id","") + "_Profile.jpg";
                try {
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    food_image.setImageBitmap(image_bitmap);
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
        }
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /*
    public void uploadImage(){
        File file = new File(imagepath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.uploadImage(body,name,n_food.image)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public final void onCompleted() {
                        RegisterFood();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(ResponseBody response) {
                        if (response != null) {
                            Log.i("image","success");
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    */
    private void uploadFile() {
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        File file = new File(imagepath);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
        String descriptionString = n_food.name;
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        Call<ResponseBody> call = conn.uploadImage(body,description,n_food.image_url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response) {
                Log.v("Upload", "success");
                RegisterFood();
            }
            @Override
            public void onFailure(Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    void RegisterFood() {
        n_food.author.author_id = SharedManager.getInstance().getMe()._id;
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.foodPost(n_food)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        Toast.makeText(getApplicationContext(), "음식 업로드에 성공했습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            Log.i("post","succes : "+response.name.toString());
                            n_food._id = response._id;
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void check_blank(){
        //TODO:서버 이미지 처리 : 현재 rate,new_food등록 후 이미지 업로드 하는데 업로드할때 food_id보내므로 해당 food_id활용해서 서버단에서 uri 만든 후 update할 것
        n_food.name = edit_name.getText().toString();
        if(n_food.name==null)
            Snackbar.make(ratingBar, "음식명을 작성해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(imagepath==null)
            Snackbar.make(ratingBar, "사진을 등록해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(n_food.taste.size()==0||n_food.country.size()==0||n_food.cooking.size()==0)
            Snackbar.make(ratingBar, "맛/국가/조리방식 각 하나이상 선택해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(n_food.ingredient.size()==0)
            Snackbar.make(ratingBar, "식재료 하나이상 입력해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(rate_num==10.0f)
            Snackbar.make(ratingBar, "음식 평가 해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else
            uploadFile();
    }

    private void initSpinner(final Spinner s, String[] array, final int type){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.spin,array);

        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!s.getSelectedItem().toString().equals("[맛]")
                        &&!s.getSelectedItem().toString().equals("[국가]")
                        &&!s.getSelectedItem().toString().equals("[조리방식]")){
                    switch (type) {
                        case 1:
                            n_food.taste.add(s.getSelectedItem().toString());
                            break;
                        case 2:
                            n_food.country.add(s.getSelectedItem().toString());
                            break;
                        case 3:
                            n_food.cooking.add(s.getSelectedItem().toString());
                            break;
                    }
                    category_list.add(s.getSelectedItem().toString());
                    addFlowChart(category_result, category_list.toArray(new String[category_list.size()]));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void set_rating() {
        ratingBar.setIsIndicator(false);
        ratingBar.setStepSize(0.1f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            //v : 4개가 채워지면 0.5점*8만큼 해서 점수가 v=4로 넘어 온다.
            public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {
                //5점 만점
                float st = 5f / ratingBar.getNumStars();
                rate_num = st * v;
                Toast.makeText(getApplicationContext(),String.valueOf(rate_num),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFlowChart(final TagFlowLayout mFlowLayout, String[] array){
        final LayoutInflater mInflater = LayoutInflater.from(getApplication());
        mFlowLayout.setAdapter(new TagAdapter<String>(array){
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.tag_result, mFlowLayout, false);
                    tv.setText(s);
                    return tv;
                }

                @Override
                public boolean setSelected(int position, String s)
                {
                    return s.equals("Android");
                }
            });
        }


    public void getTasteList() {
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getTasteList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public final void onCompleted() {
                        initSpinner(taste_spinner,taste_list,1);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<String> response) {
                        if (response != null) {
                            response.add(0,"[맛]");
                            taste_list = response.toArray(new String[response.size()]);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void getCountryList() {
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getCountryList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public final void onCompleted() {
                        initSpinner(country_spinner,country_list,2);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<String> response) {
                        if (response != null) {
                            response.add(0,"[국가]");
                            country_list = response.toArray(new String[response.size()]);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void getCookingList() {
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getCookingList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public final void onCompleted() {
                        initSpinner(cooking_spinner,cooking_list,3);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<String> response) {
                        if (response != null) {
                            response.add(0,"[조리방식]");
                            cooking_list = response.toArray(new String[response.size()]);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
