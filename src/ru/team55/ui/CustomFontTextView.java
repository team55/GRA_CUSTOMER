package ru.team55.ui;



import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class CustomFontTextView extends TextView {

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFontTextView(Context context) {
        super(context);
        init();
    }

    private void init() {

        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/AndroidClock.ttf");
        //Typeface tf = Typeface.create("AndroidClock",Typeface.BOLD);

        Typeface tf = Typefaces.get(getContext(), "AndroidClock");
        setTypeface(tf);
    }

}