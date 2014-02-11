package ru.team55.gra.rating;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.parse.*;
import org.springframework.web.client.RestClientException;
import ru.team55.gra.api.*;
import ru.team55.gra.api.Adapters.AutoCompleteAdapter;
import ru.team55.gra.api.Adapters.ReportAdapter;
import ru.team55.gra.api.pocoObjects.*;
import ru.team55.gra.api.users.UserAccounts;
import ru.team55.gra.api.users.UserAccountsCallback;
import ru.team55.gra.api.users.UserHistory;
import ru.team55.helpers.DrawableCallbackAdapter;
import ru.team55.helpers.OnScrollListenerAdapter;
import ru.team55.network.NetHelper;
import ru.team55.network.NetworkReadyCallback;

import java.util.ArrayList;
import java.util.List;


@EActivity(R.layout.activity_welcome)
@Fullscreen
@OptionsMenu(R.menu.menu_welcome)
public class welcome extends ActionBarActivity  implements BackgroundTaskCallback {

    public static final String TAG = welcome.class.getSimpleName();


    //region annotations

    @Pref app_settings_ preferences;

    //@ViewById FrameLayout header;
    @ViewById DrawerLayout drawer;
    //@ViewById View progressor;

    //--------------------------------
    //страничка настроек
    @ViewById Spinner spRegion;
    //@ViewById Spinner spKindOfReportDetails;
    @ViewById TextView txtLogin;
    @ViewById Button   btnLogin;
    //--------------------------------

    //страничка поиска
    @ViewById(android.R.id.list)  ListView reportList;
    //@ViewById TwoChipsAutoCompleteTextView chips;
    @ViewById LinearLayout search_watermark;
    @ViewById LinearLayout btnHistory;
    @ViewById LinearLayout btnFavorites;
    //@ViewById LinearLayout btnSettings;

    @Bean AutoCompleteAdapter activityAdapter;
    @Bean AutoCompleteAdapter adressAdapter;
    @Bean ReportAdapter reportAdapter;
    //@Bean TwoChipsCompleteAdapter completeAdapter;


    //--------------------------------

    @RestService svcRoot rootClient;
    @RestService svcLeaf leafClient;
    @RestService svcReports reportClient;

    @SystemService LocationManager      locationManager;
    @SystemService ConnectivityManager  connectionManager;
    @SystemService InputMethodManager   inputManager;
    //endregion

    //пуш нотификации в парсе
    //http://www.androidbook.com/akc/display?url=DisplayNoteIMPURL&reportId=4553&ownerUserId=android

    //хорошая статья о парсе
    //http://www.ibm.com/developerworks/ru/library/j-parse/

    //Облачный код - что сделать на нем? - надо бы триггер для начала
    // - при добавлении заказа отправлять уведомление о новом заказе на мыло
    // - при неизменеии статуса заказа более чем задано регламентом - слать уведомление


    //https://parse.com/docs/cloud_code_guide#jobs

    private ActionBar mActionBar;
    private ActionBarDrawerToggle mDrawerToggle;

    private View listheader;  //объект для создания отступа в списке

    private boolean mFirstGlobalLayoutPerformed;
    private int mLastHeaderHeight = -1;
    private int mLastDampedScroll;

    private int mLastScrollPosition; //?

    private Drawable mActionBarBackgroundDrawable;
    private Boolean logged = false;
    boolean gps_enabled = false;



    //--------------------------------
    Button btnVoiceSearch;
    AutoCompleteTextView edtActivity;
    AutoCompleteTextView edtAdress;
    TextView textSearchResult;
    Button btnLoadMore;
    //--------------------------------



    public int dpToPixels(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        //---------- PARSE.COM
        ParseAnalytics.trackAppOpened(getIntent());

        model.currentRegion = new pocoTown(); //что бы было

        updateAll();
    }

    @AfterViews
    void SetupUI(){





        //---------------------
        //      экшенбар
        //---------------------
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.setTitle(getString(R.string.app_header));
        mActionBar.setSubtitle(getString(R.string.app_module_header_connected));

        //---------------------
        //      слайдер
        //---------------------
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                mActionBar.setTitle(getString(R.string.app_header));
                mActionBar.setSubtitle(getString(R.string.app_module_header) + " - " + model.currentRegion.name);
                //invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mActionBar.setTitle(getString(R.string.app_leftmenu_header));
                mActionBar.setSubtitle(getString(R.string.app_leftmenu_module_header));

                //в андроид 2.3 нет метода
                //invalidateOptionsMenu();

            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        mDrawerToggle.syncState(); //без этого не отрисовывется иконка а все в он пост креате юзают



        //заголовок для списка - будем управлять его высотой
        listheader = getLayoutInflater().inflate(R.layout.header, null);
        reportList.addHeaderView(listheader,"картинка заголовка", false);


        //-----------------------
        //      фон панели
        //-----------------------
        //mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.abc_ab_solid_light_holo);
        //mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.brush_caption);
        mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_background_textured_gra_blue);

        mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            mActionBarBackgroundDrawable.setCallback(drawableCallbackAdapter);
        }
        //mActionBarBackgroundDrawable.setAlpha(0);


        UpdateLoginStatus();


        //---------------------------  ПОИСК ----------------------------------

        reportAdapter.setProgressCallback(this);
        reportList.setAdapter(reportAdapter);


        // Creating a button - Load More   ПЕРЕДЕЛАТЬ НА ЗАГРУЗКУ С ЛАЙАУТА

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        btnLoadMore = new Button(this);
        btnLoadMore.setText("Показать еще");
        btnLoadMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_ya_selector));
        btnLoadMore.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSearch();
            }
        });
        btnLoadMore.setVisibility(View.GONE);
        ll.addView(btnLoadMore);
        ll.setPadding(dpToPixels(8),dpToPixels(8),0,dpToPixels(16));
        reportList.addFooterView(ll);



        //---------------------------  РЕКВИЗИТЫ ШАПКИ ----------------------------------



        textSearchResult = (TextView)findViewById(R.id.textSearchResult);
        btnVoiceSearch = (Button)findViewById(R.id.btnVoiceSearch);

        PackageManager pm = getPackageManager();
        List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            btnVoiceSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите");
                    startActivityForResult(intent, app.VOICE_RECOGNITION_REQUEST_CODE);
                }
            });
        } else {
            //btnVoiceSearch.setEnabled(false);
            btnVoiceSearch.setVisibility(View.GONE);
        }


        //установим соответствие заголовка отступу для списка
/* v2
        FrameLayout root = (FrameLayout) findViewById(R.id.page);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.w("SCROLL", "Global layout "+ mFirstGlobalLayoutPerformed);


                int headerHeight = header.getHeight();
                if (!mFirstGlobalLayoutPerformed && headerHeight != 0) {
                    updateHeaderHeight(headerHeight);
                    mFirstGlobalLayoutPerformed = true;
                }


            }
        });

*/


        //reportList.setOnScrollListener(onScrollListenerAdapter); //после отрисовки
        //completeAdapter.addCallback(this);


        //----------------------------------------------------------------------------
   /*   edtActivity.addTextChangedListener(new TextWatcherForSearch(editKindOfActivity, adapterKindOfActivity));
        edtActivity.setAdapter(adapterKindOfActivity);
        edtActivity.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                model.currentActivityId =((SearchItemAdapter)editKindOfActivity.getAdapter()).getItem(position).id;
            }
        });

*/

        activityAdapter.addCallback(this);

        //TODO: допилить проверку сервера
        edtActivity = (AutoCompleteTextView)findViewById(R.id.edtActivity);
        edtActivity.setAdapter(activityAdapter);
        edtActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.w("DATAs", ""+position);
                model.currentActivityId = ((AutoCompleteAdapter) edtActivity.getAdapter()).getItem(position).id;
                model.currentActivitySearch = ((AutoCompleteAdapter) edtActivity.getAdapter()).getItem(position);
                edtActivity.setSelection(0);
            }
        });


        adressAdapter.addCallback(this);
        adressAdapter.setKindOfService(kindOfService.adress);

        edtAdress = (AutoCompleteTextView)findViewById(R.id.edtAdress);
        edtAdress.setAdapter(adressAdapter);
        edtAdress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.w("DATAs", ""+position);
                model.currentPositionId = ((AutoCompleteAdapter) edtAdress.getAdapter()).getItem(position).id;
                model.currentAddressSearch = ((AutoCompleteAdapter) edtAdress.getAdapter()).getItem(position);
                edtAdress.setSelection(0);
            }
        });




        Button btnKindOfActivityClear = (Button)findViewById(R.id.btnKindOfActivityClear);
        btnKindOfActivityClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtActivity.setText("");
                model.currentActivityId=0;
                model.currentActivitySearch = null;
                clearSearchResult();
            }
        });


        Button btnAdressClear = (Button)findViewById(R.id.btnAdressClear);
        btnAdressClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.currentPositionId!=0){
                    edtAdress.setText("");
                    model.currentPositionId=0;
                    model.currentAddressSearch = null;
                    clearSearchResult();
                }
                //а если и было пусто то и не чистим
            }
        });

        Button btnGeoPoint = (Button)findViewById(R.id.btnGeoPoint);
        btnGeoPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) )
                    searchGeoPoint();
                else
                    showGPSDialog();
            }
        });

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });


    }


    void clearSearchResult(){

        reportAdapter.Reset();
        search_watermark.setVisibility(View.VISIBLE);
        textSearchResult.setText("");
        btnLoadMore.setVisibility(View.GONE);

    }



