package com.nameless.impactful.mixin.client;

import com.nameless.impactful.api.PropertiesReader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import static com.nameless.impactful.api.PropertiesReader.getSubAnimationFileLocation;
import static com.nameless.impactful.api.client.VFXPropertiesReader.SUBFILE_VFX_PROPERTY;
import static yesman.epicfight.api.animation.AnimationManager.getAnimationResourceManager;

@Mixin(AnimationManager.class)
public class AnimationManagerClientMixin {

    @Inject(method = "readAnimationProperties(Lyesman/epicfight/api/animation/types/StaticAnimation;)V", at = @At("HEAD"), cancellable = false, remap = false)
    private static void loadVFXProperties(StaticAnimation animation, CallbackInfo ci){
        ResourceLocation vfxLocation = getSubAnimationFileLocation(animation.getLocation(), SUBFILE_VFX_PROPERTY);
        getAnimationResourceManager().getResource(vfxLocation).ifPresent((rs) -> PropertiesReader.readAndApply(animation, rs, SUBFILE_VFX_PROPERTY));
    }
}
