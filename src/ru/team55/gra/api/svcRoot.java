package ru.team55.gra.api;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import ru.team55.gra.api.pocoObjects.pocoTownList;

/*** получает список городов и список сервисов
 * маппируется как джейсон объект*/
@Rest(converters = {MappingJackson2HttpMessageConverter.class})
public interface svcRoot {

    @Get("/j.server.php")
    @Accept(MediaType.APPLICATION_JSON)
    public pocoTownList getAllTowns();

    @SuppressWarnings("UnusedDeclaration")
    void setRootUrl(String rootUrl);
}