/*
    @ItemSelect
    public void edtActivityItemSelected(boolean selected, pocoSearchItem item) {
        Log.w("DATAs", item.toString());
    }
*/


    void UpdateLoginStatus(){
        logged = (UserAccounts.userIsLogged() && !UserAccounts.userLoggedAsAnonymous());

        txtLogin.setText(logged?UserAccounts.getUserName():"Вход не выполнен");
        btnLogin.setText(logged?"Выйти":"Войти");

        btnFavorites.setEnabled(logged);
        btnHistory.setEnabled(logged);
        //btnSettings.setEnabled(logged);
    }


    //-------------------------------------------------------------------------------------------

    private OnScrollListenerAdapter onScrollListenerAdapter = new OnScrollListenerAdapter() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

/*
v2
            Log.d("SCROLL","onScroll" + firstVisibleItem + " total "+totalItemCount );

            View topChild = view.getChildAt(0);
            if (topChild == null) {
                onNewScroll(0);
                Log.d("SCROLL","onScroll 0");
            } else if (topChild != listheader) {
                onNewScroll(header.getHeight());
                Log.d("SCROLL","onScroll header height"+header.getHeight()+" "+topChild.toString() );
            } else {
                Log.d("SCROLL","onScroll topchild");
                onNewScroll(-topChild.getTop());
            }
*/
        }
    };

    private DrawableCallbackAdapter drawableCallbackAdapter = new DrawableCallbackAdapter() {
        @Override
        public void invalidateDrawable(Drawable who) {
            mActionBar.setBackgroundDrawable(who);
        }
    };


    @OptionsItem(R.id.mnu_settings)
    void settingsSelected(MenuItem item){
        drawer.openDrawer(Gravity.LEFT);
    }

    @OptionsItem(android.R.id.home)
    void homeSelected(MenuItem item) {
        mDrawerToggle.onOptionsItemSelected(item);
    }



  /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawer.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    //----------------------------------------------------------------


 /*   private void onNewScroll(int scrollPosition) {

        if (mActionBar == null) {
            return;
        }
        Log.d("SCROLL","onNewScroll "+scrollPosition);

        int currentHeaderHeight = header.getHeight();//я
        if (currentHeaderHeight != mLastHeaderHeight) {
            updateHeaderHeight(currentHeaderHeight);
        }

        int headerHeight = currentHeaderHeight - getSupportActionBar().getHeight();
        float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
        int newAlpha = (int) (ratio * 255);
        //mActionBarBackgroundDrawable.setAlpha(newAlpha);

        addParallaxEffect(scrollPosition);
    }
*/

/*
    private void addParallaxEffect(int scrollPosition) {
        //float damping = mUseParallax ? 0.5f : 1.0f;
        float damping =  1.0f;

        Log.d("SCROLL","addParallaxEffect "+scrollPosition);

        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        //v2 header.offsetTopAndBottom(offset);
        Log.d("SCROLL","addParallaxEffect offset "+offset);


*/
/*
        if (mListViewBackgroundView != null) {
            offset = mLastScrollPosition - scrollPosition;
            mListViewBackgroundView.offsetTopAndBottom(offset);
        }
*//*


        if (mFirstGlobalLayoutPerformed) {
            mLastScrollPosition = scrollPosition;
            mLastDampedScroll = dampedScroll;
        }
    }
*/

