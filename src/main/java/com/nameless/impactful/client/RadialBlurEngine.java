package com.nameless.impactful.client;


import com.mojang.blaze3d.platform.Window;
import com.nameless.impactful.Impactful;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

import java.io.IOException;

import static com.nameless.impactful.api.client.VFXPropertiesReader.VFXAnimationProperties.RADIAL_BLUR;

@OnlyIn(Dist.CLIENT)
public class RadialBlurEngine {

    private static RadialBlurEngine instance;
    private static PostChain blurChain;
    private static final ResourceLocation SHADER =
            ResourceLocation.fromNamespaceAndPath(Impactful.MOD_ID, "shaders/post/radial_blur.json");

    private RadialBlur radialBlur;
    private final RadialBlur default_entry = new RadialBlur(2,0.5f, 0);


    public RadialBlurEngine(){
        instance = this;
    }
    public static RadialBlurEngine getInstance(){
        return instance;
    }


    public void applyRadialBlur(int lifTime, float blurRate, int decay_time){
        this.radialBlur = new RadialBlur(lifTime, blurRate, decay_time);
        reset();
    }

    public void applyRadialBlur(RadialBlur radialBlur){
        this.radialBlur = radialBlur.copy();
        reset();
    }

    public void applyRadialBlurByAnim(int animationId, float elapsedTime){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        RenderItemBase renderitembase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
        radialBlur = ((IRenderItemBase)renderitembase).getRadialBlur() != null ? ((IRenderItemBase)renderitembase).getRadialBlur() : default_entry;
        if(AnimationManager.byId(animationId).get() instanceof AttackAnimation attackAnimation){
            radialBlur = attackAnimation.getPhaseByTime(elapsedTime).getProperty(RADIAL_BLUR).orElse(radialBlur);
        }
        this.applyRadialBlur(radialBlur.lifeTime, radialBlur.blurRate, radialBlur.decay_time);
    }

    private void reset(){
        if(radialBlur == null) return;;
        blurChain.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        EffectInstance effect = blurChain.passes.get(0).getEffect();
        Window win = Minecraft.getInstance().getWindow();
        float centerX = win.getWidth()  * 0.5f / win.getScreenWidth();
        float centerY = win.getHeight() * 0.5f / win.getScreenHeight();
        effect.safeGetUniform("center").set(centerX, centerY);
        effect.safeGetUniform("intensity").set((float) (radialBlur.blurRate * ClientConfig.RADIAL_BLUR_INTENSITY_RATE.get()));
    }
    private void tick(float partialTick){
        if(radialBlur == null) return;
        radialBlur.tick();
        EffectInstance effect = blurChain.passes.get(0).getEffect();
        effect.safeGetUniform("intensity").set((float) (radialBlur.blurRate * ClientConfig.RADIAL_BLUR_INTENSITY_RATE.get()));
        blurChain.process(partialTick);
        if(radialBlur.removed){
            radialBlur = null;
        }
    }


    @Mod.EventBusSubscriber(modid = Impactful.MOD_ID, value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void onRenderTick(RenderLevelStageEvent e) throws IOException {
            if(ClientConfig.DISABLE_RADIAL_BLUR.get()) return;
            if (blurChain == null) {
                blurChain = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(), SHADER);
                blurChain.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                Window win = Minecraft.getInstance().getWindow();
                float centerX = win.getWidth()  * 0.5f / win.getScreenWidth();
                float centerY = win.getHeight() * 0.5f / win.getScreenHeight();
                blurChain.passes.get(0).getEffect().safeGetUniform("center").set(centerX, centerY);
            }
            if (blurChain != null && Minecraft.getInstance().player != null && e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                instance.tick(e.getPartialTick());
            }
        }
    }

    public static class RadialBlur {
        private int age;
        private final int lifeTime;
        private final int decay_time;
        private boolean removed;
        private float blurRate;

        public RadialBlur(int lifeTime, float blurRate, int decay_time) {
            this.lifeTime = lifeTime;
            this.blurRate = blurRate;
            this.decay_time = decay_time;
        }

        public void tick(){
            this.age++;
            if(age > decay_time)this.blurRate *= 0.98f;
            if(age > lifeTime){
                this.removed = true;
            }
        }

        public RadialBlur copy(){
            return new RadialBlur(this.lifeTime, this.blurRate, this.decay_time);
        }
    }
}