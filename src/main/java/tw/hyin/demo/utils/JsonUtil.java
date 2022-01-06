package tw.hyin.demo.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    /**
     * JSON 轉 Class
     *
     * @param jsonStr     json 字串
     * @param objectClass 寫入之目標物件
     * @return 目標物件
     */
    public static <T> T jsonToBean(String jsonStr, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, objectClass);
    }

    /**
     * JSON 轉 Class
     *
     * @param jsonStr     json 字串
     * @param objectClass 寫入之目標物件
     * @return 目標物件
     */
    public static Object jsonToObject(String jsonStr, Class<?> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, objectClass);
    }

    /**
     * JSON 轉 map
     *
     * @param jsonStr json 字串
     * @return 目標 map
     */
    public static Map<String, Object> jsonToMap(String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Obj 轉 JSON
     *
     * @param obj 物件
     * @return JSON 字串
     */
    public static String objToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    /**
     * map 轉 JSON
     *
     * @param map map
     * @return JSON 字串
     */
    public static String MapToJson(Map<String, Object> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }

    /**
     * map 轉 object
     *
     * @param <T>    object
     * @param map    map
     * @param object object
     * @return object
     */
    @SuppressWarnings("unchecked")
    public static <T> T MapToObject(Map<String, Object> map, T object) throws IOException {
        String json = MapToJson(map);
        object = (T) jsonToObject(json, object.getClass());
        return object;
    }

    /**
     * object 轉 map
     *
     * @param <T> object
     * @return map
     */
    public static <T> Map<?, ?> objToMap(T obj) {
        ObjectMapper oMapper = new ObjectMapper();
        //No serializer found for class
        oMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // object -> Map
        return oMapper.convertValue(obj, Map.class);
    }
}
