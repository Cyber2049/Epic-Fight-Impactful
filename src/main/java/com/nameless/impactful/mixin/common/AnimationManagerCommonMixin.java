package com.nameless.impactful.mixin.common;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.nameless.impactful.api.PropertiesReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.exception.AssetLoadingException;
import yesman.epicfight.api.utils.MutableBoolean;
import yesman.epicfight.main.EpicFightMod;

import java.util.Map;

import static com.nameless.impactful.api.HitStopPropertiesReader.SUBFILE_HS_PROPERTY;
import static com.nameless.impactful.api.PropertiesReader.getSubAnimationFileLocation;
import static yesman.epicfight.api.animation.AnimationManager.getAnimationResourceManager;

@Mixin(AnimationManager.class)
public class AnimationManagerCommonMixin {

    @Shadow @Final
    private Map<AnimationManager.AnimationAccessor<? extends StaticAnimation>, StaticAnimation> animations;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"), cancellable = false, remap = false)
    public void loadHitStop(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn, CallbackInfo ci){
        this.animations.entrySet().stream().reduce(Lists.<AssetAccessor<? extends StaticAnimation>>newArrayList(), (list, entry) -> {
            MutableBoolean init = new MutableBoolean(true);

            if (entry.getValue() == null || entry.getValue().getAccessor() == null) {
                EpicFightMod.logAndStacktraceIfDevSide(Logger::error, "Invalid animation implementation: " + entry.getKey(), AssetLoadingException::new);
                init.set(false);
            }

            entry.getValue().getSubAnimations().forEach((subAnimation) -> {
                if (subAnimation == null || subAnimation.get() == null) {
                    EpicFightMod.logAndStacktraceIfDevSide(Logger::error, "Invalid sub animation implementation: " + entry.getKey(), AssetLoadingException::new);
                    init.set(false);
                }
            });

            if (init.value()) {
                list.add(entry.getValue().getAccessor());
                list.addAll(entry.getValue().getSubAnimations());
            }

            return list;
        }, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        }).forEach((accessor) -> {
            accessor.doOrThrow(StaticAnimation::postInit);
            ResourceLocation vfxLocation = getSubAnimationFileLocation(accessor.get().getLocation(), SUBFILE_HS_PROPERTY);
            getAnimationResourceManager().getResource(vfxLocation).ifPresent((rs) -> PropertiesReader.readAndApply(accessor.get(), rs, SUBFILE_HS_PROPERTY));
        });
    }
}
