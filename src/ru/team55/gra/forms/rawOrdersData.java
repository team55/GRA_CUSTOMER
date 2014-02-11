package ru.team55.gra.forms;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("rawOrdersData")
public class rawOrdersData extends ParseObject {


    public String getOrderData() {
        return getString("form_data");
    }

    public void setOrderData(String data){
        put("form_data", data);
    }

    public Boolean getProcess(){
        return getBoolean("processed");
    }

    public void setProcess(Boolean processed){
        put("processed", processed);
    }


}
