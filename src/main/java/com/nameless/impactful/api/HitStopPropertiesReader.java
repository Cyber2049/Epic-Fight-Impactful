package com.nameless.impactful.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nameless.impactful.capabilities.ImpactfulCap;
import net.minecraft.util.GsonHelper;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Map;

public class HitStopPropertiesReader {
    public static final PropertiesReader.SubFileType<HitStopProperty> SUBFILE_HS_PROPERTY = new HitStopPropertyType();
    static class HitStopProperty {
        final Map<Integer, ImpactfulCap.HitStop> hit_stop_map = Maps.newHashMap();
    }
    private static class HitStopPropertyType extends PropertiesReader.SubFileType<HitStopProperty> {

        private HitStopPropertyType() {
            super("hit_stop", new HitStopPropertyDeserializer());
        }

        @Override
        public void applySubFileInfo(HitStopProperty deserialized, StaticAnimation animation) {
            if (animation instanceof AttackAnimation attackAnimation) {
                if (!deserialized.hit_stop_map.isEmpty()) {
                    deserialized.hit_stop_map.forEach((phase, entry) -> attackAnimation.addProperty(HIT_STOP, entry, phase - 1));
                }
            }
        }
    }
    private static class HitStopPropertyDeserializer implements PropertiesReader.AnimationSubFileDeserializer<HitStopProperty> {

        @Override
        public HitStopProperty deserialize(StaticAnimation staticAnimation, JsonElement jsonElement) throws JsonParseException {
            HitStopProperty property = new HitStopProperty();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("hit_stop")) {
                JsonArray shakeEntries = jsonObject.get("hit_stop").getAsJsonArray();

                if (!shakeEntries.isEmpty()) {

                    shakeEntries.forEach(element -> {
                        JsonObject entry = element.getAsJsonObject();
                        int phase = GsonHelper.getAsInt(entry, "phase");
                        float speed = GsonHelper.getAsFloat(entry, "speed");
                        int duration = GsonHelper.getAsInt(entry, "duration");
                        property.hit_stop_map.put(phase, new ImpactfulCap.HitStop(duration, speed));
                    });
                }
            }
            return property;
        }
    }

    public static final AnimationProperty.AttackPhaseProperty<ImpactfulCap.HitStop> HIT_STOP = new AnimationProperty.AttackPhaseProperty<>();
}
