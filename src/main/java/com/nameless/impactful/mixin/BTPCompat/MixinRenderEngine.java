package com.nameless.impactful.mixin.BTPCompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.events.engine.RenderEngine;

@Mixin(RenderEngine.class)
public class MixinRenderEngine {

    @Redirect(at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/types/EntityState;turningLocked()Z"), method = "correctCamera(Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;F)V", remap = false)
    public boolean freeTuringLock(EntityState instance){
        return false;
    }
}
