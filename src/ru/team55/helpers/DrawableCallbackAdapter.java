package ru.team55.helpers;


import android.graphics.drawable.Drawable;

public abstract class DrawableCallbackAdapter implements Drawable.Callback {

    @Override
    public void invalidateDrawable(Drawable who) {}

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {}

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {}

}
