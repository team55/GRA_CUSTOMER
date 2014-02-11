package ru.team55.gra.api;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import ru.team55.gra.api.pocoObjects.pocoAdressList;
import ru.team55.gra.api.pocoObjects.pocoSearchItemList;


//http://agent-russia.ru/omsk
@Rest(converters = {MappingJackson2HttpMessageConverter.class})
public interface svcLeaf {

    //TODO: Фактически не нужно - города и ID запрашивается от центральной точки
/*
    @Get("/ajax/j.server.php?type=1")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoSearchItem[] getRegions();
*/

/*
    @Get("/ajax/j.server.php?type=1")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoSearchItemList getRegions();
*/

    @Get("/ajax/j.server.php?type=2&synonym={synonym}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoSearchItemList getAdresses(String synonym);

    //Поставщик (точка и адрес)
    @Get("/ajax/j.server.php?type=5&synonym={synonym}") //&territory_id={territory}
    @Accept(MediaType.APPLICATION_JSON)
    public pocoSearchItemList getSupplier(String synonym);

    //Вид деятельности
    @Get("/ajax/j.server.php?type=3&synonym={synonym}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoSearchItemList getKindOfActivity(String synonym);


/*
    "type=7&author_rotation=Александр&types_rotation=3&point_salesman=356&opinion=Добрый день. Я хотел оставить отзыв"
    где:
    type - всегда 7;
    author_rotation - Автор отзыва (string);
    types_rotation - id типа отзыва (integer);
    point_salesman - id точки контрагента (integer);
    opinion - Текст сообщения (text);
*/

    //todo: тут парамтров не хватает
    //Отправка отзыва
    @Post("/ajax/j.server.php?type=7")
    //@Accept(MediaType.APPLICATION_JSON)
    public void sendRotation();


    //Вид отчета (культуры)
    @Get("/ajax/j.server.php?type=8")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoSearchItemList getReportForms();


    //Поиск адреса
    @Get("/ajax/j.server.php?type=11&latitude={latitude}&longitude={longitude}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoAdressList getFirstAdress(String latitude, String longitude);


    //Настройки отчетов
    //@Get("/ajax/j.server.php?type=9")
    //@Accept(MediaType.APPLICATION_JSON)
    //public pocoSearchItem[] getReportForm();


/*
    // OK
    @Get("/events/{year}/{location}")
    EventList getEventsByYearAndLocation(int year, String location);

    // OK
    @Get("/events/{year}/{location}")
    EventList getEventsByLocationAndYear(String location, int year);


     @Post("/events")
     Event addAndReturnEvent(Event event);

     Of course, you can send a POST request without sending any entity.
     @Post("/events")
     void addEvent();



     try {
         myRestClient.getEvents("fr", 2011);
        } catch (RestClientException e) {
            // handle error for Rest client exceptions
        }

*/


    @SuppressWarnings("UnusedDeclaration")
    void setRootUrl(String rootUrl);
}
