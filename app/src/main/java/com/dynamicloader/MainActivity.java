package com.dynamicloader;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    TextView textV;
    ImageView imgV;
    DexClassLoader classLoader;
    String filePath;
    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String fileDir = getCacheDir().getAbsolutePath();
        filePath = fileDir + File.separator + "resource.apk";  //源dex/jar/apk 目录
        String fileRelease = getDir("dex", MODE_PRIVATE).getAbsolutePath();  //存放解压出来的dex文件的目录

        //初始化classloader
        classLoader = new DexClassLoader(filePath, fileRelease, null, getClassLoader());

        Log.i("Loader", "filePath = " + filePath);
        Log.i("Loader", "isExists = " + new File(filePath).exists());
        textV = (TextView) findViewById(R.id.textView);
        imgV = (ImageView) findViewById(R.id.imageView);

        //点击的时候从apk包中获取背景颜色，和图标进行显示
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadResource(filePath);

                setContent();
            }
        });

    }


    private void setContent() {
        Class clazz = null;
        try {
            clazz = classLoader.loadClass("com.example.resourceloaderapk.UIUtil");
            //设置文字
            Method method = clazz.getMethod("getTextString", Context.class);
            String str = (String) method.invoke(null, this);
            textV.setText(str);
            //设置背景
            method = clazz.getMethod("getTextBackgroundId", Context.class);
            int color = (int) method.invoke(null, this);
            Log.i("Loader","color = " + color);
            textV.setBackgroundColor(color);
            //设置图片
            method = clazz.getMethod("getImageDrawable", Context.class);
            Drawable drawable = (Drawable) method.invoke(null, this);
            Log.i("Loader","drawable =" + drawable);
            imgV.setImageDrawable(drawable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 此方法的作用是把resource.apk中的资源加载到AssetManager中，
     * 然后在重组一个Resources对象，这个Resources对象包括了resource.apk中的资源。
     *
     * resource.apk 中是使用Context.getResources()获得Resource对象的，
     * 所以还要重写一些getResources()方法，返回该Resources对象
     *
     * @param dexPath
     */
    protected void loadResource(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = assetManager.getClass().getMethod("addAssetPath",String.class);
            method.invoke(assetManager,dexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Resources resource =  getResources();
//        resource.getConfiguration();
//        resource.getDisplayMetrics();

        mResources = new Resources(mAssetManager,resource.getDisplayMetrics(),resource.getConfiguration());

        mTheme = mResources.newTheme();
        mTheme.setTo(getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
