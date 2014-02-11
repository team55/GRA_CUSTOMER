package ru.team55.gra.rating;


import android.app.Application;
import android.util.Log;
import com.parse.*;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import ru.team55.gra.api.users.UserFavorites;
import ru.team55.gra.api.users.UserHistory;
import ru.team55.gra.forms.rawFormContainer;
import ru.team55.gra.forms.rawOrdersData;


//ACRA
@ReportsCrashes(formKey = "dFFWSWhlcko5X3E1MnBrSmVyeEZHQnc6MA")
public class app extends Application {

    //под гитхабом
    public static final String PARSE_APPLICATION_ID = "GlZyxNVIZYg99mMnn6wPxoegoDYBciNdpfSoA21i";
    public static final String PARSE_CLIENT_KEY = "fYusIm2PIgfTDRXtsTQ752o5vv5Df5DmrW8FvNu3";
    public static final String ROOT_URL = "http://agent-russia.ru/city";


    public static final int AUTH_REQUEST_CODE = 10001;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 10002;
    public static final int HISTORY_REQUEST_CODE = 10003;
    public static final int FAVORITES_REQUEST_CODE = 10004;


    @Override
    public void onCreate() {
        Log.d("app", "Application started");

        ACRA.init(this);

        super.onCreate();


        //-------------- parse.com -------------------------
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);


        PushService.setDefaultPushCallback(this, welcome.class);
        PushService.subscribe(this, "channel_for_"+ParseInstallation.getCurrentInstallation().getInstallationId(), welcome.class);


        //Подписываемся на канал связанный с интсалляцией, связываем ид инсталляции с заказом, потом оповещаем по этому каналу
        //ИЛИ канал это пользователь?
        //или делать по другому - генерить ид формы - подписываться на канал формы
        //при изменении статуса отписываться от канала (штобы не накапливалось)

        //ParseInstallation.getCurrentInstallation().getObjectId()
        //ParseInstallation.getCurrentInstallation().getInstallationId();


        ParseInstallation.getCurrentInstallation().saveInBackground(); //записываем с настройками

        //todo:  сменить на активити просмотра истории заказа
        //та в свою очередь будет иметь родительским активити наш велкоме класс (определить в манифесте и
        // по кнопке назад если стек возврата пуст возвращать в велкоме)

        ParseObject.registerSubclass(UserHistory.class);
        ParseObject.registerSubclass(UserFavorites.class);
        ParseObject.registerSubclass(rawFormContainer.class);
        ParseObject.registerSubclass(rawOrdersData.class);


        //--------------------------------------------------


        ParseACL defaultACL = new ParseACL();

        //Установка дает доступ всем
        //defaultACL.setPublicReadAccess(true);
        //For an application like Dropbox, where a user's data is only accessible by the user itself unless explicit permission is given, you would provide a default ACL where only the current user is given access:

        //установка в фалс запретит текущему пользователю читать
        //An application that logs data to Parse but doesn't provide any user access to that data would instead deny access to the current user while providing a restrictive ACL
        ParseACL.setDefaultACL(defaultACL, true);
        //--------------------------------------------------


    }
}
