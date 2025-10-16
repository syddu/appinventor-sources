package com.google.appinventor.components.runtime.util;

import android.app.Activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import com.google.appinventor.components.runtime.Player;

public class FroyoUtil {
    public static void setOnCompletionListener(MediaPlayer mp, Object listener) {
        //TODO(lroman10): Real implementation
    }

    public static AudioManager setAudioManager(Activity activity) {
        //TODO(lroman10): Real implementation
      return null;
    }

    public static Object setAudioFocusChangeListener(Player player) {
        //TODO(lroman10): Real implementation
      return null;
    }

    public static boolean focusRequestGranted(AudioManager audioManager, Object listener) {
        return true; //TODO(lroman10): Real implementation
    }

    public static void abandonFocus(AudioManager audioManager, Object listener) {
        //TODO(lroman10): Real implementation
    }
}
