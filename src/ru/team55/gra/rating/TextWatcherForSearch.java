/*
package ru.team55.gra.rating;


import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.util.Timer;

*/
/*назначение - стартовать поиск по таймеру и изменить набор данных адаптера поиска *//*



public class TextWatcherForSearch implements TextWatcher {

    private final String TAG = TextWatcherForSearch.class.getSimpleName();

    private View view;
    private SearchItemAdapter adapter;

    Timer textChengedTimer;

    private TextWatcherForSearch(View view, SearchItemAdapter adapter) {
        this.view = view;
        this.adapter = adapter;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Log.i(TAG, "text changed");

        shouldAutoComplete = true;

        if(s.toString().length()<3)
        {
            Log.i(TAG, "shouldAutoComplete = false (1)");
            shouldAutoComplete = false;
            return;
        }


        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).name.equalsIgnoreCase(s.toString())) {
                Log.i(TAG, "shouldAutoComplete = false (2)");

                //TODO: выставить значение позиции в нужный объект

                shouldAutoComplete = false;
                break;
            }
        }


    }



    boolean autocompleteDisabled = true;


    @Override
    public void afterTextChanged(final Editable s) {

        //Запрос данных (надо знать тип реквизита)
        if (shouldAutoComplete && !autocompleteDisabled) {

            Log.i(TAG, "Prepare search");
            Log.i(TAG, "ID=" + view.getId());

            int queryid = 0;
            int territoryid = model.getTerritoryId();

            switch (view.getId())
            {
                case  R.id.editConsumerPlace:
                    queryid = 2;
                    break;
                case  R.id.editKindOfActivity:
                    queryid = 3;
                    break;
                case  R.id.editSupplierForRotation:
                case  R.id.editSupplier:
                    queryid = 5;
                    break;
            }

            //проверяем запущен ли таймер
            if(textChengedTimer!=null){
                Log.i(TAG, "Cancel search "+textChengedTimer.purge());

                textChengedTimer.cancel(); //остановим
                textChengedTimer=null;
                //textChengedTimer.purge(); //очистим очередь заданий
            }

            textChengedTimer = new Timer();

            final int qID = queryid;
            final int tID = territoryid;

            textChengedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final String search_string = s.toString();
                    if(search_string.length()>2)
                    {
                        Log.i(TAG, "Start search:"+search_string);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AutoCompleteSearchTask(adapter, qID, tID).execute(search_string);
                            }
                        });

                    }
                }
            }, 1000);




        }

        //if(s.length()==5)
        //{
//	        		String estring = "Неправильный номер";
//	        		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
//
//	        		ForegroundColorSpan fgcspan1 = new ForegroundColorSpan(0xFF000000); //black
//	        		ForegroundColorSpan fgcspan2 = new ForegroundColorSpan(0xFFFF0000); //red
//
//	        		ssbuilder.setSpan(fgcspan1, 0, 12, 0);
//	        		ssbuilder.setSpan(fgcspan2, 13, estring.length(), 0);
//
//	        		//edt.setError(ssbuilder);
//
//	        		Drawable errorIcon = getResources().getDrawable(R.drawable.login_error);
//	        		errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
//	        		edLogin.setError(ssbuilder,errorIcon);
//
//	        		//edt.setError("Неправильный номер",getResources().getDrawable(R.drawable.login_error));
        //((EditText)s).setError("Неправильный номер");
        //}
        //else
        //	((EditText)s).setError(null);

    }
}

*/
