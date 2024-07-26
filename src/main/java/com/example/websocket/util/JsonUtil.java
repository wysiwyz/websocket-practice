package com.example.websocket.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/** JSON converter */
@Slf4j
public class JsonUtil {

    /**
     * Convert Java object into JSON string
     *
     * @param object Java object to be converted into JSON string
     * @return json string or null
     */
    public static String parseObjToJson(Object object) {
        String string = null;
        try {
            string = JSONObject.toJSONString(object);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return string;
    }

    /**
     * Convert json string into Java object
     *
     * @param json  JSON string
     * @param c     object in correspondence to the JSON string
     */
    public static <T> T parseJsonToObj(String json, Class<T> c) {
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            return JSON.toJavaObject(jsonObject, c);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
