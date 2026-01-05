package kaysaar.bmo.buildingmenu.additionalreq;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.LinkedHashMap;

public class AdditionalReqManager {
    //Note : this is used by aotd this is why there is only one instance of baseAdditionalReq use it ONLY for vanilla industries
    public static AdditionalReqManager instance;
    public LinkedHashMap<String,BaseAdditionalReq>reqLinkedHashMap = new LinkedHashMap<>();
    public static AdditionalReqManager getInstance() {
        if (instance == null) {
            instance = new AdditionalReqManager();

        }
        return instance;
    }
    public void  addReq(String id,BaseAdditionalReq req){
        reqLinkedHashMap.put(id,req);
    }

    public LinkedHashMap<String, BaseAdditionalReq> getReqLinkedHashMap() {
        return reqLinkedHashMap;
    }
    public BaseAdditionalReq getReq(String id) {
        return reqLinkedHashMap.get(id);
    }
    public boolean doesMetReq(String id, MarketAPI market) {
        if(!reqLinkedHashMap.containsKey(id)){
            return true;
        }
        BaseAdditionalReq req = reqLinkedHashMap.get(id);
        return req.metCriteria(market,id);
    }
}
