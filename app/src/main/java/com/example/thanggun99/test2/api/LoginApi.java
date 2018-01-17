package com.example.thanggun99.test2.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thanggun99 on 17/01/2018.
 */

public class LoginApi extends BaseAPI {
    private final String ROOT = "login/";


    public void login(String encryptStr) {
        RequestTask requestTask = new RequestTask(ROOT);
        requestTask.setJsonData("{}");

        Map<String, Object> getParams = new HashMap<>();
        getParams.put("str_encrypt", encryptStr);

        requestTask.execute(getParams);
    }
}
