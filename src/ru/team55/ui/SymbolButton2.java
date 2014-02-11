package ru.team55.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class SymbolButton2 extends Button {

    public SymbolButton2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SymbolButton2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SymbolButton2(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typefaces.get(getContext(), "iconfont2");
        setTypeface(tf);
    }


}


