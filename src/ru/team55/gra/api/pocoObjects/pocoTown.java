package ru.team55.gra.api.pocoObjects;

/*** Объект описатель города и URL сервиса*/
public class pocoTown {

    public int id = 0;
    public String name = "";
    public int territory_id;
    public String url;

    @Override
    public String toString(){
        return name;
    }


}
