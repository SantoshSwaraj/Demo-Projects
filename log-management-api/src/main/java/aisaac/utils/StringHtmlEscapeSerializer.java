package aisaac.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/*
 * CustomDateSerializer class
 */
public class StringHtmlEscapeSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, 
            JsonGenerator gen,
            SerializerProvider provider
            ) throws IOException, JsonProcessingException {
        gen.writeString(StringEscapeUtils.escapeHtml4(value));
    }
}
