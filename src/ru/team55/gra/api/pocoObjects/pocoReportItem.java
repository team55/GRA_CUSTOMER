package ru.team55.gra.api.pocoObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.team55.gra.api.pocoObjects.pocoOperatingMode;
import ru.team55.gra.api.pocoObjects.pocoReportDetails;
import ru.team55.gra.api.pocoObjects.pocoReportInfo;

@JsonIgnoreProperties("declaration")
public class pocoReportItem {
    /*  declaration: []  ???
--------------  СПРАВКА -------------------------
count_arr: 736   -- откуда
declaration: []
dolgota: "37.7647604400000"
id: "4000030"
information: [{name:Адрес, value:Москва, Новомарьинская, 18, вход со двора},…]
0: {name:Адрес, value:Москва, Новомарьинская, 18, вход со двора}
name: "Адрес"
value: "Москва, Новомарьинская, 18, вход со двора"
1: {name:Наименование, value:ААА-Тюнинг, торговая компания}
name: "Наименование"
value: "ААА-Тюнинг, торговая компания"
2: {name:Сайт, value:agent-russia.ru/moscow/ajax/j.server.php?type=10&http=aaa-tuning.ru}
name: "Сайт"
value: "agent-russia.ru/moscow/ajax/j.server.php?type=10&http=aaa-tuning.ru"
3: {name:Телефон, value:84959407243}
name: "Телефон"
value: "84959407243"

name: "ААА-Тюнинг, торговая компания"
name_fa: "Автоаксессуары"
shirota: "55.6564679900000"
totalRank: 1



operating_modes: [{day_week_integer:1, day_week_string:Понедельник, operating_mode:10:00 - 18:00},…]
0: {day_week_integer:1, day_week_string:Понедельник, operating_mode:10:00 - 18:00}
day_week_integer: "1"
day_week_string: "Понедельник"
operating_mode: "10:00 - 18:00"





    */

    public int count_arr = 0;


    public int id = 0;
    public String name = "";

    public int id_fa = 0; //в избранном появилось
    public String name_fa = "";

    public float shirota = 0f;
    public float dolgota = 0f;
    public int totalRank = 0;
    public int present = 0;

    // -- этого нет в объекте справка (или пока? нет)

    public float first_shirota = 0f;
    public float first_dolgota = 0f;
    public int distance = 0;

    public float rating;
    public float rating_d;
    public float rating_k;
    public float rating_l;

    public pocoReportInfo[] information;
    public pocoReportDetails[] rating_details_d;
    public pocoReportDetails[] rating_details_k;
    public pocoReportDetails[] rating_details_l;

    public pocoOperatingMode[] operating_modes;

}
