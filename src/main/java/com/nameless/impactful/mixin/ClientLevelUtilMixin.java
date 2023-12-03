package com.nameless.impactful.mixin;

import com.nameless.impactful.client.CameraEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.LevelUtil;

import static yesman.epicfight.api.utils.LevelUtil.canTransferShockWave;

@Mixin(LevelUtil.class)
public class ClientLevelUtilMixin {
    @Inject(method = "circleSlamFracture(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;DZZZ)Z", at = @At("RETURN"), remap = false)
    private static void onCircleSlamFracture(LivingEntity caster, Level level, Vec3 center, double radius, boolean noSound, boolean noParticle, boolean hurtEntities, CallbackInfoReturnable<Boolean> cir){
        if(level.isClientSide()) {
            Vec3 closestEdge = new Vec3(Math.round(center.x), Math.floor(center.y), Math.round(center.z));
            Vec3 centerOfBlock = new Vec3(Math.floor(center.x) + 0.5D, Math.floor(center.y), Math.floor(center.z) + 0.5D);

            if (closestEdge.distanceToSqr(center) < centerOfBlock.distanceToSqr(center)) {
                center = closestEdge;
            } else {
                center = centerOfBlock;
            }

            BlockPos blockPos = new BlockPos(center);
            BlockState originBlockState = level.getBlockState(blockPos);

            if (canTransferShockWave(level, blockPos, originBlockState)) {
                int t = (int) Math.max(20, Math.min(15 * radius, 120));
                float s = (float) Math.min(2 * radius, 12);
                CameraEngine.getInstance().shakeCamera(t,s);
            }
        }
    }
}
