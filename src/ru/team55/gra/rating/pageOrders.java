package ru.team55.gra.rating;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.androidannotations.annotations.*;
import com.parse.*;
import org.springframework.web.client.RestClientException;
import ru.team55.gra.api.Adapters.FormFieldArrayAdapter;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoForms.*;
import ru.team55.gra.forms.rawFormContainer;
import ru.team55.gra.forms.rawOrdersData;
import ru.team55.sms.SMSReceiver;
import ru.team55.sms.svcSMSSender;
import ru.team55.sms.svcSMSSender_;
import ru.team55.ui.LinearLayoutBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@EActivity(R.layout.activity_order)
@OptionsMenu(R.menu.menu_order)
@Fullscreen
public class pageOrders extends ActionBarActivity {
    public static final String CONTACT = "contact";


    //TODO: Поля телефона нет - добавим по умолчанию
    //TODO: Добавим комментарий и кнопку подтверждения - СМС с полем телефона
    //TODO: Подстановка в поле телефона номера телефона
    // важная группа - выделять фоном
    //группы с реквизитами не использовать на мобильном - удалять (нет чайлдов - не добавляем)


    private BroadcastReceiver receiver;
    EditText confirmCode;
    int confirmCodeSMS = 0;
    EditText phoneNumberText;
    Button btnConfirm;
    boolean confirmed = false;

    private ActionBar mActionBar;

    @ViewById(R.id.myRoot)
    LinearLayout root;

    rawFormContainer form = null;//сырые данные парса

    pocoForm  formData = null; //разобранный объект формы
    String formDescription = "";//описание формы из парса

    HashMap<String,View> pages = new HashMap<String, View>();
    HashMap<String,View> datafields = new HashMap<String, View>(); //соответствие тэга и элемента управления


