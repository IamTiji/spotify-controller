package com.tiji.spotify_controller.api.spotify;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ImageAdapter extends TypeAdapter<URL> {
    @Override
    public void write(JsonWriter out, URL value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL read(JsonReader in) throws IOException {
        in.beginArray();
        in.beginObject();
        while (!in.nextName().equals("url")) {
            in.skipValue();
        }
        String url = in.nextString();
        while (in.hasNext()) {
            in.nextName();
            in.skipValue();
        }
        in.endObject();

        while (in.hasNext()) {
            in.beginObject();
            while (in.hasNext()) {
                in.nextName();
                in.skipValue();
            }
            in.endObject();
        }

        in.endArray();

        try {
            return new URI(url).toURL();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URL: " + url, e);
        }
    }
}
