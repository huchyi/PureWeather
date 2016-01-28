package hanjie.app.pureweather.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.transition.Explode;
import android.view.Window;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.activities.SettingsActivity;

public class AnimationUtils {
    public static void setExplodeEnterTransition(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(activity.getString(R.string.config), Context.MODE_PRIVATE);
        if (sp.getBoolean(activity.getString(R.string.transition_animation), SettingsActivity.TRANSITION_ANIMATION_DEFAULT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
                activity.getWindow().setEnterTransition(new Explode());
            }
        }
    }

    public static void startActivityWithExplodeAnimation(Activity activity, Intent activityIntent) {
        SharedPreferences sp = activity.getSharedPreferences(activity.getString(R.string.config), Context.MODE_PRIVATE);
        if (sp.getBoolean(activity.getString(R.string.transition_animation), SettingsActivity.TRANSITION_ANIMATION_DEFAULT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.startActivity(activityIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            } else {
                activity.startActivity(activityIntent);
            }
        } else {
            activity.startActivity(activityIntent);
        }
    }

}
