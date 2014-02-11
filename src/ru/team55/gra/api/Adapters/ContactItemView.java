package ru.team55.gra.api.Adapters;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import ru.team55.gra.rating.R;


@EViewGroup(R.layout.item_contact_chunk)
public class ContactItemView extends LinearLayout{

    @ViewById
    protected TextView text;

    @ViewById
    protected ImageView icon;


    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public String getText(){
        return text.getText().toString();
    }

    public void setTextAndIcon(Spannable text, int iconId){
        this.text.setText(text);
        this.icon.setImageResource(iconId);
    }


    public void setTextAndIcon(String text, int iconId){
        this.text.setText(text);
        this.icon.setImageResource(iconId);
    }

    public void setText(String text){
        this.text.setText(text);

    }


}
