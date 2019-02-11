package com.example.template2.util;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static Gson getGson(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat(dateFormat.toPattern());

        gsonBuilder.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
            Long value = Math.round(src);
            return new JsonPrimitive(value);
        });

        return  gsonBuilder.create();
    }
}
