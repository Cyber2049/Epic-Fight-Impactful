package com.nameless.impactful.mixin.BTPCompat;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(value = MouseHandler.class)
public abstract class MixinMouseHandler {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"), method = "turnPlayer()V")
	private void epicfight_turnPlayer(LocalPlayer entity, double d2, double d3) {
		LocalPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(entity, LocalPlayerPatch.class);
		RenderEngine renderEngine = ClientEngine.getInstance().renderEngine;
		
		if (playerpatch == null) {
			entity.turn(d2, d3);
		} else {
			if (!playerpatch.isTargetLockedOn()) {
					entity.turn(d2, d3);
			} else {
				renderEngine.rotateCameraByMouseInput((float)(d3), (float)d2);
			}
		}
	}
}