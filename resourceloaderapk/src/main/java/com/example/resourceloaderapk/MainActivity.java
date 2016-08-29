package com.example.resourceloaderapk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Resource_MainActivity";
    private static View parentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i(TAG, "layoutId = " + R.layout.activity_main);
//        setContentView(R.layout.activity_main);
        if(parentView == null){
            setContentView(R.layout.activity_main);
        }else{
            setContentView(parentView);
        }
    }

    public static void setLayoutView(View view){
        parentView = view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "resource activity onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "resource activity onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "resource activity onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "resource activity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "resource activity onDestroy");
    }


}