/*
    private void updateHeaderHeight(int headerHeight) {

        Log.d("SCROLL","updateHeaderHeight "+headerHeight);

        ViewGroup.LayoutParams params = listheader.getLayoutParams();

        if(params==null) return; //я - заглушка пока, а то падает - срабатывает скролл

        params.height = headerHeight;
        listheader.setLayoutParams(params);

        mLastHeaderHeight = headerHeight;
    }
*/



    //----------------------------------------------------------------


    @ItemSelect
    public void spRegionItemSelected(boolean selected, pocoTown selectedItem) {
        model.currentRegionId   = selectedItem.territory_id;
        model.leafURL           = selectedItem.url;
        preferences.region().put(selectedItem.territory_id);

        model.currentRegion = selectedItem;
    }


    @ItemClick(android.R.id.list)
    void reportListItemClicked(pocoReportItem clickedItem){

        //как передать данные в активити ? - или парсел или должны лежать в приложении
        model.currentReport = clickedItem;

        startActivity(new Intent(this, pageDetails_.class) );

    }

    @Click
    void btnLogin(){
        Boolean logged = (UserAccounts.userIsLogged() && !UserAccounts.userLoggedAsAnonymous());

        if(logged){
            UserAccounts.logOut();
            UpdateLoginStatus();
        }else{
            startActivityForResult(new Intent(this, pageLoginRegister_.class), app.AUTH_REQUEST_CODE);
        }
    }

    @OnActivityResult(app.AUTH_REQUEST_CODE)
    void onResult() {
        UpdateLoginStatus();
    }


    @Click(R.id.btnHistory)
    void btnHistory(){
        startActivityForResult(new Intent(this, History_.class), app.HISTORY_REQUEST_CODE);
    }

    @Click(R.id.btnFavorites)
    void btnFavorites(){
        startActivityForResult(new Intent(this, Favorites_.class), app.FAVORITES_REQUEST_CODE);
    }

    @OnActivityResult(app.HISTORY_REQUEST_CODE)
    void onHistoryResult(int resultCode) {

        drawer.closeDrawers();

        if(resultCode == RESULT_OK){


            //List<pocoSearchItem> items = new LinkedList<pocoSearchItem>();
            //items.add(pocoSearchItem.Create(model.currentActivityId, "asdasd", "услуга"));
            //chips.ReplaceValues(model.currentSearch);


            //id уже установлены - устанавливаем наименования
            for(pocoSearchItem item:model.currentSearch){
                if(item.type == Const.PFX_ACTIVITY){
                    edtActivity.setText(item.name);
                }else
                if(item.type == Const.PFX_ADRESS){
                    edtAdress.setText(item.name);
                }

            }

            startSearch();
        }

    }


    @Click (R.id.btnCallOperator)
    void btnCallOperator(){
        try {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + model.getOperatorPhone()));
            this.startActivity(callIntent);

        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    @Background
    void searchGeoPoint(){
        startProgress();

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider); //а если нет?

        if(location!=null){
            leafClient.setRootUrl(model.leafURL);
            pocoAdressList al = leafClient.getFirstAdress(""+location.getLatitude(), ""+location.getLongitude());
            //Log.d("TT", "Address by metod = "+al.get(0).name);

            if(!al.isEmpty()){
                pocoAdress a = al.get(0);
                updateAdress(a);
            }
        }

        stopProgress(BackgroundTaskType.AutoComplete);
    }

    @UiThread
    void updateAdress(pocoAdress a){
        pocoSearchItem is = pocoSearchItem.Create(a.id, a.name, Const.PFX_ADRESS);
        model.currentAddressSearch = is;
        model.currentPositionId = is.id;

        edtAdress.setText(a.name);
    }

    @Click (R.id.sendFeedback)
    void sendFeedback(){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "info@agent-russia.ru" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Отзыв");
        this.startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
    }


    @OnActivityResult(app.VOICE_RECOGNITION_REQUEST_CODE)
    void onResult(Intent data) {

        if(data==null) return; //не найдено

        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
/*
        String resString = "";
        for (String s : matches)
        {
            resString += s + "\t";
        }
*/

        if(matches.size()>0){
            //chips.append(matches.get(0));
            if(edtActivity.hasFocus()){
                edtActivity.setText(matches.get(0));


                //edtActivity.performCompletion();

            }
            if(edtAdress.hasFocus()){ edtAdress.setText(matches.get(0)); }
        }




    }


    //TODO: проверка наличия сети

    @Background
    void startSearch(){


        if(model.currentRegionId == 0)  return;
        if(model.currentActivityId == 0) return;

        currentPosition = 0;
        currentPage = 1;
        reportAdapter.startSearch(currentPage);


    }

    @Background
    void  nextSearch(){
        if(model.currentRegionId == 0)  return;
        if(model.currentActivityId == 0) return;

        currentPage++;
        reportAdapter.startSearch(currentPage);

        // get listview current position - used to maintain scroll position
        currentPosition = reportList.getFirstVisiblePosition();



        Log.w("SCROLL","-----------------------------------------------------------");
        //mFirstGlobalLayoutPerformed = false;
    }

    private int currentPage = 1;
    private int currentPosition = 0;


    private boolean searchHasResult = false;

    @UiThread
    void afterSearch(){

        //Прячем навигацию
/*
        getWindow().
                getDecorView().
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
*/


        if(currentPage>1 && searchHasResult){
            reportList.setSelectionFromTop(currentPosition + 1, 0);
            //mFirstGlobalLayoutPerformed = false;
        }

        if(searchHasResult) {

            pocoReportItem itm = reportAdapter.getItem(0);
            if(model.currentPositionId==0){
                textSearchResult.setText("количество результатов "+itm.count_arr);
                btnLoadMore.setVisibility(View.VISIBLE);

            }else{
                textSearchResult.setText("Рейтинг ТОП 10");
                btnLoadMore.setVisibility(View.GONE);
            }



            inputManager.hideSoftInputFromWindow(reportList.getWindowToken(), 0);

            if(logged){

                ParseQuery<UserHistory> query = ParseQuery.getQuery("UserHistory");
                query.whereEqualTo("activityId", model.currentActivityId);
                query.whereEqualTo("adressId",   model.currentPositionId);  //todo: сброс адреса
                query.whereEqualTo("regionId",   model.currentRegionId);

                query.findInBackground(new FindCallback<UserHistory>() {
                    public void done(List<UserHistory> scoreList, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + scoreList.size() + " scores");

                            if(scoreList.size()==0){
                                UserHistory data = ParseObject.create(UserHistory.class);


                                //data.setData(model.currentRegionId, chips.getItems());
                                //data.setData(edtActivity.getText().toString(), edtAdress.getText().toString());

                                data.setHistoryData(
                                        model.currentRegionId,
                                        model.currentActivityId, edtActivity.getText().toString(),
                                        model.currentPositionId, edtAdress.getText().toString());

                                data.setACL(new ParseACL(ParseUser.getCurrentUser()));
                                data.saveInBackground(); //калбак не нужен
                            }


                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });

            }

            search_watermark.setVisibility(View.GONE);

        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Ничего не найдено")
                    .setMessage("\r\n Проверьте условия поиска или измените их \r\n")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }


    //----------------------------------------------------------------

    @Background
    public void updateAll(){

        Log.w("LIFE","Upd");
        Log.w("DATA","Start getting data");

        startProgress();


        //тут стартуем поток и ждем его завершения - делать это надо в бакграунде !!
        NetHelper.readyNetwork(new NetworkReadyCallback() {

            @Override
            public void Ready() {
                updateData(); //в фоновом процессе, по завершению сам сбросит прогресс
            }

            @Override
            public void OnError(int code, String error) {

                Log.w("DATA","Error getting data");
                stopProgress(BackgroundTaskType.FirstLoad);
                showNetworkErrorDialog();

            }

        }, app.ROOT_URL, NetHelper.TIME_5S);

    }

    @AfterInject     //под вопросом логика!!! анонимус вроде как не нужен
    void getCurrentState(){

        Log.w(TAG, "getCurrentState");

        Boolean hasMessage = false;

        if(!NetHelper.networkIsEnabled(this)) {
            hasMessage = true;
        }


        if(!NetHelper.gpsIsEnabled(this)) {
            hasMessage = true;
        }



        if(!hasMessage){

            if (!UserAccounts.userIsLogged()) {


                UserAccounts.loginAsAnonymousUser(new UserAccountsCallback() {
                    @Override
                    public void onSuccess() {
                        Log.w(TAG, "loginAsAnonymousUser OK");
                    }

                    @Override
                    public void onError(int code, String Message) {
                        Log.e(TAG, "loginAsAnonymousUser failed: " + Message);
                    }
                });
            }


            //updateData();  //мы это в другом месте делали
        }
    }


    //TODO: Если нет определения координант или не указан город - скажем про настройки и откроем панельку меню

    @UiThread
    public void UpdateUI(){

        //это потому что фрагменты были
        //left_menu.bindData();

        ArrayAdapter<pocoTown> adapter1 = model.getTownsAdapter(getBaseContext());

        spRegion.setAdapter(adapter1);
        for (int position = 0; position < adapter1.getCount(); position++){
            if(adapter1.getItem(position).territory_id == model.currentRegionId){
                spRegion.setSelection(position);
                break;
            }
        }

        mActionBar.setSubtitle(getString(R.string.app_module_header) + " " + model.currentRegion.name);


    /*    ArrayAdapter<pocoSearchItem> adapter2 = model.getkindsOfReportDetailsAdapter(ctx);

        spKindOfReportDetails.setAdapter(adapter2);
        for (int position = 0; position < adapter2.getCount(); position++){
            if(adapter2.getItem(position).id == model.currentKindsOfReportDetailsId){
                spKindOfReportDetails.setSelection(position);
                break;
            }
        }
*/




        //теперь проверяем условия
        // не указан вид отчета
        // первый запуск
        // не включено определение координат
        // - если одно из них отсутсвует - покажем настройки

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean firstRun = preferences.firstRun().get();

        if(model.currentKindsOfReportDetailsId == 0 || !gps_enabled || firstRun)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //drawer.openDrawer(Gravity.START);
                    preferences.firstRun().put(false);
                }
            }, 1000);

    }


    //TODO: через гугл апи определим город
    //подставим если отличается в настройку (скажем пользователю)
    //определим адрес по координатам в апи города

    @Background
    void updateData(){

        rootClient.setRootUrl(app.ROOT_URL);
        try {

            model.towns = rootClient.getAllTowns();

            int regionId = preferences.region().get();

            for(pocoTown t:model.towns){
                if(t.territory_id==regionId) {
                    model.currentRegionId = t.territory_id;
                    model.leafURL = t.url;

                    model.currentRegion = t;

                    break;
                };
            }

/*
            leafClient.setRootUrl(model.leafURL);
            model.kindsOfReportDetails = leafClient.getReportForms(); //кратко-подробно
            int detailsId = preferences.report_form().get();
            for(pocoSearchItem t:model.kindsOfReportDetails){
                if(t.id==detailsId) {
                    model.currentKindsOfReportDetailsId = t.id;
                    break;
                }
            }
*/

            //todo получение телефона оператора от региона и в калбаке установка в модель



            UpdateUI();



        }catch(RestClientException e){

            //Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_LONG).show();

            //e.printStackTrace();
            stopProgress(BackgroundTaskType.FirstLoad);
            showNetworkErrorDialog();

        }catch(Exception ex){
            ex.printStackTrace();

        }finally {
            stopProgress(BackgroundTaskType.FirstLoad);
        }


    }


    @Override
    @UiThread
    public void startProgress() {
        setSupportProgressBarIndeterminateVisibility(true);

        //кнопки поиска недоступны
    }

    @Override
    @UiThread
    public void stopProgress(BackgroundTaskType type) {
        setSupportProgressBarIndeterminateVisibility(false);

        if(type==BackgroundTaskType.Search){
            searchHasResult = reportAdapter.getCount()>0;
            afterSearch();
        }

    }


    //TODO: вынести в хелпер и выводить через что подключены - вайфай или модем

    @SystemService ConnectivityManager connect;
    public boolean executeIfConnected(Runnable run){

        boolean connected = false;
        NetworkInfo[] ni = connect.getAllNetworkInfo();
        for (int i = 0; i<ni.length;i++){

            Log.d(TAG, String.format("%s \t available=%s \t connected=%s \t extra=%s",
                    ni[i].getTypeName(),
                    ni[i].isAvailable(),
                    ni[i].isConnected(),
                    ni[i].getExtraInfo()//internet
            ));

            if(ni[i].isAvailable() && ni[i].isConnected()){
                connected = true;
                break;
            }

        }
        if(connected)  run.run();
        return connected;
    }

    @UiThread
    void showNetworkErrorDialog(){

        new AlertDialog
                .Builder(welcome.this)

                .setCancelable(false)
                .setTitle("Ошибка")
                .setMessage("Не удается установить соединение с сервером")
                .setPositiveButton("Повторить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        updateAll();
                    }
                })
                .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        finish();
                    }
                })
                .show();

    }

    void showGPSDialog(){

        new AlertDialog
                //.Builder(this)
                .Builder(new ContextThemeWrapper(this,R.style.Theme_Dialog_Alert)) //android.R.style.Theme_Dialog Theme_DeviceDefault_Light_Dialog
                .setCancelable(false)
                .setTitle("Определение координат")
                .setMessage("Необходимо включить GPS")
                .setNegativeButton("Нет, спасибо", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Включить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                })
                .show();

    }
}