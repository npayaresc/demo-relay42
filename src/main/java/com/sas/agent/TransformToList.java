package com.sas.agent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by eurnpa on 18.06.2017.
 */

@Component("transformer")
public class TransformToList {

    private static final int FIELDS_IN_ESP_SRC_WINDOW = 88;

    private static final Logger logger = LoggerFactory
            .getLogger(TransformToList.class);

    public Map<String, String>  transformJson(String body){

        String [] jsonObject = body.substring(1, body.length() - 2).split(",");
        Map<String, String> record = new HashMap<>();

        record.put("event_id","");
        for (int i = 0; i < jsonObject.length ; i++) {
            try {
                logger.debug("jsonObject[i]: " + jsonObject[i].toString());
                record.put("attribute" + String.valueOf(i), URLDecoder.decode(jsonObject[i], StandardCharsets.UTF_8.name()));
                logger.debug("attribute" + String.valueOf(i)+ ": " + record.get("attribute" +String.valueOf(i) ));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if(record.size() < FIELDS_IN_ESP_SRC_WINDOW){
            int limit = record.size() - 1;
            for (int i = limit; i < FIELDS_IN_ESP_SRC_WINDOW - 1 ; i++) {
                record.put("attribute" + String.valueOf(i), "");
            }
        }



        return record;
    }
}
