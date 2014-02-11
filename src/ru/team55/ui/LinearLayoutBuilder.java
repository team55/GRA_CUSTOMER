package ru.team55.ui;

import android.content.Context;
import android.widget.LinearLayout;

public class LinearLayoutBuilder {

    private LinearLayoutBuilder(){}

    private LinearLayoutBuilder(Context context){
        ll = new LinearLayout(context);
        withWrapContent();//по дефолту
    }

    public static LinearLayoutBuilder Create(Context context){
        LinearLayoutBuilder vlb = new LinearLayoutBuilder(context);
        return vlb;
    }

    LinearLayout ll = null;

    public LinearLayoutBuilder VerticalLayoutBuilder(){


        return this;
    }

    public LinearLayoutBuilder withOrientation(int orientation){
        ll.setOrientation(orientation);
        return this;
    }

    public LinearLayoutBuilder withMatchParent(){

        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT) );
        return this;
    }

    public LinearLayoutBuilder withMatchWidth(){

        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) );
        return this;
    }

    public LinearLayoutBuilder withMatchHeight(){
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT) );

        return this;
    }

    public LinearLayoutBuilder withWrapContent(){
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) );
        return this;
    }

    public LinearLayout Build(){
        return ll;
    }

}
