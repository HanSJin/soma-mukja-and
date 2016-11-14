package com.hansjin.mukja_android.Utils.Connections;

import com.hansjin.mukja_android.Model.Category;
import com.hansjin.mukja_android.Model.Explore;
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

    @Multipart
    @POST("post/{food_id}/image/upload")
    Observable<Food> fileUploadWrite(@Path("food_id") String food_id,
                                     @Part("post_image\"; filename=\"android_post_image_file") RequestBody file);

    @GET("/{uid}/foods")
    Observable<List<Food>> getFoodsForUser(@Path("uid") String uid);

    @GET("food/{food_id}/{uid}/view")
    Observable<GlobalResponse> foodView(@Path("uid") String uid,
                                        @Path("food_id") String food_id);

    @GET("/users/{uid}/mylist")
    Observable<List<Food>> getLikedFood(@Path("uid") String uid);

    @GET("explore")
    Observable<List<Explore>> getExploreRanking();

    @GET("/like/{food_id}")
    Observable<List<User>> getLikedPerson(@Path("food_id") String food_id);

    @Multipart
    @POST("post/{user_id}/image/upload/profile")
    Observable<User> fileUploadWrite_User(@Path("user_id") String user_id,
                                     @Part("post_image\"; filename=\"android_post_image_file") RequestBody file);

    @POST("/users/{user_id}/edit/profile/facebook")
    Observable<User> updateUserImage_Facebook(@Path("user_id") String user_id,
                                   @Body Map<String, Object> fields);

    @GET("/users/{uid}/myinfo")
    Observable<User> getUserInfo(@Path("uid") String uid);

    @POST("/sign/in/NonFacebook")
    Observable<User> signinUser_NonFacebook(@Body Map<String, Object> fields);


    @POST("/user/withdrawal")
    Observable<User> withdrawalUser(@Body Map<String, Object> fields);


    //친구 요청 가져오기 //너가 친구 요청 //friends_NonFacebook_Waiting
    @GET("requests/waiting/{uid}/{page}")
    Observable<List<User>> getRequests(@Path("uid") String uid,
                                       @Path("page") int page);

    //친구 요청 가져오기 2 //내가 친구 요청 //friends_NonFacebook_Requested
    @GET("requests/requested/{uid}/{page}")
    Observable<List<User>> getRequests2(@Path("uid") String uid,
                                        @Path("page") int page);

    //친구 요청 가져오기 3 //우린 이미 친구 //friends_NonFacebook
    @GET("requests/friends/{uid}/{page}")
    Observable<List<User>> getRequests3(@Path("uid") String uid,
                                        @Path("page") int page);

    //친구수락 OK 정보 전송
    @POST("/friends/accept/{me_id}/{you_id}")
    Observable<User> acceptYou(@Body User You,
                               @Path("me_id") String me_id,
                               @Path("you_id") String you_id);

    //친구수락 NO 정보 전송
    @POST("/friends/reject/{me_id}/{you_id}")
    Observable<User> rejectYou(@Body User You,
                               @Path("me_id") String me_id,
                               @Path("you_id") String you_id);


    @GET("user/{you_id}/{me_id}/view")
    Observable<GlobalResponse> userView(@Path("me_id") String me_id,
                                        @Path("you_id") String you_id);

    @POST("/food/comment/{food_id}")
    Observable<GlobalResponse> commentFood(@Path("food_id") String food_id, @Body Map<String, Object> fields);

    @GET("/food/comment/get/{food_id}")
    Observable<List<Food.CommentPerson>> getCommentFood(@Path("food_id") String food_id);

    @GET("/food/comment/get/{food_id}/{comment_id}")
    Observable<List<Food.CommentPerson>> getOneCommentFood(@Path("food_id") String food_id,
                                                                 @Path("comment_id") String comment_id);


    @POST("/food/comment/{food_id}/{comment_id}")
    Observable<GlobalResponse> oneCommentFood(@Path("food_id") String food_id, @Path("comment_id") String comment_id, @Body Map<String, Object> fields);



}


