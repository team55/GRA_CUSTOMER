package ru.team55.gra.forms;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("rawFormContainer")
public class rawFormContainer extends ParseObject {

    public int getPartnerId() {
        return getInt("id_partner");
    }

    /*public String getFormId() {
        return getString("id_form");
    }*/

    public String getFormData() {
        return getString("form_data");
    }

    public String getFormDescription() {
        return getString("activity_description");
    }

}
