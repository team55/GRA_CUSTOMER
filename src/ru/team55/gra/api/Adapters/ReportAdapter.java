package ru.team55.gra.api.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.rest.RestService;
import ru.team55.gra.api.*;
import ru.team55.gra.api.pocoObjects.pocoReportItem;


import java.util.ArrayList;


@EBean
public class ReportAdapter  extends ArrayAdapter<pocoReportItem> {

    private static final String TAG = ReportAdapter.class.getSimpleName();
    private ArrayList<pocoReportItem> mData;

    @RestService
    svcReports reportClient;


    //интерфейс
    //@Bean(InMemoryPersonFinder.class)
    //PersonFinder personFinder;

    @RootContext
    Context context;

    BackgroundTaskCallback _callback;

    public ReportAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line); //fake
    }


    @AfterInject
    void initAdapter() {
        mData = new ArrayList<pocoReportItem>();
    }

    public void setProgressCallback(BackgroundTaskCallback callback){
        _callback = callback;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public pocoReportItem getItem(int index) {
        return mData.get(index);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    //TODO: калбак в форму о завершении (или в конструкторе сразу !!)

    @Background
    public void startSearch(int currentPage){

        Log.w(TAG, "Start report ");

        if(currentPage==1)
            BeginData();
        else
            AppendData();

        reportClient.setRootUrl(model.leafURL);
        pocoReportItem[] items;

        try{

            if(model.currentPositionId==0){
                items = reportClient.getRatings(
                        model.currentActivityId,
                        currentPage);
            }
            else{
                items = reportClient.getRatingsByTerritory(
                        model.currentActivityId,
                        model.currentPositionId,
                        currentPage);
            }

            for (pocoReportItem item: items){
                mData.add(item);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        CommitData();


    }

    @UiThread
    void BeginData(){
        mData.clear();
        _callback.startProgress();
    }

    @UiThread
    void AppendData(){
        _callback.startProgress();
    }

    @UiThread
    void CommitData(){

        if(mData != null && mData.size() > 0) {
            notifyDataSetChanged();
        }
        else {
            notifyDataSetInvalidated();
        }

        _callback.stopProgress(BackgroundTaskType.Search);
    }


    public void Reset(){
        mData.clear();
        notifyDataSetChanged();
    }

    // через биндер
    //             https://github.com/excilys/androidannotations/wiki/Adapters-and-lists
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ReportItemView v;

        if (convertView == null) {
            v = ReportItemView_.build(context);
        } else {
            v = (ReportItemView) convertView;
        }

        v.bind(getItem(position));

        return v;
    }


    //Add a public toString method on your class. In my example it was: public String toString() { return name; }


}


