package ru.team55.gra.rating;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultInt;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface app_settings {

    @DefaultBoolean(true)
    boolean firstRun();

    //Автор обращения
    @DefaultString("Анонимный пользователь")
    String name();

    @DefaultInt(2) //подробно
    int report_form();

    @DefaultInt(55)
    int region();
}
