package it.trustflow.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Test {

    public static void main(String[] args) throws IOException, JSONException {
        String jsonStr = "[{\"a\":null,\"b\":1},null,[2,null,3],[null,[null],{\"a\":null,\"b\":[null]}],{\"a\":4,\"b\":{\"a\":null,\"b\":null},\"c\":{\"a\":null}}]";


        ObjectMapper objectMapper = new ObjectMapper();
//        Object taskData = objectMapper.readValue(json, JSONObject.class);

        System.out.println(jsonStr);
        Object result = cleanTaskData(jsonStr);
        String output = objectMapper.writeValueAsString(result);
        System.out.println(output);
    }

    private static Object cleanTaskData(String taskData) throws JSONException {
        JSONArray rootArray = new JSONArray(taskData);
        traverseJson(rootArray);
        return rootArray.toString();
    }


    public static void traverseJson(JSONArray jsonObject) throws JSONException {
        Queue<Object> queue = new LinkedList<>();
        queue.add(jsonObject);

        while (!queue.isEmpty()) {
            Object current = queue.poll();

            if (current instanceof JSONArray array) {
                // Add children first
                for (int i = 0; i < array.length(); i++) {
                    Object item = array.get(i);
                    if (item instanceof JSONObject || item instanceof JSONArray) {
                        queue.add(item);
                    }
                }

                // Clean nulls, empty objects, empty arrays
                for (int i = array.length() - 1; i >= 0; i--) {
                    Object item = array.get(i);
                    if (item == JSONObject.NULL) {
                        array.remove(i);
                    } else if (item instanceof JSONObject obj && obj.length() == 0) {
                        array.remove(i);
                    } else if (item instanceof JSONArray arr && arr.length() == 0) {
                        array.remove(i);
                    }
                }

            } else if (current instanceof JSONObject obj) {
                List<String> keysToRemove = new ArrayList<>();

                Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = obj.get(key);

                    if (value == JSONObject.NULL) {
                        keysToRemove.add(key);
                    } else if (value instanceof JSONObject nestedObj) {
                        if (nestedObj.length() == 0) {
                            keysToRemove.add(key);
                        } else {
                            queue.add(nestedObj);
                        }
                    } else if (value instanceof JSONArray nestedArr) {
                        if (nestedArr.length() == 0) {
                            keysToRemove.add(key);
                        } else {
                            queue.add(nestedArr);
                        }
                    }
                }

                keysToRemove.forEach(obj::remove);
            }
        }
    }
}
