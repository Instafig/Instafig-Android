package com.apptao.i.instafig;

import android.graphics.Bitmap;

/**
 * Created by miss_jie on 2016/3/4.
 */
public class CacheClass {

    private Configuration configus;
    private String data_sign;
    private String[] nodes;


    public Configuration getConfigus() {
        return configus;
    }

    public void setConfigus(Configuration configus) {
        this.configus = configus;
    }

    public String getData_sign() {
        return data_sign;
    }

    public void setData_sign(String data_sign) {
        this.data_sign = data_sign;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public String[] getNodes() {
        return nodes;
    }


}
