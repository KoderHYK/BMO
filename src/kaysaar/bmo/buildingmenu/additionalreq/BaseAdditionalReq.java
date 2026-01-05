package kaysaar.bmo.buildingmenu.additionalreq;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class BaseAdditionalReq {
    public boolean metCriteria(MarketAPI market, String indId){
        return true;
    }
    public String getReason(MarketAPI market, String indId){
        return "";
    }
}
