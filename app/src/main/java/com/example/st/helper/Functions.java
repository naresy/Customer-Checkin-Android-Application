package com.example.st.helper;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Functions {

    //Main URL
    private static String MAIN_URL = "https://sandythreading.com/backend/";

    // Login URL
    public static String LOGIN_URL = MAIN_URL + "login_user.php";

    // Register URL
    public static String REGISTER_URL = MAIN_URL + "register_user.php";

    public static String Online_book=MAIN_URL+ "online_book.php";

    // OTP Verification


    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

    /**
     *  Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     *  Hide Keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);

        } catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }

}
