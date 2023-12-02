package com.nameless.impactful.mixin;

import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.network.CameraShake;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
public class LevelUtilMixin {
    @Inject(method = "circleSlamFracture(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;DZZZ)Z", at = @At("RETURN"), remap = false)
    private static void onCircleSlamFracture(LivingEntity caster, Level level, Vec3 center, double radius, boolean noSound, boolean noParticle, boolean hurtEntities, CallbackInfoReturnable<Boolean> cir){
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
            double t = Math.max(20, Math.min(15 * radius, 120));
            double s = Math.min(2 * radius, 12);
            if(caster instanceof Player player) {
                //CameraEngine.getInstance().shakeCamera((int) t, (float) s);
                for (ServerPlayer serverPlayer : player.level.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(radius, 5, radius))){
                    NetWorkManger.sendToPlayer(new CameraShake((int) t, (float) s), serverPlayer);
                }
            }
        }
    }
}
