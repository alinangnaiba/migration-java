package com.alyn.migration.parse;

import java.util.ArrayList;
import java.util.List;

public class DataFilter {
    private List<String[]> badData;
    private List<String[]> goodData;
    private List<String[]> data;

    public DataFilter(List<String[]> data) {
        this.data = data;
        badData = new ArrayList<>();
        goodData = new ArrayList<>();
    }

    public void sanitize() {
        for (String[] d : data) {
            if (isGoodData(d)) {
                goodData.add(d);
            } else {
                badData.add(d);
            }
        }
    }

    public List<String[]> getGoodData() {
        return goodData;
    }

    public List<String[]> getBadData() {
        return badData;
    }

    private boolean isGoodData(String[] data) {
        try {
            for (int i = 0; i < data.length; i++) {
                if (isNullOrEmpty(data[i])) {
                    return false;
                }
                if (i == 6) {
                    Double.parseDouble(data[i].substring(1));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isNullOrEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }
}
