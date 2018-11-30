package com.noriental.praxissvr.question.bean.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Author : Lance lance7in_gmail_com
 * Date   : 27/03/2014 14:36
 * Since  :
 */
public class VideoPathSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeString(value.substring(0, value.lastIndexOf(".")) + ".flv");
    }


}
