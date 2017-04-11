package com.bartovapps.popballoon.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by motibartov on 11/04/2017.
 */

public class HighScoreManager {

    private static final String PREFS_GLOBAL = "prefs_global";
    private static final String PREFS_HIGH_SCORE = "prefs_high_score";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFS_GLOBAL, context.MODE_PRIVATE);
    }

    public static boolean isHighScore(Context context, int newScore){
        int highScore = getSharedPreferences(context).getInt(PREFS_HIGH_SCORE, 0);
        return newScore > highScore;
    }

    public static int getHighScore(Context context){
        return getSharedPreferences(context).getInt(PREFS_HIGH_SCORE, 0);
    }

    public static void setHighScore(Context context, int newScore){
        if (isHighScore(context, newScore)) {
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            editor.putInt(PREFS_HIGH_SCORE, newScore);
            editor.apply();
        }
    }


}
