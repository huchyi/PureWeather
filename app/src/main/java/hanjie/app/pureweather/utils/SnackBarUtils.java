package hanjie.app.pureweather.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class SnackBarUtils {

    /**
     * 自定义SnackBar颜色
     *
     * @param snackbar        SnackBar实例
     * @param backgroundColor 背景颜色
     * @param textColor       文本颜色
     * @param actionTextColor action文本颜色,0表示没有添加action文本
     */
    public static void customSnackBar(Snackbar snackbar, int backgroundColor, int textColor, int actionTextColor) {
        View view = snackbar.getView();
        view.setBackgroundColor(backgroundColor);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
        if (actionTextColor != 0) {
            ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(actionTextColor);
        }
    }

}
