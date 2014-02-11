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
import ru.team55.gra.api.*;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;

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
public class AutoCompleteAdapter extends ArrayAdapter<pocoSearchItem> implements Filterable {

    private static final String TAG = AutoCompleteAdapter.class.getSimpleName();


    private ArrayList<pocoSearchItem> mData;
    private ArrayList<pocoSearchItem> mDataBackground;

    @RestService
    svcLeaf leafClient;

    //интерфейс
    //@Bean(InMemoryPersonFinder.class)
    //PersonFinder personFinder;

    @RootContext
    Context context;

    public AutoCompleteAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line); //fake
    }

    @AfterInject
    void initAdapter() {
        mData = new ArrayList<pocoSearchItem>();
        _kindOfService = kindOfService.activity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public pocoSearchItem getItem(int index) {
        //return mData.get(index);   //та же фишка что была - в адаптере 0 а по индексу пытаемся получить
        return mData.get(Math.min(index,mData.size()-1)); //fix при поиске иногда вываливается
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private int _kindOfService;
    public void setKindOfService(int k){
        _kindOfService = k;
    }


    //TODO: проверка и подъем соединения - если ввели еще чего - как бы не сразу а ложить в очередь
    //если перестали вводить - шлем запрос данных


    //TODO: тут эксепшн в другом потоке - ничего не падает но и не ищет - когда после входя связь оборвалась

    @Background
    public void startSearch(String str){

        Log.w(TAG, "Start search kindOfActivity "+str);
        if(callBack!=null) callBack.startProgress();


        leafClient.setRootUrl(model.leafURL);

        switch (_kindOfService){

            case kindOfService.activity:
                mDataBackground = leafClient.getKindOfActivity(str);
                Log.w(TAG, "Searched activities "+mDataBackground.size());
                break;
            case kindOfService.adress:
                mDataBackground = leafClient.getAdresses(str);
                break;
        }

        UpdateData();

        if(callBack!=null) callBack.stopProgress(BackgroundTaskType.AutoComplete);
    }


    BackgroundTaskCallback callBack;
    public void addCallback(BackgroundTaskCallback callBack){
        this.callBack = callBack;
    }


    @UiThread
    void UpdateData(){

        if(mDataBackground != null && mDataBackground.size() > 0) {
            Log.w(TAG, "SetData "+mDataBackground.size());
            mData = mDataBackground;
            notifyDataSetChanged();
        }
        else {
            mData.clear();
            notifyDataSetInvalidated();
        }

    }


    @Override
    public Filter getFilter() {

        Log.w(TAG, "Getting filter ");
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
    //https://github.com/excilys/androidannotations/wiki/Adapters-and-lists
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.w(TAG, "Gettinfg view "+position);

        SearchItemView personItemView;
        if (convertView == null) {
            personItemView = SearchItemView_.build(context);
        } else {
            personItemView = (SearchItemView) convertView;
        }

        personItemView.bind(getItem(position));

        return personItemView;
    }


        //Add a public toString method on your class. In my example it was: public String toString() { return name; }


}

