package com.dynamicloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by Jam on 16-8-25
 * Description:
 */
public class LoadActivity extends BaseActivity {
    public static final String TAG = "LoadActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    public void replaceLoadedApk(View v) {
        try {
            //通过替换LoadedApk中的mClassLoader来达到加载apk中的Activity
            String fileDir = getCacheDir().getAbsolutePath();
            String filePath = fileDir + File.separator + Constants.RESOURCE_APK_NAME;  //源dex/jar/apk 目录
            DexClassLoader loader = new DexClassLoader(filePath, getDir("dex", MODE_PRIVATE).getAbsolutePath(), null, getClassLoader());

            Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[]{}, new Object[]{});
            String packageName = getPackageName();

            //通过反射获取ActivityThread的 mPackages 对象
            ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mPackages");
            //通过反射获mPackages获得当前的LoadedApk对象
            WeakReference wr = (WeakReference) mPackages.get(packageName);
            Log.i(TAG, "wr = " + wr.get());
            //替换LoadedApk中的mClassLoader 为我们自己的DexClassLoader
            RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader", wr.get(), loader);
            Log.i(TAG, "classloader = " + loader);


            startResourceActivity(filePath, loader);


        } catch (Exception e) {
            Log.i(TAG, "load apk error :" + Log.getStackTraceString(e));
        }
    }

    /**
     * 启动插件Activity
     * @param filePath
     * @param loader
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private void startResourceActivity(String filePath, ClassLoader loader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //加载资源
        loadResources(filePath);


        //加载Activity ,确保这里的类名和Constants.RESOURCE_APK_NAME 中的 类名相同
        Class clazz = loader.loadClass("com.example.resourceloaderapk.MainActivity");
        //找到R.layout.activity_main
        Class rClazz = loader.loadClass("com.example.resourceloaderapk.R$layout");
        Field field = rClazz.getField("activity_main");
        Integer ojb = (Integer)field.get(null);

        View view = LayoutInflater.from(this).inflate(ojb, null);
        //设置静态变量。这里为什么要设置静态变量呢。
        // 因为测试发现setContentView() 没有起作用。
        // 所以在启动Activity之前保存一个静态的View，设置到Activity中

        Method method = clazz.getMethod("setLayoutView", View.class);
        method.invoke(null, view);

        //找到MainActivity,然后启动
        startActivity(new Intent(this, clazz));
    }

    public void injectDexElements(View v){
        Log.i(TAG,"this classloader = " + getClassLoader());
        PathClassLoader pathClassLoader = (PathClassLoader) getClassLoader();
        String fileDir = getCacheDir().getAbsolutePath();
        String filePath = fileDir + File.separator + Constants.RESOURCE_APK_NAME;  //源dex/jar/apk 目录
        DexClassLoader loader = new DexClassLoader(filePath, getDir("dex", MODE_PRIVATE).getAbsolutePath(), null, getClassLoader());
        try {
            //把PathClassLoader和DexClassLoader的pathList对象中的 dexElements 合并
            Object dexElements = combineArray(
                    getDexElements(getPathList(pathClassLoader)),
                    getDexElements(getPathList(loader)));
            //把合并后的dexElements设置到PathClassLoader的 pathList对象中的 dexElements
            Object pathList = getPathList(pathClassLoader);
            setField(pathList, pathList.getClass(), "dexElements", dexElements);

            startResourceActivity(filePath,pathClassLoader);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private static Object getPathList(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        ClassLoader bc = (ClassLoader)baseDexClassLoader;
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class<?> cl, String field)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    private static Object getDexElements(Object paramObject)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }
    private static void setField(Object obj, Class<?> cl, String field,
                                 Object value) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }

}
