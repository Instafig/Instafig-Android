package com.apptao.i.instafig;

/**
 * Created by miss_jie on 2016/3/4.
 */
public class CacheClass {
    private Instafig configus;
    private String data_sign;
    private String[] nodes;

    private String errorCode;
    private String errorMsg;

    public Instafig getConfigus() {
        return configus;
    }

    public void setConfigus(Instafig configus) {
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
