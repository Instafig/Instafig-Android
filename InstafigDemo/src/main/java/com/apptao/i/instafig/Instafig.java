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

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Instafig {
    public static String INSTAFIG_ACTION = "com.appwill.instafig";

    public Instafig(JSONObject jsonObject) {
        ActivityLifecycleCallbacks.init(context);
        this.conObj = jsonObject;
    }

    static Instafig instafig = null;

    public static Instafig getInstance() {
        if (instafig == null) {
            synchronized (Instafig.class) {
                if (instafig == null) {
                    HashMap<String, String> params = setParams(context, appKey);
                    String confString = (String) SPUtils.get(context,
                            "resultData", "");
                    if (confString.equals("")) {
                        params.put("data_sign", "");
                        RequestParams req = new RequestParams(params);
                        loadConFromInternet(context, req, defaultPath);
                        instafig = new Instafig(new JSONObject());
                    } else {
                        CacheClass cacheClass = parseJSON(confString);
                        if (cacheClass.getNodes().length != 0) {
                            if (cacheClass.getNodes().length == 0)
                                allPaths = new String[]{defaultPath};
                            else
                                allPaths = cacheClass.getNodes();
                            SPUtils.put(context, "paths", allPaths);
                            String path = allPaths[currentPathIndex];
                            params.put("data_sign", cacheClass.getData_sign());
                            RequestParams req = new RequestParams(params);
                            loadConFromInternet(context, req, path);
                        }
                        instafig = cacheClass.getConfigus();
                        if (SPUtils.contains(context, "paths"))
                            SPUtils.remove(context, "paths");
                        SPUtils.put(context, "paths", randomStrings(allPaths));
                    }
                }
            }
        }
        return instafig;
    }

    private JSONObject conObj;

    public int getInt(String key, int defaultValue) {
        try {
            return conObj.getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(conObj.getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

//    public double getDouble(String key, double defaultValue) {
//        try {
//            return conObj.getDouble(key);
//        } catch (Exception e) {
//            return defaultValue;
//        }
//    }


    public String getString(String key, String defaultValue) {
        try {
            return conObj.getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    private static String defaultPath = "beijing5.appdao.com:17070";

    private static String[] allPaths = null;
    private static int currentPathIndex = 0;
    private static Application context;
    private static String appKey;

    /**
     * @param application
     * @param curappKey
     */
    public static void startWithAPPKEY(Application application, String curappKey) {
        context = application;
        appKey = curappKey;
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
        params.put("ip", IPUtils.getLocalIpAddress(context));
        params.put("lang", context.getResources().getConfiguration().locale
                + "");
//        params.put("device_id", ((TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        params.put("device_id", Build.SERIAL);

        return params;
    }

    /**
     * load data from internet
     *
     * @param context
     * @param params
     */
    private static void loadConFromInternet(final Context context,
                                            final RequestParams params, String path) {

        if (SPUtils.contains(context, "lastLoadDate"))
            SPUtils.remove(context, "lastLoadDate");
        SPUtils.put(context, "lastLoadDate", new Date().getTime());

        String paramsString = params.getParams();
        path = "http://" + path + "/client/config?";
        String endString = path + paramsString;
        HttpUtils.getConfs(endString, new GetCallbackListener() {
            public void onFinish(String result) {
                if (SPUtils.contains(context, "resultData")) {
                    SPUtils.remove(context, "resultData");
                }
                SPUtils.put(context, "resultData", result);
                instafig = parseJSON(result).getConfigus();
                LocalBroadcastManager lbm = LocalBroadcastManager
                        .getInstance(context);
                Intent intent = new Intent(INSTAFIG_ACTION);
                intent.putExtra("configus", parseJSON(result).getConfigus()
                        .toString());
                lbm.sendBroadcast(intent);
            }

            public void onError(String exception) {
                if (allPaths != null && allPaths.length != 0 && currentPathIndex != allPaths.length) {
                    loadConFromInternet(context, params,
                            allPaths[currentPathIndex]);
                    currentPathIndex++;
                }
            }
        });
    }

    /**
     * parse json
     *
     * @param result json
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

                JSONArray nodes = dataObj.getJSONArray("nodes");
                String[] nodesList = new String[nodes.length()];
                for (int i = 0; i < nodes.length(); i++) {
                    nodesList[i] = (String) nodes.get(i);
                }

                JSONObject confsObj = dataObj.getJSONObject("configs");

                cacheClass.setConfigus(new Instafig(confsObj));
                cacheClass.setData_sign(sign);
                cacheClass.setNodes(nodesList);
            } else {
                String code = resObj.getString("code");
                String msg = resObj.getString("msg");
                cacheClass.setErrorCode(code);
                cacheClass.setErrorMsg(msg);
            }
        } catch (JSONException ex) {
            // ex.printStackTrace();
            // Log.e("------error", "出异常了");
        }
        return cacheClass;
    }

    private static String[] randomStrings(String[] params) {
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
        return result;
    }

}
