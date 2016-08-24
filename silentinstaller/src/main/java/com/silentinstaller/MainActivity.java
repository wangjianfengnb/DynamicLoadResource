package com.silentinstaller;

import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void install(View v) {
        autoInstallApk(this,new File("/data/app/autoinstall.apk"),"com.analysis","Analysis");
    }


    class MyPackageInstallObserver extends IPackageInstallObserver.Stub {
        Context ctx;
        String appname;
        String filename;
        String pkname;

        public MyPackageInstallObserver(Context context, String appname, String filename, String pkname) {
            this.ctx = context;
            this.appname = appname;
            this.filename = filename;
            this.pkname = pkname;
        }

        @Override
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            Log.i(TAG, "packageInstalled returnCode = " + returnCode);
            if (returnCode == 1) {
                //TODO install success
            }

        }
    }

    /**
     * 静默安装
     *
     * @param context
     * @param packageName
     * @param APPName
     */
    public void autoInstallApk(Context context, File file, String packageName, String APPName) {
        Log.i(TAG, "auto install apk packageName = " + packageName + ", fileName = " + file.getAbsolutePath());
        int installFlag = 0;
        if (!file.exists()) {
            //TODO file not exists
            Log.i(TAG,"file is not exists :" + file.getAbsolutePath());
            return;
        }
        installFlag |= PackageManager.INSTALL_REPLACE_EXISTING;  //
        /**
         * 在模拟器安装的时候老是返回 -18 ,通过查看PackageManager源码得出，这个码的意思是SDCARD不能安装应用。所以我这里去掉了
         */
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            installFlag |= PackageManager.INSTALL_EXTERNAL;
//        }
        try {
            PackageManager pm = context.getPackageManager();
            IPackageInstallObserver observer = new MyPackageInstallObserver(context, APPName, file.getAbsolutePath(), packageName);
            pm.installPackage(Uri.fromFile(file), observer, installFlag, packageName);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

}
