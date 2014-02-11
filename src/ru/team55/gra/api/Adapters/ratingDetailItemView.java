package ru.team55.gra.api.Adapters;

import android.content.Context;
import android.widget.LinearLayout;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import ru.team55.gra.api.pocoObjects.pocoReportDetails;
import ru.team55.gra.rating.R;

@EViewGroup(R.layout.part_rating_detail_item)
public class ratingDetailItemView extends LinearLayout {

    //@ViewById android.widget.TextView textPokazateli_name;
    @ViewById android.widget.TextView textEstimation;
    @ViewById android.widget.TextView textPokazateli_comment;

    public ratingDetailItemView(Context context) {
        super(context);
    }

    public void bind(pocoReportDetails item) {
        //textPokazateli_name.setText(item.pokazateli_name);
        textEstimation.setText(Float.toString(item.estimation));
        textPokazateli_comment.setText(item.pokazateli_comment);
    }
}
