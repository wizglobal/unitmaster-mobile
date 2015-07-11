package com.wizglobal.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.wizglobal.app.ChangePasswordActivity;
import com.wizglobal.app.LoginActivity;
import com.wizglobal.app.MainActivity;

/**
 * Created by tiberius on 6/20/2014.
 */
public class Helper {

    public static  boolean isLoggedIn(Context context) {
        boolean res = false;
        SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
        String memberNo = pref.getString("member", null);
        if (memberNo != null) {
            res = true;
        }
        return res;
    }

    public static void logout(Context context) {
        SharedPreferences pref = context.getSharedPreferences("WizGlobalPreferences", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void changePin(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    public static boolean isValidName(String name) {
        boolean res = false;
        if (name.matches("[a-zA-Z][a-zA-Z ]+") && name.length() > 2) {
            res = true;
        }
        return res;
    }

    public static boolean isValidOName(String name) {
        boolean res = false;
        if (name.isEmpty()) {
            res = true;
        } else if (name.matches("[a-zA-Z][a-zA-Z ]+") && name.length() > 2) {
            res = true;
        }
        return res;
    }

    public static boolean isValidPhone(String phone) {
        boolean res = false;
        if (phone.matches("[0-9]+") && phone.length() > 9) {
            res = true;
        }
        return res;
    }

    public static boolean isValidIdNo(String idNo) {
        boolean res = false;
        if (idNo.matches("[0-9]+") && idNo.length() >= 7) {
            res = true;
        }
        return res;
    }

    public static boolean isValidEmail(String email) {
        boolean res = false;
        if (email.matches("/^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?$/i")) {
            res = true;
        }
        return res;
    }
}
