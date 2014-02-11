package ru.team55.gra.api.Adapters;



import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;
import ru.team55.gra.rating.R;


@EViewGroup(R.layout.dropdown_item_1line)
//@EViewGroup(resName = "simple_dropdown_item_1line")
public class SearchItemView extends LinearLayout {

    @ViewById(android.R.id.text1) TextView text1;

    public SearchItemView(Context context) {
        super(context);
    }

    public void bind(pocoSearchItem item) {
        text1.setText(item.name);
    }
}
