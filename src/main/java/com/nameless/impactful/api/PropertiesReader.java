package com.nameless.impactful.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.main.EpicFightMod;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class PropertiesReader {
    public static void readAndApply(StaticAnimation animation, Resource iresource, SubFileType<?> subFileType) {
        InputStream inputstream = null;

        try {
            inputstream = iresource.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert inputstream != null : "Input stream is null";

        try {
            subFileType.apply(inputstream, animation);
        } catch (JsonParseException e) {
            EpicFightMod.LOGGER.warn("Can't read sub file " + subFileType.directory + " for " + animation);
            e.printStackTrace();
        }
    }


    public static abstract class SubFileType<T> {
        private final String directory;
        private final AnimationSubFileDeserializer<T> deserializer;

        public SubFileType(String directory,  AnimationSubFileDeserializer<T> deserializer) {
            this.directory = directory;
            this.deserializer = deserializer;
        }

        // Deserialize from input stream
        public void apply(InputStream inputstream, StaticAnimation animation) {
            Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);
            T deserialized = this.deserializer.deserialize(animation, Streams.parse(jsonReader));
            this.applySubFileInfo(deserialized, animation);
        }

        protected abstract void applySubFileInfo(T deserialized, StaticAnimation animation);

        public String getDirectory() {
            return this.directory;
        }
    }

    public static ResourceLocation getSubAnimationFileLocation(ResourceLocation location, PropertiesReader.SubFileType<?> subFileType) {
        int splitIdx = location.getPath().lastIndexOf(47);
        if (splitIdx < 0) {
            splitIdx = 0;
        }

        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), String.format("%s/" + subFileType.getDirectory() + "%s", location.getPath().substring(0, splitIdx), location.getPath().substring(splitIdx)));
    }

    public interface AnimationSubFileDeserializer<T> {
        public T deserialize(StaticAnimation animation, JsonElement json) throws JsonParseException;
    }
}
