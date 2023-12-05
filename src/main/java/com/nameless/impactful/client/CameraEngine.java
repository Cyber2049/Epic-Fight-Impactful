package com.nameless.impactful.client;

import com.nameless.impactful.Impactful;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class CameraEngine {
    private static CameraEngine instance;
    public static CameraEngine getInstance(){
        return instance;
    }

    private int cameraShakeTime = 0;
    private float cameraShakeStrength = 0;
    private float frequency = 0;

    public CameraEngine(){
        instance = this;
    }

    public void shakeCamera(int time, float strength, float frequency){
        if(strength > this.cameraShakeStrength) {
            this.cameraShakeStrength = strength;
            this.cameraShakeTime = time;
            this.frequency = frequency;
        }
    }
    public void shakeCamera(int time, float strength){
        this.shakeCamera(time, strength, 3);
    }

    public void reset(){
        this.cameraShakeStrength = 0;
        this.frequency = 0;
    }

    @Mod.EventBusSubscriber(modid = Impactful.MOD_ID, value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void cameraSetupEvent(EntityViewRenderEvent.CameraSetup event) {
            Player player = Minecraft.getInstance().player;
            if(player == null) return;
            if(instance.cameraShakeTime > 0) {
                instance.cameraShakeTime--;
                float delta = Minecraft.getInstance().getFrameTime();
                float ticksExistedDelta = player.tickCount + delta;
                float k = instance.cameraShakeStrength / 4F * ClientConfig.SCREEN_SHAKE_AMPLITUDE_MULTIPLY.get().floatValue();
                float f = instance.frequency;
                if(!ClientConfig.DISABLE_SCREEN_SHAKE.get()  && !Minecraft.getInstance().isPaused()) {
                    event.setPitch((float) (event.getPitch() + k * Math.cos(ticksExistedDelta * f + 2)));
                    event.setYaw((float) (event.getYaw() + k * Math.cos(ticksExistedDelta * f + 1)));
                    event.setRoll((float) (event.getRoll() + k * Math.cos(ticksExistedDelta * f)));
                }
            } else if(instance.cameraShakeStrength != 0){
                instance.reset();
            }
        }
    }
}
