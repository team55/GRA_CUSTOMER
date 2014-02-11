package ru.team55.gra.api;


import android.content.Context;
import android.widget.ArrayAdapter;
import ru.team55.gra.api.pocoObjects.*;
import ru.team55.gra.rating.R;

import java.util.LinkedList;
import java.util.List;

public class model {

    static{
        currentSearch = new LinkedList<pocoSearchItem>();
    }
    public static String leafURL = "";


    //ГОРОДА ---------------------------------------------
    public static pocoTownList towns = new pocoTownList();
    private static ArrayAdapter towns_adapter;
    public static ArrayAdapter getTownsAdapter(Context context){
        //towns_adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item, towns);
        towns_adapter = new ArrayAdapter(context, R.layout.spinner_item, towns);
        //towns_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return towns_adapter;
    }


    //ФОРМЫ ОТЧЕТОВ ---------------------------------------------
    public static pocoSearchItemList kindsOfReportDetails = new pocoSearchItemList();
    private static ArrayAdapter kindsOfReportDetails_adapter;
    public static ArrayAdapter getkindsOfReportDetailsAdapter(Context context){
        //kindsOfReportDetails_adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item, kindsOfReportDetails);
        kindsOfReportDetails_adapter = new ArrayAdapter(context,R.layout.spinner_item, kindsOfReportDetails);
        //kindsOfReportDetails_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return kindsOfReportDetails_adapter;
    }


    public static void setFilterItem(pocoSearchItem item){

        if(item.type.equalsIgnoreCase("адрес")){
            currentPositionId = item.id;
        }else{
            currentActivityId = item.id;
            currentPositionId = 0; //первый токен
        }

    }



    //ВЫБРАННЫЕ ПАРАМЕТРЫ----------------------------------------
    public static int currentRegionId = 0;
    public static int currentKindsOfReportDetailsId = 0;

    public static int currentActivityId = 0; //вид деятельности
    public static int currentPositionId = 0; //позиция




    //Объекты времени выполнения----------------------------------------
    public static pocoReportItem currentReport;
    public static List<pocoSearchItem> currentSearch; //задействовано при переходе из истории

    //надо бы модель вью организовать - представления объектов и тд

    //зачаток mvc - для поиска фиксируем
    public static pocoSearchItem  currentActivitySearch;
    public static pocoSearchItem currentAddressSearch;
    public static pocoTown currentRegion;



    public static String getOperatorPhone(){
        //запросить при старте приложения по региону, и схранить - подставить дефолтное если отсутствует
        return "+79136282768";
    }


    public static String getBalanceInfo(){
        return "1000 (300)";
    }


    //Работа с избранным - запрашивать всегда?

}
