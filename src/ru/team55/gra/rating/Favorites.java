package ru.team55.gra.rating;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import com.googlecode.androidannotations.annotations.*;
import com.parse.ParseQueryAdapter;
import ru.team55.gra.api.Const;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoObjects.pocoReportItem;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;
import ru.team55.gra.api.svcReports;
import ru.team55.gra.api.svcReports_;
import ru.team55.gra.api.users.UserFavorites;

import java.util.List;

@Fullscreen
@EActivity(R.layout.activity_history)
public class Favorites extends ActionBarActivity {

    public static final String TAG = Favorites.class.getSimpleName();

    @ViewById
    ListView listview;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }


    @AfterViews
    void bindData(){

        ParseQueryAdapter<UserFavorites> adapter1 = new ParseQueryAdapter<UserFavorites>(this, UserFavorites.class);
        adapter1.setTextKey("displayName");


        adapter1.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<UserFavorites>() {
            @Override
            public void onLoading() {
                startProgress();
            }

            @Override
            public void onLoaded(List<UserFavorites> userFavorites, Exception e) {
                stopProgress();
            }
        });

        listview.setAdapter(adapter1);  //подозрение на ACL - список пуст..
    }




    @UiThread
    public void startProgress() {
        setSupportProgressBarIndeterminateVisibility(true);
    }


    @UiThread
    public void stopProgress() {
        setSupportProgressBarIndeterminateVisibility(false);
    }


    @ItemClick
    void listviewItemClicked(UserFavorites clickedItem){

        //установить поиск по активности
        //выполнить запрос по орагнизации - запросить репорт
        //открыть подробную инфоомацию

        pocoSearchItem currSearch = pocoSearchItem.Create(clickedItem.getActivityId(), clickedItem.getActivityName(), Const.PFX_ACTIVITY);

        model.currentSearch.clear();
        model.currentSearch.add(currSearch);

        model.currentActivityId = clickedItem.getActivityId();
        model.currentActivitySearch = currSearch;

        //бакграундная ботва по получению данных фирмы
        //в каллбаке которой закрытие активити?
        //вроде нет - подробное инфо надо перейти - старт активити
        getReports(clickedItem.getSupplierId(), clickedItem.getActivityId());

    }

    svcReports reports = new svcReports_();

    @Background
    void getReports(int pointSalesMan, int activity_id){
        Log.w(TAG, "start");
        startProgress();

        reports.setRootUrl(model.leafURL);
        pocoReportItem[] items = reports.getInfo(pointSalesMan, activity_id);
        //как поведет на нескольких?
        model.currentReport = items[0];

        stopProgress();
        Log.w(TAG, "start");

        showReport();
    }

    @UiThread
    void showReport(){
        startActivity(new Intent(this, pageDetails_.class) );
    }





}