package org.demo.utils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonSerializer
{
    private JsonSerializer()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * this is like ISO_INSTANT
     */
    public static final String JSON_ZONED_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSVV";

    private static final DateTimeParser DATE_TIME_PARSER = new DateTimeParser();

    public static <T> T fromJson(String data, Class<T> clazz)
    {
        if (!StringUtils.hasLength(data))
        {
            return null;
        }

        try
        {
            Gson gson = createGson(false);
            return gson.fromJson(data, clazz);
        }
        catch(JsonSyntaxException e)
        {
            log.error("Unable to parse string. " + data, e);
            return null;
        }
    }

    public static String toJson(Object data, boolean isPretty)
    {
        if (data == null)
        {
            return "";
        }

        try
        {
            Gson gson = createGson(isPretty);
            return gson.toJson(data);
        }
        catch(JsonSyntaxException e)
        {
            log.error("Unable to create string.", e);
            return null;
        }
    }

    private static Gson createGson(boolean isPretty)
    {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, DATE_TIME_PARSER);
        if (isPretty)
        {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

    private static class DateTimeParser extends TypeAdapter<ZonedDateTime>
    {
        @Override
        public void write(JsonWriter out, ZonedDateTime value) throws IOException
        {
            String string = "";
            if (value != null)
            {
                string = value.format(DateTimeFormatter.ofPattern(JsonSerializer.JSON_ZONED_DATE_TIME));
            }
            out.value(string);
        }

        @Override
        public ZonedDateTime read(JsonReader in) throws IOException
        {
            if (in.peek() == JsonToken.NULL)
            {
                in.nextNull();
                return null;
            }

            String string = in.nextString();
            if (StringUtils.hasLength(string))
            {
                return ZonedDateTime.parse(string, DateTimeFormatter.ofPattern(JsonSerializer.JSON_ZONED_DATE_TIME));
            }
            return null;
        }
    }

}
