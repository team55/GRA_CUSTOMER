package ru.team55.gra.api;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import ru.team55.gra.api.pocoObjects.pocoReportItem;

//http://agent-russia.ru/omsk
@Rest(converters = {MappingJackson2HttpMessageConverter.class})
public interface svcReports {


/*
    @Get("/ajax/s.server.functions.php?type=1&form_activity_id={activity_id}&culture_id={culture_id}&territory_id={territory_id}&ten=1&sort_rank=1")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoReportItem[] getRatings(int activity_id, int culture_id, int territory_id);
*/

    @Get("/ajax/s.server.functions.php?type=1&form_activity_id={activity_id}&ten=1&sort_rank=1&culture_id=2&page={page}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoReportItem[] getRatings(int activity_id, int page);

/*
    @Get("/ajax/s.server.functions.php?type=1&form_activity_id={activity_id}&position_buyer_id={position_id}&culture_id={culture_id}&territory_id={territory_id}&ten=1&sort_rank=1")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoReportItem[] getRatingsByTerritory(int activity_id, int position_id ,int culture_id, int territory_id);
*/

    @Get("/ajax/s.server.functions.php?type=1&form_activity_id={activity_id}&position_buyer_id={position_id}&culture_id=2&ten=1&sort_rank=1&page={page}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoReportItem[] getRatingsByTerritory(int activity_id, int position_id, int page);

    //справка
/*
    @Get("/ajax/s.server.functions.php?type=2&point_salesmen_id={point_salesman_id}&culture_id={culture_id}&territory_id={territory_id}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoReportItem[] getInfo(int point_salesman_id, int culture_id, int territory_id);
*/

    //form_activity_id=1375

    @Get("/ajax/s.server.functions.php?type=2&point_salesmen_id={point_salesman_id}&culture_id=2&form_activity_id={form_activity_id}")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoReportItem[] getInfo(int point_salesman_id, int form_activity_id);


    @SuppressWarnings("UnusedDeclaration")
    void setRootUrl(String rootUrl);
}
