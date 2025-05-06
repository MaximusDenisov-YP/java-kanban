package ru.kanban.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter writer, Duration value) throws IOException {
        if (value != null) {
            writer.value(value.toString());
        } else writer.nullValue();
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        return Duration.parse(in.nextString());
    }
}