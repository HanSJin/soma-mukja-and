package com.hansjin.mukja_android.Utils.Connections;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.GlobalResponse;
import com.hansjin.mukja_android.Model.Result;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.Model.itemScores;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public interface CSConnection {
    @GET("/foods")
    Observable<List<Food>> getAllFood();

    @GET("/foods/{id}/{user}")
    Observable<Food> getOneFood(@Path("id") String id,
                                @Path("user") String user);

    @POST("/users")
    Observable<User> createUser(@Body User user);

    @GET("/pio/create_items")
    Observable<Result> createAllItems();

    @GET("/pio/buy/{user}/{food}")
    Observable<Result> buyItem(@Path("user") String user,
                               @Path("food") String food);

    /* 이전 ver
    @GET("/pio/similar/{food}")
    Observable<List<itemScores>> similarResult(@Path("food") String food);

    @GET("/pio/recommendation/{user}")
    Observable<List<itemScores>> recommendationResult(@Path("user") String user);
    */

    //미정 - 후에 api확실해지면 연동 후 수정
    //먹고싶어요
    @GET("/like/{uid}/{food_id}")
    Observable<Food> likeFood(@Path("uid") String uid,
                              @Path("food_id") String food_id);

    //먹고싶어요 취소
    @GET("/like/{event_id}")
    Observable<Food> likeCancle(@Path("event_id") String event_id);

    //rate점수 전송
    @GET("/rate/{uid}/{food_id}")
    Observable<Food> rateFood(@Path("uid") String uid,
                              @Path("food_id") String food_id);

    //similar 결과 값
    @GET("food/{food_id}/similar")
    Observable<List<Food>> similarResult(@Path("food_id") String food_id);

    //recommendation 결과 값
    @POST("/recommand/{uid}")
    Observable<List<Food>> recommendationResult(@Path("uid") String uid,
                                                @Body Map<String, Object> fields);


    //피드 가져오기
    @GET("feeds/{uid}/{page}")
    Observable<List<Food>> getFeedList(@Path("uid") String uid,
                                       @Path("page") int page);

    //신고하기
    @GET("report/{uid}/{food_id}")
    Observable<GlobalResponse> reportFood(@Path("uid") String uid,
                                          @Path("food_id") String food_id);

    //taste 목록 가져오기
    @GET("/food/taste")
    Observable<List<String>> getTasteList();

    //country 목록 가져오기
    @GET("/food/country")
    Observable<List<String>> getCountryList();

    //cooking 목록 가져오기
    @GET("/food/cooking")
    Observable<List<String>> getCookingList();

    //food upload ( 이거 docs api랑 다르게 했어요! id는 자동생성 되어야 할 것 같아서 )
    @POST("/food")
    Observable<Food> foodPost(@Body Food food);

    @Multipart
    @POST("upload/{image_url}")
    Call<ResponseBody> uploadImage(@Part("photo") MultipartBody.Part photo,
                                   @Part("name") RequestBody name,
                                   @Path("image_url") String food_id);

    @POST("/sign/up")
    Observable<User> signupUser(@Body User user);

    @POST("/users/{user_id}/edit/aboutMe")
    Observable<User> updateAboutme(@Path("user_id") String user_id,
                                   @Body Map<String, Object> fields);

    @POST("/sign/in")
    Observable<User> signinUser(@Body Map<String, Object> fields);

    @GET("/users/{user_id}/keyword")
    Observable<List<String>> getAllKeyword(@Path("user_id") String id);

    @GET("/foods/{keyword}")
    Observable<List<Food>> getSearchResult(@Path("keyword") String keyword);

    @GET("/{uid}/foods")
    Observable<List<Food>> getFoodsForUser(@Path("uid") String uid);




}

