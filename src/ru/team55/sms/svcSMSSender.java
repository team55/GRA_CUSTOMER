package ru.team55.sms;




//[19.11.13, 12:56:15] alexander.vilvarion: там https://lcab.sms-uslugi.ru/integration/xml
/*
[19.11.13, 12:57:30] alexander.vilvarion: вот Женин (наш, уже рабочий) аккаунт "Зарегистрировался на http://sms-uslugi.ru/
        логин BeQuick пароль BeQuick1 на балансе 533 руб."


        [19.11.13, 12:54:46] alexander.vilvarion: http://sms-uslugi.ru/send/xml
        [19.11.13, 12:55:09] alexander.vilvarion: есть и другие способы вроде, сам апи у них в кабинете после регистрации
        [19.11.13, 12:55:27] alexander.vilvarion: можешь зайти vilvarion  testpass
*/


import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;
import org.apache.http.HttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;

import java.net.URLEncoder;

//@Rest(rootUrl = "https://lcab.sms-uslugi.ru/API/XML", converters = {StringHttpMessageConverter.class, SimpleXmlHttpMessageConverter.class})
@Rest(rootUrl = "https://lcab.sms-uslugi.ru/API/XML", converters = {StringHttpMessageConverter.class})
public interface svcSMSSender {

    @Post("/send.php")
    //@Accept(MediaType.APPLICATION_XML)
    @Accept(MediaType.TEXT_XML)
    void send(String data);
}
