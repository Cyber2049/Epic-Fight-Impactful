package com.nameless.impactful.mixin.BTPCompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

@Mixin(LocalPlayerPatch.class)
public class MixinLocalPlayerPatch {
    @Redirect(at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/types/EntityState;turningLocked()Z"), method = "clientTick(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)V", remap = false)
    public boolean freeTuringLock(EntityState instance){
        return false;
    }
}
