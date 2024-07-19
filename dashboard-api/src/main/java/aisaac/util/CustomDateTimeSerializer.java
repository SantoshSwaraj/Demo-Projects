package aisaac.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/*
 * CustomDateSerializer class
 */
public class CustomDateTimeSerializer extends JsonSerializer<Date> {
	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {

		SimpleDateFormat formatter = new SimpleDateFormat(AppConstants.DATETIME_TZ_FORMAT_STR);
		String formattedDate = formatter.format(value);

		gen.writeString(formattedDate);
	}
}
