package ru.team55.gra.api.users;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("UserFavorites")
public class UserFavorites extends ParseObject {

   /* public String getDisplayName() {
        return getString("displayName");
    }
*/
    //-----------------------------------------------

    public int getSupplierId() {
        return getInt("supplierId");
    }

    public String getSupplierPresentation() {
        return getString("supplierName");
    }


    public int getActivityId() {
        return getInt("activityId");
    }

    public String getActivityName() {
        return getString("activityName");
    }

    //-----------------------------------------------


    @Override
    public String toString(){
        return getString("displayName");
    }


    public void setData(int regionId, int activityId, String activityPresentation, int organizationId, String organizationPresentation){

        put("regionId", regionId);

        put("supplierId", organizationId);
        put("supplierName", organizationPresentation);

        put("activityId", activityId);
        put("activityName", activityPresentation);

        put("displayName", String.format("%s (%s)",organizationPresentation, activityPresentation));

    }



}
