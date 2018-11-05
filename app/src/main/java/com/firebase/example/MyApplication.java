package com.firebase.example;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class MyApplication extends Application {

    public static String tbl_USERS = "Users";
    public static String tbl_POSTS = "Posts";

    public static String FOLDER_POST_IMAGE = "post_image";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));

        Picasso picasso = builder.build();
        picasso.setIndicatorsEnabled(false);
        picasso.setLoggingEnabled(true);

        Picasso.setSingletonInstance(picasso);

    }
}
