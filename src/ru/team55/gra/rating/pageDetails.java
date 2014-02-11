package ru.team55.gra.rating;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.googlecode.androidannotations.annotations.*;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import ru.team55.gra.api.Adapters.*;
import ru.team55.gra.api.Const;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoObjects.pocoOperatingMode;
import ru.team55.gra.api.pocoObjects.pocoReportDetails;
import ru.team55.gra.api.pocoObjects.pocoReportInfo;
import ru.team55.gra.api.pocoObjects.pocoReportItem;
import ru.team55.gra.api.users.UserAccounts;
import ru.team55.gra.api.users.UserFavorites;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@EActivity(R.layout.activity_details)
@OptionsMenu(R.menu.menu_report_details)
@Fullscreen
public class pageDetails extends ActionBarActivity {


    private static final String TAG = pageDetails.class.getSimpleName();

    boolean logged = false;
    boolean hasMoney = false;
    boolean hasTime = false;
    boolean hasWeb = false;

    pocoReportItem item;
    List<String> phones = new LinkedList<String>();
    List<String> emails = new LinkedList<String>();
    String url = "";

    @ViewById ImageView imgMapView;
    @ViewById LinearLayout container;
    @ViewById LinearLayout containerMoney;
    @ViewById LinearLayout containerTime;
    @ViewById LinearLayout containerForRatings;

    @ViewById(R.id.svMap) ScrollView root;
    //@ViewById ExpandableListView listDetails;
    //@ViewById SlidingPaneLayout sp;


    //Будет только в v3
    //@OptionMenuItem
    //MenuItem menuSearch;

    private ActionBar mActionBar;
    private boolean sizeCalculated = false;

    //--------------------------------------------
    MenuItem mnu_call;
    MenuItem mnu_mail;
    MenuItem mnu_web;


    @OptionsItem(android.R.id.home)
    void homeSelected(MenuItem item) {
       this.finish();
    }

