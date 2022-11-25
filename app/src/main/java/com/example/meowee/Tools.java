package com.example.meowee;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean emailPatternValidate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean checkValidPassword(String password) {
        return !password.contains(" ") && password.length() >= 6;
    }

    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, context.getResources().getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public static boolean checkValidName(String name) {
        String[] tokens = name.split("\\s+");
        return tokens.length >= 1;
    }

    public static boolean checkValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[0-9]+")
                && (phoneNumber.length() == 10 || phoneNumber.length() == 11);
    }
}
