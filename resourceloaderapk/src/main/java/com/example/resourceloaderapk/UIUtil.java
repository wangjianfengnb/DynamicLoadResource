package com.example.resourceloaderapk;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Jam on 16-8-22
 * Description:
 */
public class UIUtil {


    public static String getTextString(Context ctx){
        return ctx.getResources().getString(R.string.text);
    }

    public static Drawable getImageDrawable(Context ctx){
        return ctx.getResources().getDrawable(R.mipmap.ic_launcher);
    }

    public static int getTextBackgroundId(Context ctx){
        return ctx.getResources().getColor(R.color.color_green);
    }


}
