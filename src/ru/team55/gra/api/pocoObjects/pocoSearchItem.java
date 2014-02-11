package ru.team55.gra.api.pocoObjects;

public class pocoSearchItem {

    public int    id;
    public String name;
    public String type = ""; //адрес услуга

    //старое?
    //public String synonym;
    //public String address;

    public static pocoSearchItem Create(int id, String name, String type){
        pocoSearchItem item = new pocoSearchItem();
        item.id = id;
        item.name = name;
        item.type = type;
        return item;
    }

    @Override
    public String toString(){
        //return String.format("id=%s name=%s synonym=%s adress=%s",id,name,synonym,address);
        return name;
    }
}
