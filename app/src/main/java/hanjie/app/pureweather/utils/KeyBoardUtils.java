package hanjie.app.pureweather.utils;


import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtils {

    /**
     * 如果软键盘显示则收起软键盘
     *
     * @param activity Activity实例
     */
    public static void hintKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

}
