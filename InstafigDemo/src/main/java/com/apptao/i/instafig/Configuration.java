package com.apptao.i.instafig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by appwillgoogle on 16/3/23.
 */
public class Configuration {

    JSONObject configurationDetail;

    public Configuration(JSONObject jsonObject) {
        this.configurationDetail = jsonObject;
    }

    public Configuration(String configString) {
        try {
            this.configurationDetail = new JSONObject(configString);
        } catch (JSONException e) {
            e.printStackTrace();
            this.configurationDetail = null;
        }
    }

    /**
     * get int value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(String key, int defaultValue) {
        try {
            return configurationDetail.getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * get float value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(configurationDetail.getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * get string value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        try {
            return configurationDetail.getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        return configurationDetail.toString();
    }
}
