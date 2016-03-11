package com.apptao.i.instafig;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miss_jie on 2016/3/3.
 */
public class HttpUtils {

    public static void getConfs(final String name,
                                final GetCallbackListener callbackListener) {
        new AsyncTask<String, Integer, String>() {
            protected String doInBackground(String... params) {
                URL url = null;
                HttpURLConnection conn = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;
                try {
                    url = new URL(name);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    if (conn.getResponseCode() == 200) {
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        int len = -1;
                        byte[] buf = new byte[128];

                        while ((len = is.read(buf)) != -1) {
                            baos.write(buf, 0, len);
                        }
                        baos.flush();
                        return baos.toString();
                    } else {
                        return null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;

                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (baos != null)
                            baos.close();
                    } catch (IOException e) {
                    }
                    conn.disconnect();
                }
            }

            protected void onPostExecute(String s) {
                if (s != null && callbackListener != null) {
                    callbackListener.onFinish(s);
                } else {
                    callbackListener.onError("");
                }
            }
        }.execute(name);
    }
}
