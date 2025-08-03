package com.nameless.impactful.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.client.IRenderItemBase;
import com.nameless.impactful.client.RadialBlurEngine;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

@Mixin(RenderItemBase.class)
public class RenderItemBaseMixin implements IRenderItemBase {
    private CameraEngine.ShakeEntry shakeEntry;
    private RadialBlurEngine.RadialBlur blurEntry;

    @Inject(method = "<init>(Lcom/google/gson/JsonElement;)V", at = @At("TAIL"), cancellable = false, remap = false)
    private void onRenderItemBase(JsonElement jsonElement, CallbackInfo ci){
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        this.shakeEntry = jsonObj.has("shake_entry") ? deserializeShakeEntry(jsonObj) : null;
        this.blurEntry = jsonObj.has("blur_entry") ? deserializeRadialBlur(jsonObj) : null;
    }


    private CameraEngine.ShakeEntry deserializeShakeEntry(JsonObject jsonObj){
        JsonObject object = jsonObj.getAsJsonObject("shake_entry");
        double strength = GsonHelper.getAsDouble(object, "strength");
        int duration = GsonHelper.getAsInt(object, "duration");
        return new CameraEngine.ShakeEntry(strength, duration);
    }

    private RadialBlurEngine.RadialBlur deserializeRadialBlur(JsonObject jsonObj){
        JsonObject object = jsonObj.getAsJsonObject("blur_entry");
        float strength = GsonHelper.getAsFloat(object, "strength");
        int duration = GsonHelper.getAsInt(object, "duration");
        return new RadialBlurEngine.RadialBlur(duration, strength);
    }

    @Override
    public CameraEngine.ShakeEntry getShakeEntry() {
        return this.shakeEntry;
    }

    @Override
    public RadialBlurEngine.RadialBlur getRadialBlur() {
        return this.blurEntry;
    }
}
