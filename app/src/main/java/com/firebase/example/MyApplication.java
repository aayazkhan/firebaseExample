package com.firebase.example;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class MyApplication extends Application {

    public static String tbl_USERS = "Users";
    public static String tbl_POSTS = "Posts";

    public static String tbl_POST_LIKE = "likes";


    public static String tbl_FOLLOWING = "Following";

    public static String FOLDER_POST_IMAGE = "post_image";
    public static String FOLDER_PROFILE_IMAGE = "profile_image";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FacebookSdk.setIsDebugEnabled(true);

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(this);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));

        Picasso picasso = builder.build();
        picasso.setIndicatorsEnabled(false);
        picasso.setLoggingEnabled(true);

        Picasso.setSingletonInstance(picasso);

    }
}
