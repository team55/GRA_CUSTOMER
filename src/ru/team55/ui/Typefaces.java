package ru.team55.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

public class Typefaces {

    private static final String TAG = Typefaces.class.getSimpleName();

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String assetPath) {

        //"fonts/AndroidClock.ttf"

        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {

                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),String.format("fonts/%s.ttf",assetPath));
                    cache.put(assetPath, t);
                } catch (Exception e) {

                    Log.e(TAG, "1. Could not get typeface '" + assetPath + "' because " + e.getMessage());

                    try {
                        Typeface t = Typeface.create(assetPath, Typeface.NORMAL);
                        cache.put(assetPath, t);
                    } catch (Exception e2) {
                        Log.e(TAG, "2. Could not get typeface '" + assetPath + "' because " + e2.getMessage());
                        return null;
                    }

                    //return null;
                }

            }
            return cache.get(assetPath);
        }
    }
}