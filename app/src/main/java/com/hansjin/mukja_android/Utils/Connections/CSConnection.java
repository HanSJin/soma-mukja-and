package com.hansjin.mukja_android.Utils.Connections;

import com.hansjin.mukja_android.Model.Category;
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

    @GET("/pio/buy/{user}/{food}")
    Observable<Result> buyItem(@Path("user") String user,
                               @Path("food") String food);

    //먹고싶어요
    @POST("/like/{uid}/{food_id}")
    Observable<Food> likeFood(@Path("uid") String uid,
                              @Path("food_id") String food_id);

    //먹고싶어요 취소
//    @GET("/like/{event_id}")
//    Observable<Food> likeCancle(@Path("event_id") String event_id);

    //rate점수 전송
    @POST("/rate/{uid}/{food_id}")
    Observable<Food> rateFood(@Body Food food,
                              @Path("uid") String uid,
                              @Path("food_id") String food_id);

    //similar 결과 값
    @GET("food/{food_id}/similar")
    Observable<List<Food>> similarResult(@Path("food_id") String food_id);

    //recommendation 결과 값
    @POST("/recommand/{uid}")
    Observable<List<Food>> recommendationResult(@Path("uid") String uid,
                                                @Body Category fields);


    //피드 가져오기
    @GET("feeds/{uid}/{page}")
    Observable<List<Food>> getFeedList(@Path("uid") String uid,
                                       @Path("page") int page);

    //신고하기
    @GET("report/{uid}/{food_id}")
    Observable<GlobalResponse> reportFood(@Path("uid") String uid,
                                          @Path("food_id") String food_id);

    //카테고리 목록 가져오기
    @GET("/category")
    Observable<Category> getCategoryList();

    @POST("/food/post")
    Observable<Food> foodPost(@Body Food food);

    @Multipart
    @POST("upload/food/{image_url}")
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

<<<<<<< HEAD

    @Multipart
    @POST("post/{food_id}/image/upload")
    Observable<Food> fileUploadWrite(@Path("food_id") String food_id,
                                     @Part("post_image\"; filename=\"android_post_image_file") RequestBody file);
=======
    @GET("/{uid}/foods")
    Observable<List<Food>> getFoodsForUser(@Path("uid") String uid);


>>>>>>> 48b47ef88b236c1327365724d203c6004f8f405a


}

