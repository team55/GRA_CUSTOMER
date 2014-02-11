package ru.team55.gra.rating;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.widget.ListView;
import com.googlecode.androidannotations.annotations.*;
import com.parse.ParseQueryAdapter;
import ru.team55.gra.api.Const;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;
import ru.team55.gra.api.users.UserHistory;

import java.util.List;

@Fullscreen
@EActivity(R.layout.activity_history)
public class History extends ActionBarActivity {

    @ViewById ListView listview;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }


    @AfterViews
    void bindData(){

        ParseQueryAdapter<UserHistory> adapter1 = new ParseQueryAdapter<UserHistory>(this, UserHistory.class);
        adapter1.setTextKey("displayName");

        adapter1.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<UserHistory>() {
            @Override
            public void onLoading() {
                startProgress();
            }

            @Override
            public void onLoaded(List<UserHistory> userHistories, Exception e) {
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
    void listviewItemClicked(UserHistory clickedItem){

        pocoSearchItem currA =  pocoSearchItem.Create(clickedItem.getActivityId(), clickedItem.getActivityName(), Const.PFX_ACTIVITY);

        model.currentSearch.clear();
        model.currentSearch.add(currA);
        model.currentActivityId = clickedItem.getActivityId();

        model.currentActivitySearch = currA;



        if(clickedItem.getAdressId()!=0){
            pocoSearchItem currAdress = pocoSearchItem.Create(clickedItem.getAdressId(), clickedItem.getAdressName(), Const.PFX_ADRESS);
            model.currentSearch.add(currAdress);
            model.currentPositionId = clickedItem.getAdressId();

            model.currentAddressSearch = currAdress;
        }

        setResult(RESULT_OK);
        finish();

    }


}