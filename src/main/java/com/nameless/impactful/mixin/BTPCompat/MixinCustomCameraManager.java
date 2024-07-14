package com.nameless.impactful.mixin.BTPCompat;


import io.socol.betterthirdperson.api.CustomCamera;
import io.socol.betterthirdperson.api.CustomCameraManager;
import io.socol.betterthirdperson.api.adapter.IPlayerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(CustomCameraManager.class)
public class MixinCustomCameraManager {
    @Shadow private CustomCamera customCamera;

    @Inject(method = "mustHaveCustomCamera(Lio/socol/betterthirdperson/api/adapter/IPlayerAdapter;)Z", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void lockonCancelCustomCamera(IPlayerAdapter player, CallbackInfoReturnable<Boolean> cir){
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(localPlayer, LocalPlayerPatch.class);
        if(localPlayerPatch != null && localPlayerPatch.isTargetLockedOn()){
            this.customCamera = null;
            cir.setReturnValue(false);
        }
    }
}
