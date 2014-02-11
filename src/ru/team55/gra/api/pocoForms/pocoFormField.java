package ru.team55.gra.api.pocoForms;


public class pocoFormField {

    public String uid = "";
    public String object_type = "";
    public String field_type = "";
    public String group_orientation = "vertical";

    //На будущее
    public String field_type_name = "";

    public String label = "";
    public Boolean important = false;
    public Boolean required = false;


    public Boolean multiline = false;
    public String hint = "";
    public int width = 0;

    public pocoFieldValues values;
    public pocoFieldList Fields;


    public boolean disabledOnMobile = false;
    public boolean rebuildOnMobile  = false;
    /*
    uid - идентификатор реквизита (только для полей ввода)
    label - метка (заголовок) поля ввода или группы, заполняется если не запрещен вывод и задано значение
    field_type  Дата, Число, Булево, Строка, Справочник (значения в массиве values), Адрес, ВидДеятельности, Город  (значения Адрес, ВидДеятельности, Город - будут использовать данные API)
    important - булево, выделить жирным метку или обвести группу рамкой
    object_type ПолеВвода, Страницы (группа для страниц), Страница, Группа, ТекстоваяМетка
    multiline - многострочное - имеет смысл для полей ввода с типом “Строка”
    hint - подсказка для поля ввода или группы
    group_orientation вид ориентации в группе - horizontal, vertical (не передается если тип vertical, - это значение используется по умолчанию)
    required - требовать проверки заполнения
    disabledOnMobile - признак что поле ввода не используется на мобильном устройстве
    rebuildOnMobile - признак для группы - значение истина разрешает изменить ориентацию группы на мобильном устройстве (имеет смысл для горизонтальной)
    values - массив объектов для поля ввода типа “справочник” (объекты состоят из полей uid и description)
*/



}
