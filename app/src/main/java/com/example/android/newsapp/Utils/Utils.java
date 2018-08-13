package com.example.android.newsapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class Utils {
    //Hides Keyboard
    public static void hideKeyboard(Activity MainActivity) {
        //Closing the softkeyboard
        InputMethodManager inputManager = (InputMethodManager)
                MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        try { //Hiden when search button is clicked
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(MainActivity.getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e("Keyboard Not found", e.toString());
        }
        MainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Hidden during onCreate
    }
}
