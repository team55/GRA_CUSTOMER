package ru.team55.gra.api.Adapters;



import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import ru.team55.gra.api.Const;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoObjects.pocoOperatingMode;
import ru.team55.gra.api.pocoObjects.pocoReportInfo;
import ru.team55.gra.api.pocoObjects.pocoReportItem;
import ru.team55.gra.rating.R;
import ru.team55.gra.rating.pageDetails_;
import ru.team55.gra.rating.pageOrders_;


import java.util.Calendar;



@EViewGroup(R.layout.item_report)
//@EViewGroup(resName = "item_report") //так если в библиотеке
public class ReportItemView extends LinearLayout {

    Calendar calendar;

    @ViewById TextView textName;
    @ViewById TextView textMap;
    @ViewById TextView textDistance;
    @ViewById TextView textRating;
    @ViewById TextView textDate;
    @ViewById ImageView star;

    @ViewById Button btnMore;
    @ViewById Button btnOrder;

    @ViewById LinearLayout containerDistance;


    public ReportItemView(Context context) {
        super(context);
        calendar = Calendar.getInstance();
    }

    public void bind(final pocoReportItem item) {
        textName.setText(item.name);

        //TODO: фидимость лайаутов


        for(pocoReportInfo info:item.information){
            if(info.name.equalsIgnoreCase(Const.ID_ADRESS))  textMap.setText(info.value);
            //if(info.name.equalsIgnoreCase(Const.ID_SITE))    textTags.setText(info.value);
        }
        for(pocoOperatingMode info:item.operating_modes){
            if(info.day_week_integer==0){
                textDate.setText(String.format("%s %s",info.day_week_string, info.operating_mode ));
            }
        }

        if(item.distance>0){
            containerDistance.setVisibility(View.VISIBLE);
            textDistance.setText("Дистанция: "+item.distance+" м");
        }
        else{
            containerDistance.setVisibility(View.GONE);
        }


/*
        //Установка рейтинга
        if(item.rating_d>0 && item.rating_k>0 && item.rating_l>0)
            star.setImageDrawable(getResources().getDrawable(R.drawable.star_rate_orange));
        else
            star.setImageDrawable(getResources().getDrawable(R.drawable.star_rate_gray));



        textRating.setText(
                String.format("Рейтинг: %s ", item.rating)+
                        String.format("Место: %s/%s", item.totalRank, item.count_arr)
        );
*/


        float total = item.rating_d+item.rating_k+item.rating_l;
        if(total>0){
            star.setImageDrawable(getResources().getDrawable(R.drawable.star_rate_orange));
            textRating.setText(
                    String.format("Рейтинг: %s ", Math.round(item.rating))+
                            String.format("Место: %s/%s", item.totalRank, item.count_arr)
            );

        }else{
            star.setImageDrawable(getResources().getDrawable(R.drawable.star_rate_gray));
            textRating.setText("Оценки нет. "+String.format("Место: %s/%s", item.totalRank, item.count_arr));
        }


        btnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                model.currentReport = item;
                getContext().startActivity( new Intent(getContext(), pageDetails_.class) );

            }
        });

        btnOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                model.currentReport = item;
                getContext().startActivity(new Intent(getContext(), pageOrders_.class));
            }
        });

    }
}
