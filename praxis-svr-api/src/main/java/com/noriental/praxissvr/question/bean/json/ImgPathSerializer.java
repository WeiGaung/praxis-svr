package com.noriental.praxissvr.question.bean.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Author : Lance lance7in_gmail_com
 * Date   : 06/01/2014 10:06
 * Since  :
 */
public class ImgPathSerializer extends JsonSerializer<String> {
    private static String prefix;

    @Override
    public void serialize(String value, JsonGenerator jgen,
                          SerializerProvider provider)
            throws IOException {
        if (!value.contains(".")) {
            jgen.writeString(value);
        } else {
            String[] arrstr = value.split("\\.");
            if ("png".equals(arrstr[1])) {
                jgen.writeString(arrstr[0] + "." + arrstr[1]);
            } else {
                jgen.writeString(value);
            }
        }

    }

    public void setPrefix(String prefix) {
        ImgPathSerializer.prefix = prefix;
    }

    public static String getPrefix() {
        return prefix;
    }
}
