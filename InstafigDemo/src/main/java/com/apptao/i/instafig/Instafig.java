package com.apptao.i.instafig;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Random;

public class Instafig {
    public static String INSTAFIG_ACTION = "com.appwill.instafig";

    private static String defaultPath = "beijing5.appdao.com:17070";

    private static String[] allNodes = null;
    private static int currentNodeIndex = 0;
    private static Application context;
    private static String appKey;

    static Instafig instafig = null;

    private static Configuration config = null;

    private static HashMap<String, String> params;

    /**
     * init instafig
     *
     * @param application
     * @param curappKey
     * @param nodePaths
     */
    public static void startWithAPPKEY(Application application, String curappKey, String... nodePaths) {
        context = application;
        appKey = curappKey;

        params = setParams(context, appKey);
        String configCache = (String) SPUtils.get(context,
                "configCache", "");
        if (configCache.equals("")) {
            config = new Configuration(new JSONObject());
            params.put("data_sign", "");
            if (nodePaths != null && nodePaths.length != 0)
                allNodes = nodePaths;
            else {
                allNodes = new String[]{defaultPath};
            }
        } else {
            config = new Configuration(configCache);
            String allNodePath = (String) SPUtils.get(context, "nodes", "");
            params.put("data_sign", (String) SPUtils.get(context, "data_sign", ""));
            if (allNodePath.equals(""))
                allNodes = new String[]{defaultPath};
            else
                allNodes = parseStrings(allNodePath);
        }
    }

    public static Instafig getInstance() {
        if (instafig == null) {
            synchronized (Instafig.class) {
                if (instafig == null) {
                    instafig = new Instafig();
                    RequestParams req = new RequestParams(params);
                    loadConFromInternet(context, req, allNodes[currentNodeIndex]);
                }
            }
        }
        return instafig;
    }


    public Instafig() {
        ActivityLifecycleCallbacks.init(context);
    }

    /**
     * @return
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * get data from net
     */
    public void getConfigFromNet() {
        HashMap<String, String> params = setParams(context, appKey);
        params.put("data_sign", "");
        RequestParams requestParams = new RequestParams(params);
        if (allNodes != null && allNodes.length != 0) {
            loadConFromInternet(context, requestParams, allNodes[0]);
            allNodes = parseStrings(randomStringArray(allNodes));//random
        } else {
            loadConFromInternet(context, requestParams, defaultPath);
        }
    }


    /**
     * set params
     *
     * @param context
     * @param appKey
     * @return
     */
    private static HashMap<String, String> setParams(Context context,
                                                     String appKey) {
        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("app_key", appKey);
        params.put("os_type", "andorid");
        params.put("os_version", Build.VERSION.SDK_INT + "");
        try {
            params.put(
                    "app_version",
                    (context.getPackageManager().getPackageInfo(
                            context.getPackageName(), 0)).versionCode
                            + "");
        } catch (PackageManager.NameNotFoundException e) {
            params.put("app_version", "");
        }
        params.put("lang", context.getResources().getConfiguration().locale
                + "");
        params.put("device_id", Build.SERIAL);
        return params;
    }

    /**
     * load configuration from internet
     *
     * @param context
     * @param params
     */
    private static void loadConFromInternet(final Context context,
                                            final RequestParams params, String path) {
        String paramsString = params.getParams();
        path = "http://" + path + "/client/config?";
        String requestString = path + paramsString;
        HttpUtils.getConfs(requestString, new GetCallbackListener() {
            public void onFinish(String result) {
                CacheClass cacheClass = parseJSON(result);
                if (cacheClass != null) {
                    SPUtils.remove(context, "nodes");
                    SPUtils.remove(context, "data_sign");
                    SPUtils.put(context, "nodes", randomStringArray(cacheClass.getNodes()));
                    SPUtils.put(context, "data_sign", cacheClass.getData_sign());
                    if (cacheClass.getConfigus() != null) {
                        SPUtils.remove(context, "configCache");
                        SPUtils.put(context, "configCache", cacheClass.getConfigus().toString());
                        config = cacheClass.getConfigus();
                    }
                    LocalBroadcastManager lbm = LocalBroadcastManager //send broadcast when data update
                            .getInstance(context);
                    Intent intent = new Intent(INSTAFIG_ACTION);
                    lbm.sendBroadcast(intent);
                } else {
                    requestConWhenError(context, params);
                }
            }

            public void onError(String exception) {
                requestConWhenError(context, params);
            }
        });
    }

    /**
     * request data when status is false or node error
     *
     * @param context
     * @param params
     */
    private static void requestConWhenError(Context context, RequestParams params) {
        if (allNodes != null && allNodes.length != 0 && currentNodeIndex < allNodes.length) {
            loadConFromInternet(context, params,
                    allNodes[currentNodeIndex]);
            currentNodeIndex++;
        }
    }

    /**
     * parse json
     *
     * @param result
     * @return
     */
    private static CacheClass parseJSON(String result) {
        CacheClass cacheClass = new CacheClass();
        try {
            JSONTokener jsonParser = new JSONTokener(result);
            JSONObject resObj = (JSONObject) jsonParser.nextValue();
            boolean status = resObj.getBoolean("status");
            if (status) {
                JSONObject dataObj = resObj.getJSONObject("data");
                String sign = dataObj.getString("data_sign");
                try {
                    JSONObject confsObj = dataObj.getJSONObject("configs");  //if web server don't return data_sign and configus
                    cacheClass.setConfigus(new Configuration(confsObj));
                } catch (Exception e) {
                    cacheClass.setConfigus(null);
                }
                JSONArray nodes = dataObj.getJSONArray("nodes");
                String[] nodesList = new String[nodes.length()];
                for (int i = 0; i < nodes.length(); i++) {
                    nodesList[i] = (String) nodes.get(i);
                }
                cacheClass.setData_sign(sign);
                cacheClass.setNodes(nodesList);
            } else {
                return null; //if status is not true
            }
        } catch (JSONException ex) {
            return null;
        }
        return cacheClass;
    }


    /**
     * random all nodePaths(StringArray) and change to String
     *
     * @param params
     * @return
     */
    private static String randomStringArray(String[] params) {
        String[] result = new String[params.length];
        int count = params.length;
        int cbRandCount = 0;
        int cbPosition = 0;
        int k = 0;
        int runCount = 0;
        do {
            runCount++;
            Random rand = new Random();
            int r = count - cbRandCount;
            cbPosition = rand.nextInt(r);
            result[k++] = params[cbPosition];
            cbRandCount++;
            params[cbPosition] = params[r - 1];
        } while (cbRandCount < count);

        StringBuilder builder = new StringBuilder();
        for (String s : result) {
            builder.append(s);
            builder.append(",");
        }
        return builder.toString();
    }


    /**
     * String to StringArray
     *
     * @param params
     * @return
     */
    private static String[] parseStrings(String params) {
        String[] result = params.split(",");
        return result;
    }

}
