package com.nameless.impactful.mixin.client;

import com.nameless.impactful.client.CameraEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.LevelUtil;

@Mixin(LevelUtil.class)
public class ClientLevelUtilMixin {
    @Inject(method = "circleSlamFracture(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/phys/Vec3;DZZ)Z", at = @At("TAIL"), remap = false)
    private static void onCasterCircleSlamFracture(LivingEntity caster, ClientLevel level, Vec3 center, double radius, boolean noSound, boolean noParticle, CallbackInfoReturnable<Boolean> cir) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null && localPlayer.position().distanceTo(center) <= radius + 3) {
                int t = (int) Math.max(20, Math.min(15 * radius, 120));
                float s = (float) Math.min(2 * radius, 12);
                CameraEngine.getInstance().shakeCamera(t, s, 0);
            }
        }
}
