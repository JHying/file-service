package tw.hyin.demo.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import tw.hyin.demo.utils.Log;

import org.apache.commons.lang.time.DateUtils;
import org.assertj.core.util.DateUtil;

import java.io.IOException;
import java.util.Date;

/**
 * 透過 @Requestbody 直接反序列為 JavaBean 時，JavaBean 屬性的 @DateTimeFormat 會失效<br>
 * 此時需透過 JsonDeserializer 設定<br>
 * Date 使用方法 -- 於 JavaBean 屬性加上 @JsonDeserialize(using=JsonDateDeseriConfig.class)<br>
 * Collection 使用方法 -- 於 JavaBean 屬性加上 @JsonDeserialize(contentUsing=JsonDateDeseriConfig.class)
 *
 * @author H-yin
 */
public class JsonDateDeseriConfig extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            return DateUtils.parseDate(jsonParser.getText(), new String[]{
                    "yyyyMMdd", "yyyyMMdd HH:mm", "yyyyMMdd HH:mm:ss", "yyyyMMdd HH:mm:ss.SSS",
                    "yyyy/MM/dd", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.SSS",
                    "yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS",
                    "MM/dd/yyyy", "MM/dd/yyyy HH:mm", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy HH:mm:ss.SSS"});
        } catch (Exception e) {
            try {
                //接受預設的 new Date() 格式
                return DateUtil.parse(jsonParser.getText());
            } catch (IOException e1) {
                e.printStackTrace();
                Log.error(e.getMessage());
                return null;
            }
        }
    }

}
