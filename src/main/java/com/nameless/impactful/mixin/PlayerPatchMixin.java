package com.nameless.impactful.mixin;

import com.mojang.datafixers.util.Pair;
import com.nameless.impactful.config.CommonConfig;
import com.nameless.impactful.gameassets.HitStopSkill;
import com.nameless.impactful.skill.Slots;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import static com.nameless.impactful.skill.HitStop.HIT_STOP;


@Mixin(PlayerPatch.class)
public class PlayerPatchMixin<T extends Player> {

    @Inject(method = "getAttackSpeed(Lnet/minecraft/world/InteractionHand;)F", at = @At("RETURN"), cancellable = true, remap = false)
    public void onGetAttackSpeed(InteractionHand hand, CallbackInfoReturnable<Float> cir){
        SkillContainer skill = ((PlayerPatch<?>)(Object)this).getSkill(Slots.HIT_STOP);
        WeaponCategory category = ((PlayerPatch<?>)(Object)this).getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory();
        if(skill.getSkill() != null && skill.getDataManager().getDataValue(HIT_STOP)){
            DynamicAnimation animation = ((PlayerPatch<?>)(Object)this).getAnimator().getPlayerFor(null).getAnimation();
            float k;
            if(animation instanceof AttackAnimation attackAnimation && CommonConfig.hit_stop_by_animation.containsKey(attackAnimation) && CommonConfig.camera_shake_by_animation.containsKey(attackAnimation)){
                k = CommonConfig.hit_stop_by_animation.getOrDefault(attackAnimation, Pair.of(2, 0.5F)).getSecond();
            } else {
                k = CommonConfig.hit_stop_by_weapon_categories.getOrDefault(category, Pair.of(2, 0.5F)).getSecond();
            }
            cir.setReturnValue(cir.getReturnValueF() * Math.min(k, 1F));
        }
    }

    @Inject(method = "onJoinWorld(Lnet/minecraft/world/entity/player/Player;Lnet/minecraftforge/event/entity/EntityJoinWorldEvent;)V", at = @At("TAIL"), remap = false)
    public void onPlayerJoinWorld(T entityIn, EntityJoinWorldEvent event, CallbackInfo ci){
        ((PlayerPatch<?>)(Object)this).getSkillCapability().skillContainers[Slots.HIT_STOP.universalOrdinal()].setSkill(HitStopSkill.HIT_STOP);
    }

}
