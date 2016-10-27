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
import android.widget.EditText;
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


public class PopupWithdrawal extends Activity {

    Button BT_yes;
    Button BT_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_withdrawal);

        BT_yes = (Button) findViewById(R.id.BT_yes);
        BT_no = (Button) findViewById(R.id.BT_no);

        BT_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.isWithdrawal = true;
                finish();
            }
        });
    }


}
