package com.fededri.utils;

import android.util.Log;


/**
 * Created by Federico Torres on 21/10/2015.
 */
public class StringUtils {



    public static String capitalizeFirstChar(String input) {
        if (input.length() < 1) return null;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    public static String convertPointToComma(String input) {
        try {
            return input.replace(".",",");

        } catch (Exception e) {
            Log.e("Utils", "error al convertir punto en coma");
            e.printStackTrace();
        }

        return "";
    }


    public static String convertPointToComma(float input) {
        String string = String.valueOf(input);
        try {
            string  = string.replace(".",",");

        } catch (Exception e) {
            Log.e("Utils", "error al convertir punto en coma");
            e.printStackTrace();
        }
        return string;
    }


    public static String limitDoubleDecimals(Double n){
        return  StringUtils.convertPointToComma(String.format("%.2f",n));
    }



}

