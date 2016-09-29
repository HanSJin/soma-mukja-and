package com.hansjin.mukja_android.Utils.Connections;

import com.hansjin.mukja_android.Model.Food;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public interface CSConnection {
    @GET("/food")
    Observable<List<Food>> getAllFood();
}
