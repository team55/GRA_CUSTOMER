package ru.team55.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SymbolTextView  extends TextView {

    public SymbolTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SymbolTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SymbolTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/iconfont1.ttf");
        Typeface tf = Typefaces.get(getContext(), "iconfont1");
        setTypeface(tf);
    }

}