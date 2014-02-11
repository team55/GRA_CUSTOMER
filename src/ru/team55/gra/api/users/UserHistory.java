package ru.team55.gra.api.users;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import org.json.JSONException;
import org.json.JSONObject;
import ru.team55.gra.api.Const;
import ru.team55.gra.api.model;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;

import java.util.List;


@ParseClassName("UserHistory")
public class UserHistory extends ParseObject {

    public String getDisplayName() {
        return getString("displayName");
    }

    //-----------------------------------------------
    public int getAdressId() {
        return getInt("adressId");
    }

    public String getAdressName() {
        return getString("adressName");
    }

    public int getActivityId() {
        return getInt("activityId");
    }

    public String getActivityName() {
        return getString("activityName");
    }
    //-----------------------------------------------


    public void setDisplayName(String value) {
        put("displayName", value);
    }


    @Override
    public String toString(){
        return getDisplayName();
    }


  /*  public void setData(int region, List<pocoSearchItem> items)  {

        put("regionId", region);
        put("adressId", 0);   //необязательное но для создания объекта надо
        put("adressName", "");

        StringBuilder sb = new StringBuilder();
        for(pocoSearchItem itm:items){
            sb.append(sb.length()>0?" + ":"");
            sb.append(itm.name);

            if(itm.type.equalsIgnoreCase(Const.PFX_ADRESS)){
                //адрес
                put("adressId", itm.id);
                put("adressName", itm.name);
            }else{
                //вид деятельности
                put("activityId", itm.id);
                put("activityName", itm.name);
            }


        }
        setDisplayName(sb.toString());

    }
*/

    public void setHistoryData(int regionId, int activityId, String activityPresentation, int adressId, String adressPresentation){

        put("regionId", regionId);
        put("adressId", adressId);
        put("adressName", adressPresentation);
        put("activityId", activityId);
        put("activityName", activityPresentation);

        setDisplayName(activityPresentation + (adressPresentation.isEmpty()?"":" + "+adressPresentation));

    }



}
