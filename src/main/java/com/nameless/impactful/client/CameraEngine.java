package com.nameless.impactful.client;

import com.nameless.impactful.Impactful;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

import java.util.Comparator;
import java.util.PriorityQueue;

import static com.nameless.impactful.api.client.VFXPropertiesReader.VFXAnimationProperties.SCREEN_SHAKE;

@OnlyIn(Dist.CLIENT)
public class CameraEngine {
    public CameraEngine(){
        instance = this;
    }
    private static CameraEngine instance;
    public static CameraEngine getInstance(){
        return instance;
    }
    public PriorityQueue<ShakeEntry> getQueue(){
        return this.queue;
    }
    private final PriorityQueue<ShakeEntry> queue =
            new PriorityQueue<>(Comparator.comparingDouble(e -> -e.strength));
    private final ShakeEntry default_entry = new ShakeEntry(1d,3,0.3d);
    public void tick(ViewportEvent.ComputeCameraAngles event, Player player) {
        if (ClientConfig.DISABLE_SCREEN_SHAKE.get() || Minecraft.getInstance().isPaused() || queue.isEmpty()) return;

        queue.removeIf(entry -> {
            entry.remainingTicks--;
            entry.strength *= 0.99;
            entry.frequency *= 0.99;
            return entry.remainingTicks <= 0;
        });

        if (!queue.isEmpty()) {
            ShakeEntry top = queue.peek();
            double ticksExistedDelta = player.tickCount + event.getPartialTick();
            double k = top.strength / 4F * ClientConfig.SCREEN_SHAKE_AMPLITUDE_RATE.get().floatValue();
            double f = top.frequency;
            event.setPitch((float) (event.getPitch() + k * Math.cos(ticksExistedDelta * f + 2)));
            event.setYaw((float) (event.getYaw() + k * Math.cos(ticksExistedDelta * f + 1)));
            event.setRoll((float) (event.getRoll() + k * Math.cos(ticksExistedDelta * f)));
        }
    }

    public void shakeCamera(ShakeEntry entry){
        ShakeEntry entry1 = entry.copy();
        queue.add(entry1);
    }
    public void shakeCamera(float strength, int time, float frequency){
        this.shakeCamera(new ShakeEntry(strength, time, frequency));
    }
    public void shakeCamera(int time, float strength){
        this.shakeCamera(new ShakeEntry(strength, time, 0.3));
    }
    public void shakeCameraByAnim(int animationId, float elapsedTime){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        RenderItemBase renderitembase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
        ShakeEntry shakeEntry = ((IRenderItemBase)renderitembase).getShakeEntry() != null ? ((IRenderItemBase)renderitembase).getShakeEntry() : default_entry;
        if(AnimationManager.byId(animationId).get() instanceof AttackAnimation attackAnimation){
            shakeEntry = attackAnimation.getPhaseByTime(elapsedTime).getProperty(SCREEN_SHAKE).orElse(shakeEntry);
        }

        this.shakeCamera(shakeEntry);
    }

    @Mod.EventBusSubscriber(modid = Impactful.MOD_ID, value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void cameraSetupEvent(ViewportEvent.ComputeCameraAngles event) {
            Player player = Minecraft.getInstance().player;
            if(player == null) return;

            instance.tick(event, player);
        }
    }

    public static class ShakeEntry {
        double strength;
        int remainingTicks;
        double frequency;
        public ShakeEntry(double strength, int tick, double frequency) {
            this.strength = strength;
            this.remainingTicks = tick;
            this.frequency = frequency;
        }
        public ShakeEntry(double strength, int tick){
            this(strength, tick, 0.3f);
        }

        public ShakeEntry copy(){
            return new ShakeEntry(this.strength, this.remainingTicks, this.frequency);
        }
    }
}
