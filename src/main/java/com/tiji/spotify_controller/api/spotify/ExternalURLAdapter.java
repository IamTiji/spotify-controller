package com.tiji.spotify_controller.api.spotify;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ExternalURLAdapter extends TypeAdapter<URL> {
    @Override
    public void write(JsonWriter out, URL value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL read(JsonReader in) throws IOException {
        in.beginObject();
        if (!in.nextName().equals("spotify")) throw new IOException("Malformed external_url field");
        String spotifyUrl = in.nextString();
        in.endObject();

        try {
            return new URI(spotifyUrl).toURL();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
