package com.example.thanggun99.test2.one;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by No1VietNam on 17/05/2017.
 */

public class JsonUtils {

    public static <T> T getValue(JSONObject jsonObject, String key, T defaultValue) {
        try {
            Object value = getValueOfJsonObjectByKey(jsonObject, key, defaultValue);
            if (value != null) {
                if (defaultValue == null && (value instanceof Integer || value instanceof Double)) {
                    return (T) value.toString();
                } else {
                    return (T) value;
                }
            } else {
                return defaultValue;
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    //Đệ quy tìm giá trị trong jsonObject với key truyền vào
    private static Object getValueOfJsonObjectByKey(String jsonString, String key, Object defaultValue) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return getValueOfJsonObjectByKey(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            return null;
        }
    }

    private static Object getValueOfJsonObjectByKey(JSONObject jsonObject, String key, Object defaultValue) {
        if (jsonObject == null) {
            return null;
        }
        if (jsonObject.has(key)) {
            try {
                if (jsonObject.isNull(key)) {
                    return null;
                } else {
                    if (defaultValue instanceof String) {
                        return jsonObject.getString(key);
                    } else if (defaultValue instanceof Float) {
                        return Float.parseFloat(jsonObject.getString(key));
                    } else if (defaultValue instanceof Integer) {
                        return jsonObject.getInt(key);
                    } else if (defaultValue instanceof Double) {
                        return jsonObject.getDouble(key);
                    } else if (defaultValue instanceof Long) {
                        return jsonObject.getLong(key);
                    } else if (defaultValue instanceof Boolean) {
                        return jsonObject.getBoolean(key);
                    } else {
                        return jsonObject.get(key);
                    }
                }
            } catch (JSONException e) {
                return null;
            }
        } else {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                try {
                    Object value = getValueOfJsonObjectByKey(jsonObject.getJSONObject(iterator.next()), key, defaultValue);
                    if (value != null) {
                        return value;
                    }
                } catch (JSONException ignored) {
                }
            }
            return null;
        }
    }

    public static Map<String, Object> toMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap();

        Iterator<String> keysItr = jsonObject.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = null;
            try {
                value = jsonObject.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = null;
            try {
                value = array.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
