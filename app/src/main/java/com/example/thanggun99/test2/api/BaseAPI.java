package com.example.thanggun99.test2.api;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.thanggun99.test2.one.JsonUtils;
import com.example.thanggun99.test2.utils.FileUtils;
import com.example.thanggun99.test2.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by No1VietNam on 17/04/2017.
 */

public abstract class BaseAPI {
    private final String SCHEME = "http",
            HOST = "cpvm.vnedutech.vn",
            PATH_ROOT = "services";

    private OnResponseJsonCallback onResponseJsonCallback;
    private OnResonseTextCallback onResonseTextCallback;

    @NonNull
    private static String convertStremToString(InputStream inputStream) throws IOException {
        return FileUtils.readFile(inputStream);
    }

    public void setOnResponseJsonCallback(OnResponseJsonCallback onResponseJsonCallback) {
        this.onResponseJsonCallback = onResponseJsonCallback;
    }

    public void setOnResonseTextCallback(OnResonseTextCallback onResonseTextCallback) {
        this.onResonseTextCallback = onResonseTextCallback;
    }

    private String request(String api, Map<String, Object> getParams, String jsonData) {
        String response = "";
        String url = "";
        HttpURLConnection connection = null;
        try {
            Uri.Builder builder = new Uri.Builder()
                    .scheme(SCHEME)
                    .authority(HOST)
                    .appendPath(PATH_ROOT)
                    .appendEncodedPath(api);

            if (getParams != null) {
                com.example.thanggun99.test2.utils.Utils.builderParams(builder, getParams);
            }

            url = builder.build().toString();
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Content-Type",
                    "application/json;charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "close");
            connection.setDoInput(true);
            connection.setUseCaches(false);
//            connection.setConnectTimeout(8000);
            connection.setUseCaches(true);

            if (!TextUtils.isEmpty(jsonData)) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
//                connection.setReadTimeout(8000);

                BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                outputStream.write(jsonData);
                outputStream.flush();
                outputStream.close();

            } else {
                connection.setRequestMethod("GET");
            }

           /* if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            }*/
            response = convertStremToString(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        Utils.showLog("API_" + api, url);
        if (!TextUtils.isEmpty(jsonData)) {
            Utils.showLog("API_POST_Data", api + ": " + jsonData);
        }
        Utils.showLog("API_Response", api + ": " + response);
        return response;
    }

    //callback on response
    public interface OnResponseJsonCallback {
        void onResponse(JSONObject jsonObject, boolean success);
    }


    public interface OnResonseTextCallback {
        void onResponse(String response);
    }

    @SuppressLint("StaticFieldLeak")
    public class RequestTask extends AsyncTask<Map<String, Object>, Void, String> {
        private final String api;
        private String jsonData;
        private Map<String, Object> getParams;

        public RequestTask(String api) {
            this.api = api;
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            if (maps != null && maps.length > 0) {
                return request(api, maps[0], jsonData);
            } else {
                return request(api, getParams, jsonData);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (onResponseJsonCallback != null) {
                boolean success = false;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    String status = JsonUtils.getValue(jsonObject, "status", "fail");
                    if ("success".equals(status)) {
                        success = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onResponseJsonCallback.onResponse(jsonObject, success);
            }
            if (onResonseTextCallback != null) {
                onResonseTextCallback.onResponse(s);
            }
            super.onPostExecute(s);
        }

        public void setJsonData(String jsonData) {
            this.jsonData = jsonData;
        }

        public void setGetParams(HashMap<String, Object> getParams) {
            this.getParams = getParams;
        }
    }

}
