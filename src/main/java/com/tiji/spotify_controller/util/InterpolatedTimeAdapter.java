package com.tiji.spotify_controller.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class InterpolatedTimeAdapter extends TypeAdapter<InterpolatedTime> {
    @Override
    public void write(JsonWriter out, InterpolatedTime value) throws IOException {
        out.value(value.getInterpolatedTime());
    }

    @Override
    public InterpolatedTime read(JsonReader in) throws IOException {
        return new InterpolatedTime(in.nextInt());
    }
}
