package ru.team55.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class SymbolButton extends Button {

    public SymbolButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SymbolButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SymbolButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typefaces.get(getContext(), "iconfont1");
        setTypeface(tf);
    }


}


