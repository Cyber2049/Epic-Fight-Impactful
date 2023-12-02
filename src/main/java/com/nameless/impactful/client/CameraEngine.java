package com.nameless.impactful.client;

import com.nameless.impactful.Impactful;
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

    public CameraEngine(){
        instance = this;
    }

    public void shakeCamera(int time, float strength){
        this.cameraShakeTime = time;
        this.cameraShakeStrength = strength;
    }
    @Mod.EventBusSubscriber(modid = Impactful.MOD_ID, value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void cameraSetupEvent(EntityViewRenderEvent.CameraSetup event) {
            Player player = Minecraft.getInstance().player;
            if(player != null && instance.cameraShakeTime > 0 && !Minecraft.getInstance().isPaused()){
                float delta = Minecraft.getInstance().getFrameTime();
                float ticksExistedDelta = player.tickCount + delta;
                float k = instance.cameraShakeStrength / 100F;
                event.setPitch((float) (event.getPitch() + k * Math.cos(ticksExistedDelta * 3) * 25));
                event.setYaw((float) (event.getYaw() + k * Math.cos(ticksExistedDelta * 3) * 25));
                event.setRoll((float) (event.getRoll() + k * Math.cos(ticksExistedDelta * 3) * 25));
                instance.cameraShakeTime--;
            }
        }
    }



}
