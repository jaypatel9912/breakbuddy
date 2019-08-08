package com.breakbuddy.UI;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;


public class Globals extends Application {


    private static final String DEFAULT_SANS_HEAVY = "SanFranciscoText-Heavy.otf";
    private static final String DEFAULT_SANS_BOLD = "SanFranciscoText-Bold.otf";
    private static final String DEFAULT_SANS_REGULAR = "SanFranciscoText-Regular.otf";
    private static final String DEFAULT_SANS_MEDIUM = "SanFranciscoText-Medium.otf";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            setDefaultFonts();
        } catch (Exception e) {
            logFontError(e);
        }
    }

    private void setDefaultFonts() {
        try {
            final Typeface regular = Typeface.createFromAsset(getAssets(), DEFAULT_SANS_REGULAR);
            final Typeface italic = Typeface.createFromAsset(getAssets(), DEFAULT_SANS_MEDIUM);
            final Typeface bold = Typeface.createFromAsset(getAssets(), DEFAULT_SANS_BOLD);
            final Typeface boldItalic = Typeface.createFromAsset(getAssets(), DEFAULT_SANS_HEAVY);

            Field DEFAULT = Typeface.class.getDeclaredField("DEFAULT");
            DEFAULT.setAccessible(true);
            DEFAULT.set(null, regular);

            Field DEFAULT_BOLD = Typeface.class.getDeclaredField("DEFAULT_BOLD");
            DEFAULT_BOLD.setAccessible(true);
            DEFAULT_BOLD.set(null, bold);

            Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
            sDefaults.setAccessible(true);
            sDefaults.set(null, new Typeface[]{
                    regular, bold, italic, boldItalic
            });

        } catch (NoSuchFieldException e) {
            logFontError(e);
        } catch (IllegalAccessException e) {
            logFontError(e);
        } catch (Throwable e) {
            //cannot crash app if there is a failure with overriding the default font!
            logFontError(e);
        }
    }

    private void logFontError(Throwable e) {
        Log.e("font_override", "Error overriding font", e);
    }
}