    @OptionsItem
    void mnu_add_favorites(){

        UserFavorites data = ParseObject.create(UserFavorites.class);
        data.setData(
                model.currentRegion.territory_id,
                model.currentActivitySearch.id, model.currentActivitySearch.name,
                model.currentReport.id, model.currentReport.name);
        data.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(pageDetails.this, "Организация добавлена в избранное", Toast.LENGTH_LONG).show();
            }
        });

    }

    @OptionsItem
    void mnu_call(){

        String phones_a[] = new String[phones.size()];
        phones_a = phones.toArray(phones_a);

        if(phones_a.length==0) return;


        if(phones_a.length==1){
            callPhone(phones.get(0));
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Звонок по номеру")
                    .setSingleChoiceItems(phones_a,0, null)
                    .setPositiveButton("Позвонить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            callPhone(phones.get(selectedPosition));

                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    void callPhone(String phone){
        //Intent callIntent = new Intent(Intent.ACTION_DIAL);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone.trim()));
        startActivity(callIntent);
    }

    @OptionsItem
    void mnu_mail() {

        String mails_a[] = new String[emails.size()];
        mails_a = emails.toArray(mails_a);

        if(mails_a.length==0) return;


        if(mails_a.length==1){
            sendEmail(emails.get(0));
        }else{




            new AlertDialog.Builder(this)
                    //.setView()
                    .setTitle("Отправка письма")
                    .setSingleChoiceItems(mails_a,0, null)
                    .setPositiveButton("Написать", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            sendEmail(emails.get(selectedPosition));
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
    }

    void sendEmail(String mail){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { mail.trim() });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Агент по рейтингам");
        this.startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
    }

    @OptionsItem
    void mnu_map() {
        MapClick();
    }

    @OptionsItem
    void mnu_order() {
        startActivity(new Intent(this, pageOrders_.class));
    }

    @OptionsItem
    void mnu_web() {
        if(url.equalsIgnoreCase("")) return;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((url.contains("http://") ? "" : "http://") + url));
        startActivity(browserIntent);

    }

    @Click(R.id.imgMapView)
    void MapClick(){
        float latitude = item.shirota;
        float longitude = item.dolgota;
        String label = item.name;

        //http://stackoverflow.com/questions/13053352/android-how-to-launch-google-map-intent-in-android-app-with-certain-location

        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        try{
         startActivity(intent);
        }catch (ActivityNotFoundException ex)
        {

            new AlertDialog
                    .Builder(pageDetails.this)
                    .setCancelable(false)
                    .setTitle("Карты")
                    .setMessage("Не установлено программы для работы с картами")
                    .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        }
                    })
                    .show();


        }

    }

    //-----------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem mnu_add_favorites = menu.findItem(R.id.mnu_add_favorites);
        mnu_add_favorites.setVisible(logged);

        mnu_call = menu.findItem(R.id.mnu_call);
        mnu_mail = menu.findItem(R.id.mnu_mail);
        mnu_web  = menu.findItem(R.id.mnu_web);

        if(phones.size()==0) mnu_call.setVisible(false);
        if(emails.size()==0) mnu_mail.setVisible(false);
        if(!hasWeb) mnu_web.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @AfterViews
    void setupUIAndBindData(){

        item = model.currentReport;

        logged = (UserAccounts.userIsLogged() && !UserAccounts.userLoggedAsAnonymous());


        //---------------------
        //      экшенбар
        //---------------------
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);


        mActionBar.setSubtitle(model.currentActivitySearch.name);
        mActionBar.setTitle(model.currentReport.name);



        //sp.setShadowDrawable(getResources().getDrawable(R.drawable.drawer_shadow_left));

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //int headerHeight = header.getHeight();
                //if (!mFirstGlobalLayoutPerformed && headerHeight != 0) {
                //    updateHeaderHeight(headerHeight);
                //    mFirstGlobalLayoutPerformed = true;
                //    }


                if(!sizeCalculated) {
                    Log.w("SIZE", "w="+root.getWidth());
                    //imgMapView.setMinimumWidth(root.getWidth());

                    LoadImageMap(); //после устаканивания размеров


                    sizeCalculated = true;
                }



            }
        });



        //расчет ширины





        //listDetails.computeScroll();


        if(item.distance>0){
            ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
            container.addView(v);
            v.setTextAndIcon("Дистанция: "+item.distance+" м", R.drawable.location_map);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapClick();
                }
            });

        }


        //расписание (нужно на все дни - как в веб),
        //способ оплаты,

        for(pocoReportInfo info:item.information){

            if(info.name.equalsIgnoreCase(Const.ID_NAME)){
                //ничего
            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_ADRESS)){
                ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
                container.addView(v);
                v.setTextAndIcon(info.value, R.drawable.location_map);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapClick();
                    }
                });
            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_SITE)){

                    String urltext = info.value.replace(model.leafURL.replace("http://", "") + "ajax/j.server.php?type=10&http=" ,"");
                    url = info.value; //Значение обрабатывается сервером и перенаправляет на сайт
                    Spannable text = new SpannableString(urltext);
                    text.setSpan(new StyleSpan(Typeface.BOLD), 0 , urltext.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(new ForegroundColorSpan(0xFF4d59a1), 0 , urltext.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
                    container.addView(v);

                    v.setTextAndIcon(text, R.drawable.location_web_site);
                    v.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            mnu_web();
                        }
                    });
                    hasWeb = true;
            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_MAIL)) {
                emails.add(info.value);
            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_PHONE)){
                phones.add(info.value);

            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_TAX)){

                hasMoney = true;
                ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
                containerMoney.addView(v);
                v.setTextAndIcon(String.format("Способ оплаты:\r\n%s", info.value), R.drawable.collections_labels);
            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_FAX)){

                ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
                container.addView(v);
                v.setText(String.format("Факс: %s", info.value));
            }
            else //------------------------------------------------------------------------------
            if(info.name.equalsIgnoreCase(Const.ID_GPS)){

                ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
                container.addView(v);
                v.setTextAndIcon(String.format("Географические координаты:\r\n %s", info.value), R.drawable.location_place);
            }
            else //------------------------------------------------------------------------------
            {
                ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
                container.addView(v);
                v.setText(String.format("%s: %s", info.name, info.value));
            }


        } //цикл свойства




        //------------------------------------------------------------------------------
        if(phones.size()>0){
            ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
            container.addView(v);

            String phone = "";
            for(String p: phones) phone+= phone.isEmpty()?p:", "+p;
            v.setTextAndIcon(phone, R.drawable.device_access_call);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mnu_call();
                }
            });
        }


        //------------------------------------------------------------------------------
        if(emails.size()>0){

            ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
            container.addView(v);


            String mail = "";
            for(String p: emails) mail+= mail.isEmpty()?p:", "+p;
            v.setTextAndIcon(mail, R.drawable.content_read);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mnu_mail();
                }
            });

        }

        //-----------------------------------
        //          График работы
        //-----------------------------------
        String worktime="";
        if(item.operating_modes!=null)
        {
            for(pocoOperatingMode info:item.operating_modes){

                hasTime = true;

                //if(info.day_week_integer==0)continue; //сегодня

                //тут надо определять длину поля - для двухсимвольных все проще
                String format = info.day_week_string.length()==0?"%s%s %13s":"%-11s %s %13s";

                worktime+=(worktime.isEmpty()?"":"\r\n")+
                        String.format(format,
                                item.operating_modes.length>1?info.day_week_string:"Ежедневно",
                                info.day_week_string.length()>10?"\t": info.day_week_string.length()<6?"\t\t\t":"\t\t" ,
                                info.operating_mode);



            }
            ContactItemView v = ContactItemView_.build(getApplicationContext(), null);
            containerTime.addView(v);
            v.setTextAndIcon(worktime, R.drawable.device_access_time);
        }

        //Если нет способа оплаты или графика работы - скроем соответствующие поля
        if(!hasMoney) containerMoney.setVisibility(View.GONE);
        if(!hasTime) containerTime.setVisibility(View.GONE);
        if(item.rating_k+item.rating_d+item.rating_l == 0) containerForRatings.setVisibility(View.GONE);



        //LoadImageMap();



        //откроем панельку
        /*if(item.present==1){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sp.openPane();
                }
            }, 1000);

        }*/

        //listDetails.setAdapter(new MyAdapter());


        addRatingPanel(0);
        addRatingPanel(1);
        addRatingPanel(2);

    }

    void addRatingPanel(int position){

        pocoReportDetails[] detailses =  getDetails(position);

        if(detailses==null) return;

        ratingDetailGroupView dv = ratingDetailGroupView_.build(pageDetails.this);
        dv.bind(item, position);
        containerForRatings.addView(dv);



        for(pocoReportDetails details:detailses){
            ratingDetailItemView cv = ratingDetailItemView_.build(pageDetails.this);
            cv.bind(details);
            containerForRatings.addView(cv);
        }

    }

    private pocoReportDetails[] getDetails(int groupPosition){

        switch (groupPosition){
            case 0:
                return item.rating_details_d;
            case 1:
                return item.rating_details_k;
            default:
                return item.rating_details_l;
        }
    }


    @Background
    void LoadImageMap(){
        try {
            //Bitmap bmp = BitmapFactory.decodeStream(new URL(getUrlMapApi(13, imgMapView.getLayoutParams().width, imgMapView.getLayoutParams().height)).openStream());

            Bitmap bmp = BitmapFactory.decodeStream(new URL(getUrlMapApi(13, imgMapView.getWidth(), imgMapView.getHeight()) ).openStream());
            Log.w("SIZE", String.format("x=%s y=%s", imgMapView.getWidth(), imgMapView.getHeight()));
            setImageMap(bmp);
        } catch (IOException e) {
        }
    }

    @UiThread
    void setImageMap(Bitmap bmp){
        imgMapView.setImageBitmap(bmp);
    }


    private String getUrlMapApi(int zoom, int w, int h){
        String ret = String.format("http://maps.googleapis.com/maps/api/staticmap?center=%1$s,%2$s&zoom=%3$s&size=%4$sx%5$s&sensor=false&markers=color:green|%1$s,%2$s",
                item.shirota, item.dolgota, zoom, w,h);

        Log.w("MAP", ret);

        return  ret;
        //&maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C 40.711614,-74.012318&markers=color:red%7Ccolor:red%7Clabel:C%7C40.718217,-73.998284
    }




    /* =========================================================================== */


    public class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return 3;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getDetails(groupPosition).length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return item; //тут нужен заголовок по конторе
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return getDetails(groupPosition)[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return item.id;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return  childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            ratingDetailGroupView v;

            if (convertView == null) {
                v = ratingDetailGroupView_.build(pageDetails.this);
            } else {
                v = (ratingDetailGroupView) convertView;
            }

            v.bind(item, groupPosition);

            //ExpandableListView eLV = (ExpandableListView) parent;
            //eLV.expandGroup(groupPosition);

            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ratingDetailItemView v;

            if (convertView == null) {
                v = ratingDetailItemView_.build(pageDetails.this);
            } else {
                v = (ratingDetailItemView) convertView;
            }

            pocoReportDetails[] detailses =  getDetails(groupPosition);

            v.bind(detailses[childPosition]);

            return v;

        }

    }


}
