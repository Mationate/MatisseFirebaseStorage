package com.desafiolatam.matisseandfirebasestorage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.List;

public class MainActivity extends AppCompatActivity implements UploadPhoto.Callback {

    private static final int RC_GALLERY = 322;
    private static final int RC_CHOOSE = 343;




    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, RC_GALLERY);
        } else {
            selectPhotos();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotos();

            }
        });
    }

    private void selectPhotos() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9)
                .gridExpectedSize(200)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(RC_CHOOSE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (RC_GALLERY == requestCode) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                selectPhotos();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RC_CHOOSE == requestCode) {
            if (RESULT_OK == resultCode) {
                List<Uri> selections = Matisse.obtainResult(data);
                for (Uri uri : selections) {
                    Log.d("PHOTO", uri.toString());
                }
                new UploadPhoto(this).toFirebase(selections);



                /*ImageView imageView = findViewById(R.id.urlIv);
                Picasso.with(this).load("file://" + selections.get(0)).fit().centerCrop().into(imageView);*/
            }
        }
    }


    @Override
    public void progress(int progress) {
        Toast.makeText(this, String.valueOf(progress), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void done() {
        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
    }
}
