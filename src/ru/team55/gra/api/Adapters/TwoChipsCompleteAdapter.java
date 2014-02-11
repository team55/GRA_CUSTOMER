package ru.team55.gra.api.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.rest.RestService;
import ru.team55.gra.api.BackgroundTaskCallback;
import ru.team55.gra.api.BackgroundTaskType;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;
import ru.team55.gra.api.svcLeaf;

import java.util.ArrayList;


/*

public class MyActivity extends Activity {
    private AutoCompleteTextView style;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ...
        style = (AutoCompleteTextView) findViewById(R.id.style);
        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        style.setAdapter(adapter);
    }
}

Need to set the Tokenizer !!!!

*/


@EBean
public class TwoChipsCompleteAdapter extends ArrayAdapter<pocoSearchItem> implements Filterable {

    private static final String TAG = TwoChipsCompleteAdapter.class.getSimpleName();


    private ArrayList<pocoSearchItem> mData;


    @RestService
    svcLeaf leafClient;


    //интерфейс
    //@Bean(InMemoryPersonFinder.class)
    //PersonFinder personFinder;

    @RootContext
    Context context;


    public TwoChipsCompleteAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line); //fake
    }


    @AfterInject
    void initAdapter() {
        mData = new ArrayList<pocoSearchItem>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public pocoSearchItem getItem(int index) {
        return mData.get(Math.min(index,mData.size()-1)); //fix при поиске иногда вываливается
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public enum searchTypes {kindOfActivity, adress}

    private searchTypes searchType = searchTypes.kindOfActivity;
    public void setSearchType(searchTypes searchType){
        this.searchType = searchType;
    }

    @Background
    public void startSearch(String str){
        Log.w(TAG, "Start search " + str);
        leafClient.setRootUrl(model.leafURL);

        if(callBack!=null) callBack.startProgress();

        if(searchType==searchTypes.adress)
            mData = leafClient.getAdresses(str); //, model.currentRegionId
        else
            mData = leafClient.getKindOfActivity(str);

        UpdateData();


        if(callBack!=null) callBack.stopProgress(BackgroundTaskType.AutoComplete);
    }


    @UiThread
    void UpdateData(){

        if(mData != null && mData.size() > 0) {
            notifyDataSetChanged();
        }
        else {
            notifyDataSetInvalidated();
        }

    }


    @Override
    public Filter getFilter() {

        Filter myFilter = new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {

                    // A class that queries a web API, parses the data and returns an ArrayList<Style>
                    //StyleFetcher fetcher = new StyleFetcher();

                    //try {
                    //    mData = fetcher.retrieveResults(constraint.toString());
                    //}
                    //catch(Exception e) {}



                    //TODO:адаптер должен проверять сеть а не медленных девайсах поиск по горячей клавише
                    startSearch(constraint.toString());

                    // Now assign the values and count to the FilterResults object
                    filterResults.values = mData;
                    filterResults.count = mData.size();
                }
                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence contraint, Filter.FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }


    // через биндер
    //             https://github.com/excilys/androidannotations/wiki/Adapters-and-lists
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchItemView personItemView;
        if (convertView == null) {
            personItemView = SearchItemView_.build(context);
        } else {
            personItemView = (SearchItemView) convertView;
        }

        pocoSearchItem itm = getItem(position);
        personItemView.bind(itm);

        return personItemView;
    }


    //Add a public toString method on your class. In my example it was: public String toString() { return name; }



    BackgroundTaskCallback callBack;
    public void addCallback(BackgroundTaskCallback callBack){
        this.callBack = callBack;
    }




}

