package com.apptao.i.instafig;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by miss_jie on 2016/3/3.
 */
public class RequestParams {

    private StringBuilder params;


    public RequestParams(HashMap<String, String> params) {
        this.params = new StringBuilder();

        Iterator iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String mapkey = (String) iterator.next();
            String key = (String) mapkey;

            String value = params.get(key);
            this.params.append(key);
            this.params.append("=");
            this.params.append(value);

            this.params.append("&");
        }
    }

    public String getParams() {
        String targetString = params.toString();
        return targetString.substring(0, targetString.length() - 1);
    }
}
