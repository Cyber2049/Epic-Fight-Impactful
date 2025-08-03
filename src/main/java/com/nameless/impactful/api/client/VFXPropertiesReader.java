package com.nameless.impactful.api.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nameless.impactful.api.PropertiesReader;
import com.nameless.impactful.capabilities.ImpactfulCap;
import com.nameless.impactful.capabilities.ImpactfulCapabilities;
import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.client.RadialBlurEngine;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class VFXPropertiesReader {
    public static final PropertiesReader.SubFileType<VFXProperty> SUBFILE_VFX_PROPERTY = new VFXPropertyType();
    @OnlyIn(Dist.CLIENT)
    static class VFXProperty {
        final Map<Integer, CameraEngine.ShakeEntry> shake_entry_map = Maps.newHashMap();
         final Map<Integer, RadialBlurEngine.RadialBlur> radial_blur_map = Maps.newHashMap();
         final List<CameraEngine.ShakeEntry> shakeEventList = Lists.newArrayList();
        final List<RadialBlurEngine.RadialBlur> blurEventList = Lists.newArrayList();
    }

    @OnlyIn(Dist.CLIENT)
    private static class VFXPropertyType extends PropertiesReader.SubFileType<VFXProperty> {


        private VFXPropertyType() {
            super("visual_effect", new VFXPropertyDeserializer());
        }

        @Override
        public void applySubFileInfo(VFXProperty deserialized, StaticAnimation animation) {
            if(animation instanceof AttackAnimation attackAnimation){
                if(!deserialized.shake_entry_map.isEmpty()){
                    deserialized.shake_entry_map.forEach((phase, entry) -> attackAnimation.addProperty(VFXAnimationProperties.SCREEN_SHAKE, entry, phase-1));
                }
                if(!deserialized.radial_blur_map.isEmpty()){
                    deserialized.radial_blur_map.forEach((phase, entry) -> attackAnimation.addProperty(VFXAnimationProperties.RADIAL_BLUR, entry, phase-1));
                }
            }
            if(!deserialized.shakeEventList.isEmpty()){
                deserialized.shakeEventList.forEach((entry ->
                        animation.addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.SimpleEvent.create((entityPatch, animation1, params) -> {
                            if(entityPatch.getOriginal() instanceof Player player){
                                ImpactfulCap impactfulCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
                                if(impactfulCap != null){
                                    impactfulCap.ShakeCam(entry);
                                }
                            }
                                }, AnimationEvent.Side.CLIENT))));
            }
            if(!deserialized.blurEventList.isEmpty()){
                deserialized.blurEventList.forEach((entry ->
                        animation.addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.SimpleEvent.create((entityPatch, animation1, params) -> {
                            if(entityPatch.getOriginal() instanceof Player player){
                                ImpactfulCap impactfulCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
                                if(impactfulCap != null){
                                    impactfulCap.applyBlur(entry);
                                }
                            }
                        }, AnimationEvent.Side.CLIENT))));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class VFXPropertyDeserializer implements PropertiesReader.AnimationSubFileDeserializer<VFXProperty> {

        @Override
        public VFXProperty deserialize(StaticAnimation staticAnimation, JsonElement jsonElement) throws JsonParseException {
            VFXProperty property = new VFXProperty();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("hit_shake")) {
                JsonArray shakeEntries = jsonObject.get("hit_shake").getAsJsonArray();

                if (!shakeEntries.isEmpty()) {

                    shakeEntries.forEach(element -> {
                        JsonObject entry = element.getAsJsonObject();
                        int phase = GsonHelper.getAsInt(entry, "phase");
                        double strength = GsonHelper.getAsDouble(entry, "strength");
                        int duration = GsonHelper.getAsInt(entry, "duration");
                        property.shake_entry_map.put(phase, new CameraEngine.ShakeEntry(strength, duration));
                    });
                }
            }

            if (jsonObject.has("hit_blur")) {
                JsonArray shakeEntries = jsonObject.get("hit_blur").getAsJsonArray();

                if (!shakeEntries.isEmpty()) {

                    shakeEntries.forEach(element -> {
                        JsonObject entry = element.getAsJsonObject();
                        int phase = GsonHelper.getAsInt(entry, "phase");
                        float strength = GsonHelper.getAsFloat(entry, "strength");
                        int duration = GsonHelper.getAsInt(entry, "duration");
                        property.radial_blur_map.put(phase, new RadialBlurEngine.RadialBlur(duration, strength));
                    });
                }
            }

            if (jsonObject.has("event_shake")) {
                JsonObject shakeEntry = jsonObject.getAsJsonObject("event_shake");
                if(shakeEntry != null){
                    float strength = GsonHelper.getAsFloat(shakeEntry, "strength");
                    int duration = GsonHelper.getAsInt(shakeEntry, "duration");
                    property.shakeEventList.add(new CameraEngine.ShakeEntry(strength, duration));
                }
            }

            if (jsonObject.has("event_blur")) {
                JsonObject shakeEntry = jsonObject.getAsJsonObject("event_blur");
                if(shakeEntry != null){
                    float strength = GsonHelper.getAsFloat(shakeEntry, "strength");
                    int duration = GsonHelper.getAsInt(shakeEntry, "duration");
                    property.blurEventList.add(new RadialBlurEngine.RadialBlur(duration, strength));
                }
            }

            return property;
        }
    }

    public static class VFXAnimationProperties {
        @OnlyIn(Dist.CLIENT)
        public static final AnimationProperty.AttackPhaseProperty<CameraEngine.ShakeEntry> SCREEN_SHAKE = new AnimationProperty.AttackPhaseProperty<>();
        @OnlyIn(Dist.CLIENT)
        public static final AnimationProperty.AttackPhaseProperty<RadialBlurEngine.RadialBlur> RADIAL_BLUR = new AnimationProperty.AttackPhaseProperty<>();
    }
}
