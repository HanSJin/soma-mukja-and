package com.hansjin.mukja_android.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hansjin.mukja_android.Model.Category;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.GlobalResponse;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    RegisterActivity activity;

    List<String> taste_list = new ArrayList<>();
    List<String> country_list = new ArrayList<>();
    List<String> cooking_list = new ArrayList<>();

    List<String> category_list_taste = new ArrayList<String>();
    List<String> category_list_country = new ArrayList<String>();
    List<String> category_list_cooking = new ArrayList<String>();


    Food n_food = new Food();
    Float rate_num = 10.0f;

    private String imagepath = null;
    Boolean btn_push = false;

    String tempName;


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
    TagFlowLayout category_result_taste;
    @ViewById
    TagFlowLayout category_result_country;
    @ViewById
    TagFlowLayout category_result_cooking;
    @ViewById
    TagFlowLayout ingredient_result;

    public static int index = 0;

    @Click
    void btn_ingredient() {
        n_food.ingredient.add(edit_ingredient.getText().toString());
        addFlowChart(ingredient_result, n_food.ingredient.toArray(new String[n_food.ingredient.size()]));
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

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("음식 등록");

        //TODO: 카테고리 서버 연동 테스트
        //only android code test
        taste_list.add("[맛]");
        country_list.add("[국가]");
        cooking_list.add("[조리방식]");

        for (String taste : SharedManager.getInstance().getCategory().taste) {
            taste_list.add(taste);
        }
        for (String country : SharedManager.getInstance().getCategory().country) {
            country_list.add(country);
        }
        for (String cooking : SharedManager.getInstance().getCategory().cooking) {
            cooking_list.add(cooking);
        }
        initSpinner(taste_spinner, taste_list, 1);
        initSpinner(country_spinner, country_list, 2);
        initSpinner(cooking_spinner, cooking_list, 3);

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
            if (!btn_push) {
                btn_push = true;
                check_blank();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);
                Log.e("imagepath : ", imagepath);
                Log.e("upload message : ", "Uploading file path:" + imagepath);
                //TODO:임시데이터 넣음 user+현재시간으로 바꿀 것
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
                n_food.image_url = "lmjing_" + current_time;

                int[] maxTextureSize = new int[1];
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

                final int[] tempMaxTextureSize = maxTextureSize;

                Glide.with(activity).load(imagepath).asBitmap()./*override(500,300).*/into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        food_image.setImageBitmap(resource);

                        if (resource.getHeight() > tempMaxTextureSize[0]) {
                            int resizedWidth = food_image.getWidth();
                            int resizedHeight = food_image.getHeight();
                            food_image.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                        }

                    }
                });
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadFile1(final Food food) {
        File file = new File(imagepath);
        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), saveBitmapToFile(file));
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.fileUploadWrite(food._id, fbody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        Log.i("zxc", "업로드 완료 : " + food.name);
                        setResult(Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT);
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
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void RegisterFood() {
        for (index = 0; index < 10; index++) {

            n_food.author.author_id = SharedManager.getInstance().getMe()._id;
            n_food.author.author_nickname = SharedManager.getInstance().getMe().nickname;
            n_food.author.author_thumbnail_url = SharedManager.getInstance().getMe().thumbnail_url;
            n_food.author.author_thumbnail_url_small = SharedManager.getInstance().getMe().thumbnail_url_small;
            n_food.author.author_location_point = SharedManager.getInstance().getMe().location_point;
            n_food.name = tempName + index;
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
            n_food.update_date = current_time;

            n_food.rate_person.add(0, n_food.newrate(SharedManager.getInstance().getMe()._id, rate_num));

//            Map field = new HashMap();
//            field.put("name", n_food.name+String.valueOf(i));
//            field.put("taste", n_food.taste);
//            field.put("cooking", n_food.cooking);
//            field.put("country", n_food.country);
//            field.put("ingredient", n_food.ingredient);
//            field.put("author", n_food.author);
//            field.put("image_url", n_food.image_url);
//            Log.d("hansjin", "Filed" + field.toString());

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


        /*
        n_food.author.author_id = SharedManager.getInstance().getMe()._id;
        n_food.author.author_nickname = SharedManager.getInstance().getMe().nickname;
        n_food.author.author_thumbnail_url = SharedManager.getInstance().getMe().thumbnail_url;
        n_food.author.author_thumbnail_url_small = SharedManager.getInstance().getMe().thumbnail_url_small;
        n_food.author.author_location_point = SharedManager.getInstance().getMe().location_point;

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
        n_food.update_date = current_time;

        n_food.rate_person.add(0, n_food.newrate(SharedManager.getInstance().getMe()._id, rate_num));

        Map field = new HashMap();
        field.put("name", n_food.name);
        field.put("taste", n_food.taste);
        field.put("cooking", n_food.cooking);
        field.put("country", n_food.country);
        field.put("ingredient", n_food.ingredient);
        field.put("author", n_food.author);
        field.put("image_url", n_food.image_url);
        field.put("update_date",current_time);
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
                */
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private void check_blank(){
        //TODO:서버 이미지 처리 : 현재 rate,new_food등록 후 이미지 업로드 하는데 업로드할때 food_id보내므로 해당 food_id활용해서 서버단에서 uri 만든 후 update할 것
        n_food.name = edit_name.getText().toString();
        tempName = n_food.name;
        if(n_food.name==null)
            Snackbar.make(ratingBar, "음식명을 작성해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(imagepath==null)
            Snackbar.make(ratingBar, "사진을 등록해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(n_food.taste.size()==0||n_food.country.size()==0||n_food.cooking.size()==0)
            Snackbar.make(ratingBar, "맛/국가/조리방식 각 하나 이상 선택해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(n_food.ingredient.size()==0)
            Snackbar.make(ratingBar, "식재료 하나 이상 입력해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(rate_num==10.0f)
            Snackbar.make(ratingBar, "음식 평가 해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else
            RegisterFood();
    }

    private void initSpinner(final Spinner s, List<String> array, final int type){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.spin,array);

        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!s.getSelectedItem().toString().equals("[맛]")
                        && !s.getSelectedItem().toString().equals("[국가]")
                        && !s.getSelectedItem().toString().equals("[조리방식]")) {
                    switch (type) {
                        case 1:
                            n_food.taste.add(s.getSelectedItem().toString());
                            category_list_taste.add(s.getSelectedItem().toString());
                            addFlowChart(category_result_taste, category_list_taste.toArray(new String[category_list_taste.size()]));
                            break;
                        case 2:
                            n_food.country.add(s.getSelectedItem().toString());
                            category_list_country.add(s.getSelectedItem().toString());
                            addFlowChart(category_result_country, category_list_country.toArray(new String[category_list_country.size()]));
                            break;
                        case 3:
                            n_food.cooking.add(s.getSelectedItem().toString());
                            category_list_cooking.add(s.getSelectedItem().toString());
                            addFlowChart(category_result_cooking, category_list_cooking.toArray(new String[category_list_cooking.size()]));
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    private void set_rating() {
        ratingBar.setIsIndicator(false);
        ratingBar.setStepSize(0.5f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            //v : 4개가 채워지면 0.5점*8만큼 해서 점수가 v=4로 넘어 온다.
            public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {
                //5점 만점
                float st = 5f / ratingBar.getNumStars();
                rate_num = st * v;
            }
        });
    }

    private void addFlowChart(final TagFlowLayout mFlowLayout, String[] array) {
        final LayoutInflater mInflater = LayoutInflater.from(getApplication());

        mFlowLayout.setAdapter(new TagAdapter<String>(array){
            @Override
            public View getView(final FlowLayout parent, final int position, String s) {
                final TextView tv = (TextView) mInflater.inflate(R.layout.tag_result, mFlowLayout, false);
                tv.setText(s);

                int type = parent.getId();
                switch(type){
                    case R.id.category_result_taste:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                category_list_taste.remove(position);
                                mFlowLayout.removeViewAt(position);
                                n_food.taste.remove(position);
                                addFlowChart(category_result_taste, category_list_taste.toArray(new String[category_list_taste.size()]));
                            }
                        });
                        break;
                    case R.id.category_result_country:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                category_list_country.remove(position);
                                mFlowLayout.removeViewAt(position);
                                n_food.country.remove(position);
                                addFlowChart(category_result_country, category_list_country.toArray(new String[category_list_country.size()]));
                            }
                        });
                        break;
                    case R.id.category_result_cooking:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                category_list_cooking.remove(position);
                                mFlowLayout.removeViewAt(position);
                                n_food.cooking.remove(position);
                                addFlowChart(category_result_cooking, category_list_cooking.toArray(new String[category_list_cooking.size()]));
                            }
                        });
                        break;
                    case R.id.ingredient_result:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFlowLayout.removeViewAt(position);
                                n_food.ingredient.remove(position);
                                addFlowChart(ingredient_result,n_food.ingredient.toArray(new String[n_food.ingredient.size()]));
                            }
                        });
                }

                return tv;
            }

            @Override
            public boolean setSelected(int position, String s) {
                return s.equals("Android");
            }
        });

    }

    public void food_rate(Food food) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rateFood(food, SharedManager.getInstance().getMe()._id, food._id)
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
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}