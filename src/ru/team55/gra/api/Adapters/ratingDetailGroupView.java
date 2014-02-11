package ru.team55.gra.api.Adapters;

import android.content.Context;
import android.widget.LinearLayout;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import ru.team55.gra.api.pocoObjects.pocoReportItem;
import ru.team55.gra.rating.R;


@EViewGroup(R.layout.part_rating_detail_group)
public class ratingDetailGroupView extends LinearLayout {

    @ViewById android.widget.TextView textName;
    @ViewById android.widget.TextView textRating;

    public ratingDetailGroupView(Context context) {
        super(context);
    }

    public void bind(pocoReportItem item, int position) {

        textName.setText(item.name);

        float rating = 0;
        String rating_name = "";
        switch (position){
            case 0:
                rating = item.rating_d;
                rating_name = "Доступность";
                break;
            case 1:
                rating = item.rating_k;
                rating_name = "Качество";
                break;
            default:
                rating = item.rating_l;
                rating_name = "Лояльность";
        }

        textName.setText(rating_name);
        textRating.setText(Float.toString(rating));

    }
}
