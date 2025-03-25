package com.example.newApp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.newApp.classHelper.OffersByStatus;

public class OfferService {
    public static List<OffersByStatus> parseOffersByStatus(List<Map<String, Object>> offerByStatusData) {
        List<OffersByStatus> offersByStatusList = new ArrayList<>();

        if (offerByStatusData != null) {
            for (Map<String, Object> statusMap : offerByStatusData) {
                OffersByStatus offersByStatus = new OffersByStatus();
                offersByStatus.setStatus((String) statusMap.get("status"));
                offersByStatus.setTotal((int) statusMap.get("total"));
                offersByStatusList.add(offersByStatus);
            }
        }

        return offersByStatusList;
    }
}
