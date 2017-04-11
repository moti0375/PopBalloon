package com.bartovapps.popballoon.utils;

import android.content.Context;
import android.util.TypedValue;

import static android.R.attr.x;

/**
 * Created by motibartov on 09/04/2017.
 */

public class PixelHelper {


    public static int pixelToDp(int px, Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());

    }

}
