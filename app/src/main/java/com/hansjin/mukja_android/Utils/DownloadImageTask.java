package com.hansjin.mukja_android.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.hansjin.mukja_android.R;

import java.io.InputStream;
import java.net.URL;

import static com.hansjin.mukja_android.Sign.SignActivity.context;

/**
 * Created by mac on 2016. 10. 15..
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;

        try {
            URL url = new URL(urldisplay);
            InputStream in = url.openConnection().getInputStream();
            mIcon11 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(result==null){
//            bmImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.backgroundimage1));
        }else {
            bmImage.setImageBitmap(result);
        }
    }
}