    boolean hasContact = false; //флаг в циклах обхода (сбрасываем при построении и сохранении данных)

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }


    @AfterViews
    void setupUI(){
        mActionBar = getSupportActionBar();
        mActionBar.setSubtitle(model.currentActivitySearch.name);
        mActionBar.setTitle(model.currentReport.name);
        mActionBar.setHomeButtonEnabled(true);


        //TODO: gоказать прогрессс потом в UI потоке обработать конструктор форм
        //String id_partner = Integer.toString(model.currentReport.id);
        Log.d("dataForms", "Quering data for patner " + model.currentReport.id);

        setSupportProgressBarIndeterminateVisibility(true);

        ParseQuery<rawFormContainer> query = ParseQuery.getQuery("rawFormContainer");
        query.whereEqualTo("id_partner", model.currentReport.id);
        query.whereEqualTo("id_activity", model.currentActivityId);
        query.whereEqualTo("id_region", model.currentRegionId);
        query.findInBackground(new FindCallback<rawFormContainer>() {

            public void done(List<rawFormContainer> formList, ParseException e) {


                setSupportProgressBarIndeterminateVisibility(false);


                if (e == null) {
                    Log.d("dataForms", "Retrieved " + formList.size() + " records");

                    //тут сказать что нет или показать форму
                    if(formList.size()==0){
                        showOrderNotFoundDialog();

                    }   else{
                        form = formList.get(0);
                    }

                    BuildForm();

                } else {
                    Log.d("dataForms", "Error: " + e.getMessage());

                    showOrderNotFoundDialog();


                }
            }

        });


        //далее асинхронно запрашиваем форму для вида деятельности
        //по полученной форме выгребаем страницы
        //группы и прочее
        /*назначаем адаптеры в зависимости от типа данных
        логика
        генерим страницы, потом смотрим количество - более трех - создаем пейджер адаптер и прочую требуху
         */





        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(SMSReceiver.MESSAGE_NEW_SMS);

        this.receiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(SMSReceiver.MESSAGE_NEW_SMS))
                {
                    int str = intent.getIntExtra("code",0);
                    Log.i("SMS", "SMS received code "+str);

                    if(confirmCode != null){

                        confirmCode.setText(""+str);

                        if(str==confirmCodeSMS){
                            //подтверждено
                            //выставить флаг и обновить видимость

                            //confirmCode.setEnabled(false);
                            if(btnConfirm!=null)btnConfirm.setVisibility(View.GONE);
                            confirmCode.setVisibility(View.GONE);
                            confirmed = true;

                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);

                            Toast.makeText(pageOrders.this ,"НОМЕР  подтвержден", Toast.LENGTH_LONG).show();

                        }



                    }
                }
            }
        };
        registerReceiver(this.receiver, localIntentFilter);

    }


    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    @UiThread
    void showOrderNotFoundDialog(){

        new AlertDialog
                .Builder(pageOrders.this)

                .setCancelable(false)
                .setTitle("Заказать через оператора")
                .setMessage("По этому контрагенту услугу можно заказать только через оператора.")
                .setPositiveButton("Позвонить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        dialog.dismiss();
                        finish();

                        try {

                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + model.getOperatorPhone()));
                            pageOrders.this.startActivity(callIntent);


                        } catch (ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();

    }

    @UiThread
    void showOrderCompleteDialog(int i){

        String message = String.format("По заказу %s с вами свяжется исполнитель в срок от 1 до 59 минуты. ",i);

        new AlertDialog
                .Builder(pageOrders.this)

                .setCancelable(false)
                .setTitle("Приступили к выполнению")
                .setMessage(message)
                .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();

    }


    @UiThread
    void BuildForm(){

        if(form==null) return;
        hasContact = false;


        try {

            byte[] jsondata = Base64.decode(form.getFormData(), Base64.DEFAULT);
            String data =  new String(jsondata, "UTF-8");

            byte[] descrdata = Base64.decode(form.getFormDescription(), Base64.DEFAULT);
            formDescription =  new String(descrdata, "UTF-8");

            Log.d("dataForms", "Retrieved data:" + data);
            Log.d("dataForms", "Retrieved Descr:"+formDescription);


            ObjectMapper mapper = new ObjectMapper();
            formData = mapper.readValue(jsondata, pocoForm.class);

            //Заносим параметры формы
            //затем по реквизитам цикл - берем Fields и рекурсивно + контейнер владелец

            ViewGroup root1 = new ScrollView(this);
            root1.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            //--------------------------------------------
            /*
            TextView tv = fb.addTextView(this, ll);
            tv.setText(objform.optString("form_name"));
            int pad = dpToPixels(8);
            tv.setPadding(pad,pad,pad,pad);
            tv.setTextSize(dpToPixels(14));
            tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);*/
            //---------------------------------------------

            enumerateFields(formData.Fields, ll);

            if(!hasContact){
                addPhoneField("", "Контактный телефон", "", ll);;
            }


            //----------------------------------------------
            //Описание услуги
            if(!formDescription.isEmpty()){

                TextView tv = new TextView(this);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
                tv.setText("Как происходит выполнение услуги:");
                tv.setBackgroundColor(0xfff6f6f6);
                int padding = dpToPixels(8);
                tv.setPadding(padding, padding, padding,padding);
                ll.addView(tv);

                TextView tv1 = new TextView(this);
                tv1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
                tv1.setText(formDescription);
                tv1.setBackgroundColor(0xfff6f6f6);

                tv1.setPadding(padding, padding, padding,padding);
                ll.addView(tv1);

            }


            root1.addView(ll);

            root.addView(root1);  //ll

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



    public TextView addTextView(Context ctx, ViewGroup group){
        TextView tv = new TextView(ctx);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
        int padding = dpToPixels(8);
        tv.setPadding(padding, padding, padding,padding);
        //tv.setId(++i);
        group.addView(tv);
        return tv;
    }


    void enumerateFields(pocoFieldList fields, ViewGroup group){

        if(fields == null) return;

        for(pocoFormField f:fields){
            Log.w("dataForms", "field="+f.label );

            //генерим поля ввода в текущем контейнере
            //Группа, поле, ТекстоваяМетка, Страницы, страница

            if(f.object_type.equalsIgnoreCase("группа")){

                LinearLayout ll = LinearLayoutBuilder.Create(this)
                        .withOrientation(f.group_orientation.equalsIgnoreCase("vertical")?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL)
                        .withMatchWidth()
                        .Build();

                //перестроение в столбец на мобильном
                if(f.rebuildOnMobile && f.group_orientation.equalsIgnoreCase("horizontal"))  ll.setOrientation(LinearLayout.VERTICAL);

                //TODO: группа важное и заголовок группы !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //TODO: для важной надо рамку - для это устанавливать бакграунд - нарисовать нада в векторе


                //Группа имеет подсказку - добавим метку комментария
                if(!f.hint.isEmpty()  || !f.label.isEmpty()  ) {

                    //Горизонтальная группа - значит надо допилить - воткнуть контейнер для нее и меток

                    if(ll.getOrientation()==LinearLayout.HORIZONTAL){

                        // /горизонтальная - создадим контейнер для метки
                        LinearLayout ll_container = LinearLayoutBuilder.Create(this)
                                .withOrientation(LinearLayout.VERTICAL)
                                .withMatchWidth()
                                .Build();
                        if(f.important) ll_container.setBackgroundColor(0xfff6f6f6);

                        if(!f.label.isEmpty()){
                            TextView tv = addTextView(this, ll_container);
                            tv.setTextColor(0xff990000);
                            tv.setTextSize(dpToPixels(10));
                            tv.setText(f.label);
                        }

                        if(!f.hint.isEmpty()){
                            TextView tv = addTextView(this, ll_container);
                            tv.setTextColor(0xff999999);
                            tv.setText(f.hint);
                        }



                        //а поля уже в родной контейнер
                        enumerateFields(f.Fields, ll);

                        if(ll.getChildCount()>0){
                            ll_container.addView(ll);
                            group.addView(ll_container);
                        }



                    }else{

                        if(f.important) ll.setBackgroundColor(0xfff6f6f6);
                        //Вертикальная  - без проблем добавляем и заголовок и подсказку

                        if(!f.label.isEmpty()){
                            TextView tv = addTextView(this, ll);
                            tv.setTextColor(0xff990000);
                            tv.setTextSize(dpToPixels(10));
                            tv.setText(f.label);
                        }

                        if(!f.hint.isEmpty()){
                            TextView tv = addTextView(this, ll);
                            tv.setTextColor(0xff999999);
                            tv.setText(f.hint);
                        }


                        enumerateFields(f.Fields, ll);

                        //непустые
                        if(ll.getChildCount()>0)  group.addView(ll);
                    }
                }else{

                    enumerateFields(f.Fields, ll);
                    if(ll.getChildCount()>0)  group.addView(ll);

                }
                //а с пустым хинтом ?????



            }else if(f.object_type.equalsIgnoreCase("ТекстоваяМетка")){
                TextView tv = addTextView(this, group);
                tv.setText(f.label);

                //TODO: а еще плейсхолдер дескрипшена?

            }else if(f.object_type.equalsIgnoreCase("полеввода")){

                //булево, дата, время, строка и два типа числа
                //справочник
                createField(f,group);

            }else if(f.object_type.equalsIgnoreCase("страницы")){

                //коллекция страниц - тут чего делать ?
                //либо заводить несколько - в каждую добавлять контейнер в который пускать реквизиты
                //пока считаем что коллекция одна и все страница маппим в нее


                enumerateFields(f.Fields, group); //с той же группой


                //----------------------------------------------------
                OrdersPagerAdapter pagerAdapter = new OrdersPagerAdapter(pages);

                ViewPager pager = new ViewPager(this);
                pager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPixels(180)));
                pager.setAdapter(pagerAdapter);
                pager.setCurrentItem(1);


                //Создание вкладки с закладками
                PagerSlidingTabStrip tabs = new PagerSlidingTabStrip(this);
                tabs.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tabs.setMinimumHeight(dpToPixels(32));
                tabs.setViewPager(pager);

                //это стилем надо
                //if(group.getChildCount()>1) tabs.setPadding(0,dpToPixels(32), 0, dpToPixels(8));


                group.addView(tabs);
                group.addView(pager);

                pager.setCurrentItem(0);


            }else if(f.object_type.equalsIgnoreCase("страница")){

                //скроллвью + контейнер - каждая страница идет контейнером
                //так что та же вложенность

                ScrollView swPage = new ScrollView(this);
                swPage.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                LinearLayout lp = new LinearLayout(this);
                lp.setOrientation(LinearLayout.VERTICAL);
                lp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    enumerateFields(f.Fields, lp);

                swPage.addView(lp);
                pages.put(f.label,swPage);

            }



        }
    }



    void createField(pocoFormField f,  ViewGroup group){

        final Boolean oldAndroid = !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);

        int pad16 = dpToPixels(16);
        int pad8 = dpToPixels(8);
        int pad4 = dpToPixels(4);

            //---------------------------------------
            String fName = f.label;
            String fType = f.field_type;
            String fTypeName = f.field_type_name;
            String key = f.uid;
            Log.w("dataForms", String.format("%s field: %s %s %s ", key ,fName, fType, fTypeName));
            //---------------------------------------



            if(fType.equalsIgnoreCase("дата") || fType.equalsIgnoreCase("время")){

                //-----------------------------------------------------
                //  ДАТА или ВРЕМЯ
                //-----------------------------------------------------
                //http://www.mkyong.com/android/android-date-picker-example/
                //http://developer.alexanderklimov.ru/android/views/datepicker.php
                //http://www.startofandroid.com/ru/uroki/vse-uroki-spiskom/118-urok-59-dialogi-datepickerdialog.html


                //Горизонтальная группа
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                //В ней текстовая метка
                addTextView(this, ll).setText(fName);

                //Далее стилизованная кнопка для диалога выбора даты
                final boolean isDate = fType.equalsIgnoreCase("дата");
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);
                final int mHour = c.get(Calendar.HOUR_OF_DAY);
                final int mMinute = c.get(Calendar.MINUTE);


                final Button btn = new Button(this);
                btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ispinner));

                btn.setTag(f.uid);
                if(isDate){
                    btn.setText(String.format("%s.%s.%s", mDay, mMonth+1,mYear));
                }
                else{
                    btn.setText(String.format("%s:%s", mHour, mMinute));
                }
                ll.addView(btn);

                ll.setPadding(pad4, pad4, pad4, pad4);
                group.addView(ll);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isDate){

                            DatePickerDialog tpd = new DatePickerDialog(pageOrders.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    btn.setText(String.format("%s.%s.%s", dayOfMonth, monthOfYear+1,year));
                                }
                            }, mYear, mMonth, mDay);
                            if(!oldAndroid) tpd.getDatePicker().setCalendarViewShown(false);
                            tpd.show();

                        }else{

                            TimePickerDialog tpd = new TimePickerDialog(pageOrders.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    btn.setText(String.format("%s:%s", hourOfDay, minute));
                                }
                            }, mHour,mMinute, true);

                            tpd.show();
                        }

                    }
                });

                btn.setTag(f.uid);
                datafields.put(f.uid, btn);

            }else if(fType.equalsIgnoreCase("телефон")){

                addPhoneField(f.label, f.hint, f.uid, group);
                hasContact = true;

            }else if( fType.equalsIgnoreCase("строка")
                    || fType.equalsIgnoreCase("целое")
                    || fType.equalsIgnoreCase("дробное") ){

                //для строки - определять ориентацию заголовка (сверху, сбоку) или для мобилы сверху + хинт
                //посмотреть параметры у поля ввода - там что то было на этот счет

                //TODO: формат числа (знаков после запятой) - точнее два типа - целое - дробное
                //-----------------------------------------------------
                // СТРОКА И ЧИСЛО
                //-----------------------------------------------------

                if(!f.label.isEmpty()) addTextView(this, group).setText(fName);

                EditText tv = new EditText(this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(lp);
                if(f.multiline) tv.setMinLines(5);
                if(!f.hint.isEmpty()) tv.setHint(f.hint);

                int padding = dpToPixels(8);
                tv.setPadding(padding, padding, padding, padding);

                //if(oldAndroid)
                    tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.ifield));
                //else
                //    tv.setBackground(getResources().getDrawable(R.drawable.ifield));

                if( fType.equalsIgnoreCase("целое") || fType.equalsIgnoreCase("дробное")  || fType.equalsIgnoreCase("телефон") ) {
                    //android:numeric="decimal"
                    //tv.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    //tv.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL); //цело-дробное
                    tv.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                }

                tv.setTag(f.uid);
                datafields.put(f.uid, tv);

                group.addView(tv);


            }else if(fType.equalsIgnoreCase("булево")){

                CheckBox chk = new CheckBox(this);
                chk.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
                chk.setText(fName);
                chk.setTag(f.uid);
                datafields.put(f.uid, chk);
                group.addView(chk);

            }else if(fType.equalsIgnoreCase("справочник")){

                //создаем адаптер - загружаем в него values
                //http://developer.alexanderklimov.ru/android/views/spinner.php

                addTextView(this, group).setText(fName);

                Spinner sp = new Spinner(this, Spinner.MODE_DIALOG); //TODO: выбирать режим выбора
                sp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                sp.setBackgroundDrawable(getResources().getDrawable(R.drawable.ispinner));

                final FormFieldArrayAdapter adapter = new FormFieldArrayAdapter(this, R.layout.spinner_item, f.values);
                sp.setAdapter(adapter);

                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        adapter.flag = true;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                group.addView(sp);


            }else{

                //справочник
                addTextView(this, group).setText(fName);
                //EditText et = fb.addEditText(this,group);

                //обход данных списка и симпл адаптер

                //et.setPadding(pad,0,pad,0);
                //et.setHint(fName);
                //et.setBackground(getResources().getDrawable(R.drawable.selector_border));

            }

    }


    void addPhoneField(String label, String hint, String uid, ViewGroup group){

        if(!label.isEmpty()) addTextView(this, group).setText(label);

        int padding = dpToPixels(8);

        LinearLayout.LayoutParams lp = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,padding,0,0);

        phoneNumberText = new EditText(this);
        phoneNumberText.setLayoutParams(lp);
        phoneNumberText.setBackgroundDrawable(getResources().getDrawable(R.drawable.ifield));
        phoneNumberText.setRawInputType(InputType.TYPE_CLASS_NUMBER);


        //if(Account.phone==null)
        //{
            //----------- Получаем номер телефона для подстановки в поле регистрации
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            if(telephonyManager!=null){
                String number =  telephonyManager.getLine1Number();
                if(number!=null)
                    if(!number.equals(""))
                        phoneNumberText.setText(number);

            }
        //}


        if(!hint.isEmpty()) phoneNumberText.setHint(hint);

        datafields.put(  uid.isEmpty()? CONTACT :uid, phoneNumberText);

        group.addView(phoneNumberText);

        //TODO: Интент для подтверждения
        //два варианта - подставлять телефон если был подтвержденный, при очистке показывать
        //данные от парса - имеет ли подтвержденный телефон

        addTextView(this, group).setText("На указанный номер будет отправлено СМС с кодом подтверждения");

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(padding, padding, padding, padding);
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        btnConfirm = new Button(this);
        btnConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_ya_selector));
        btnConfirm.setText("Получить код");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!phoneNumberText.getText().toString().trim().isEmpty())  sendSMS();
                //Toast.makeText(pageOrders.this, "code "+confirmCodeSMS, Toast.LENGTH_LONG ).show();

            }
        });
        ll.addView(btnConfirm);

        confirmCode = new EditText(this);
        LinearLayout.LayoutParams lp2 = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(padding,dpToPixels(4),0,0);
        confirmCode.setLayoutParams(lp2);
        confirmCode.setHint("Код подтверждения");
        //confirmCode.setPaddingRelative(padding, padding, padding, padding); //не работает на старых андроид
        confirmCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.ifield));
        confirmCode.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        ll.addView(confirmCode);

        group.addView(ll);

    }



    @Background
    void sendSMS(){

        startProgress();

        confirmCodeSMS = (new Random().nextInt(1000));



        svcSMSSender sms = new svcSMSSender_();
        try {

            //
            String message = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<data>\n" +
                    "<login>BeQuick</login>\n" +
                    "<password>BeQuick1</password>\n" +
                    "<action>send</action>\n" +
                    "<text>"+String.format("%s",confirmCodeSMS)+" vash kod podtverjdenia</text>\n" +
                    "<to number='"+phoneNumberText.getText().toString()+"'></to>\n" +
                    "</data>";

            //message = URLEncoder.encode(message,"UTF-8");
            sms.send(message);

            sendOk();

/*
        } catch (UnsupportedEncodingException e) {

            sendFalse();
*/

        } catch (RestClientException e) {

            sendFalse();

        }

        stopProgress();
    }


    @UiThread
    void startProgress(){
        setSupportProgressBarIndeterminateVisibility(true);
    }

    @UiThread
    void stopProgress(){
        setSupportProgressBarIndeterminateVisibility(false);
    }

    @UiThread
    void sendOk(){
        Toast.makeText(pageOrders.this, "сообщение отправлено", Toast.LENGTH_LONG).show();
    }

    @UiThread
    void sendFalse(){
        Toast.makeText(pageOrders.this, "ОШИБКА ПРИ ОТПРАВКЕ", Toast.LENGTH_LONG).show();
    }

    //==================================================================================================================
    //Публикация данных
    //==================================================================================================================

    boolean allDataComplete = true;

    @OptionsItem(R.id.mnu_confirm_order)
    void menuConfirm() {

        if(formData==null) return;
        setSupportProgressBarIndeterminateVisibility(true);

        hasContact = false;
        allDataComplete = true;

        final pocoOrder order = new pocoOrder();
        //TODO: тестовая или нет форма
        order.ChannelId = ParseInstallation.getCurrentInstallation().getInstallationId();
        buildDataForSend(formData.Fields, order);

        if(!hasContact){
            EditText v = (EditText)datafields.get(CONTACT);
            String val = v.getText().toString().trim();
            order.Contact = val;
            if(val.isEmpty()) allDataComplete = false;
        }

        if(!confirmed)
            if(confirmCode.getText().toString().equalsIgnoreCase(""+confirmCodeSMS)) confirmed=true;

        if(!allDataComplete || !confirmed) {
            setSupportProgressBarIndeterminateVisibility(false);
            Toast.makeText(pageOrders.this, "Не заполнены необходимые данные.\r\nДействие отменено.", Toast.LENGTH_LONG).show();
            return;
        }



        final int[] num = {0};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("OrdersNum");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null){

                    try {

                        //получаем текущий заказ -----------------
                        object.increment("currentNumber");
                        num[0] = object.getInt("currentNumber");
                        object.saveInBackground();

                        //формируем контейнер --------------------
                        rawOrdersData parse_data = new rawOrdersData();
                        ParseACL acl = new ParseACL();
                        acl.setPublicReadAccess(true);
                        parse_data.setACL(acl);


                        order.id_form = formData.form_id;
                        order.id_partner = model.currentReport.id;
                        order.number = num[0];
                        order.id_region = model.currentRegionId;


                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(order);


                        Log.w("dataForms", String.format("Данные на отправку %s", jsonString ));
                        byte[] binary_data = jsonString.getBytes("UTF-8");
                        parse_data.setOrderData( Base64.encodeToString(binary_data, Base64.DEFAULT) );
                        parse_data.setProcess(false);


                        parse_data.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                setSupportProgressBarIndeterminateVisibility(false);
                                showOrderCompleteDialog(num[0]);

                            }
                        });


                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (JsonProcessingException e2) {
                        e2.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }



                }
            }
        });





    }



    //TODO: Определять тип реквизита по другому - без ветвлений а typeOf
    void buildDataForSend(pocoFieldList fields, pocoOrder order){

        for(pocoFormField f:fields){

            //если объект имеет тег - то в зависимости
            if(datafields.containsKey(f.uid)){

                View v = datafields.get(f.uid);
                //проверять типы и парсить


                if(f.field_type.equalsIgnoreCase("дата") || f.field_type.equalsIgnoreCase("время")){
                    String val = ((Button) v).getText().toString().trim();

                    if(f.required && val.isEmpty()) allDataComplete = false;

                    order.createItem(f.uid, val );
                }else

                if(f.field_type.equalsIgnoreCase("строка")
                        || f.field_type.equalsIgnoreCase("целое")
                        || f.field_type.equalsIgnoreCase("дробное") ){

                    String val = ((EditText) v).getText().toString().trim();
                    if(f.required && val.isEmpty()) allDataComplete = false;

                    order.createItem(f.uid, val );
                }else
                if(f.field_type.equalsIgnoreCase("булево")){
                    order.createItem(f.uid, ((CheckBox) v).isChecked() );
                }else
                if(f.field_type.equalsIgnoreCase("справочник")){
                    Log.w("dataForms", String.format("Справочник %s = %s", f.uid, ((EditText)v).getText()  ));

                   //тут надо брать объект - от него отталкиваться - что выбрано

                }else
                if(f.field_type.equalsIgnoreCase("телефон")){

                    String val = ((EditText) v).getText().toString().trim();
                    if(val.isEmpty()) allDataComplete = false;

                    order.createItem(f.uid, val );
                    order.Contact = val;
                    hasContact = true;
                }

                //TODO: API



            }// есть uid


            if(f.Fields != null) buildDataForSend(f.Fields, order);  //рекурсия
        }


    }


    //Объекты на вынос за пределы класса ===============================================================================


    public int dpToPixels(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    public class OrdersPagerAdapter extends PagerAdapter {

        HashMap<String,View> pages = null;

        public OrdersPagerAdapter(HashMap<String, View> pages){
            this.pages = pages;
        }

        @Override
        public Object instantiateItem(View collection, int position){
            View v = pages.get((pages.keySet().toArray())[position]);


            ((ViewPager) collection).addView(v, 0);
            return v;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return pages[position]
            // ;
            return (CharSequence) (pages.keySet().toArray())[position];
        }

        @Override
        public void destroyItem(View collection, int position, Object view){
            ((ViewPager) collection).removeView((View) view);
        }

     /*   @Override
        public void startUpdate(View arg0){
        }


        @Override
        public void finishUpdate(View arg0){
        }
*/

        @Override
        public int getCount(){
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object){
            return view.equals(object);
        }


        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1){
        }

        @Override
        public Parcelable saveState(){
            return null;
        }



    }



}