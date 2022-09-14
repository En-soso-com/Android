// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package com.example.pytorchandroid.objectdetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pytorchandroid.HomeActivity;

public class BaseModuleActivity extends AppCompatActivity {
    protected HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;
    protected Handler mUIHandler;
    protected String modelType;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        modelType = intent.getStringExtra("modelType");
        mUIHandler = new Handler(getMainLooper());
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startBackgroundThread();
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("ModuleActivity");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    protected void onDestroy() {
        stopBackgroundThread();
        super.onDestroy();

        if(HomeActivity.textToSpeech != null) {
            HomeActivity.textToSpeech.stop();
        }
    }

    protected void stopBackgroundThread() {
      mBackgroundThread.quitSafely();
      try {
          mBackgroundThread.join();
          mBackgroundThread = null;
          mBackgroundHandler = null;
      } catch (InterruptedException e) {
          Log.e("Object Detection", "Error on stopping background thread", e);
      }
    }
}
