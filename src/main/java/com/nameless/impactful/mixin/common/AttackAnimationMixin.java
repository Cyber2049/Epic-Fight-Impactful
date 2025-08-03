package com.nameless.impactful.mixin.common;

import com.nameless.impactful.capabilities.ImpactfulCap;
import com.nameless.impactful.capabilities.ImpactfulCapabilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import static com.nameless.impactful.capabilities.ImpactfulCap.HIT_STOP;

@Mixin(AttackAnimation.class)
public class AttackAnimationMixin {


    @Inject(method = "getPlaySpeed(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/types/DynamicAnimation;)F", at = @At("RETURN"), cancellable = true, remap = false)
    public void getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation, CallbackInfoReturnable<Float> cir) {
        if(entitypatch instanceof PlayerPatch<?> playerPatch){
            Player player = playerPatch.getOriginal();
            ImpactfulCap impactfulCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
            if(impactfulCap != null && player.getEntityData().get(HIT_STOP)){
                float k = 1;
                if(impactfulCap.HIT_STOP_TIME > 0) {
                    k = impactfulCap.HIT_STOP_SPEED;
                    impactfulCap.HIT_STOP_TIME--;
                } else {
                    player.getEntityData().set(HIT_STOP, false);
                }
                cir.setReturnValue(cir.getReturnValueF() * Math.min(k, 1F));
            }
        }
    }

}
