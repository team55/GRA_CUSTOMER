package ru.team55.gra.api.pocoForms;

public class pocoOrder {
    /* со стороны Parse.com у нас присутствует - объект, статус обработано и данные формы и ид пользователя оформившего заказ
       данные формы - это объект содержащий
             - ид клиента
             - данные пользователя (телефон - реквизиты)
             - ид формы
             - номер заказа
             - список полей формы и значения

     */


    public int id_partner;
    public int id_region;
    public String id_form;
    public int number;
    public boolean fromMobile = true;

    public String Contact;
    public String ChannelId;

    public pocoOrderItemList Items = new pocoOrderItemList();

    public void createItem(String uid, Object value){
        Items.add(new pocoOrderItem(uid, value));
    }

}
