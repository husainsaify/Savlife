package com.hackerkernel.blooddonar.parser;

import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.pojo.SimplePojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to parse json response
 */
public class JsonParser {
    public static SimplePojo SimpleParser(String response) throws JSONException {
        JSONObject jo = new JSONObject(response);
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setMessage(jo.getString(Constants.COM_MESSAGE));
        simplePojo.setReturned(jo.getBoolean(Constants.COM_RETURN));
        return simplePojo;
    }
}
