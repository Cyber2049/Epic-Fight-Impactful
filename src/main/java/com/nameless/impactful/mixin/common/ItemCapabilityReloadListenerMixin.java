package com.nameless.impactful.mixin.common;

import com.nameless.impactful.api.ICapabilityItem;
import com.nameless.impactful.capabilities.ImpactfulCap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@Mixin(ItemCapabilityReloadListener.class)
public class ItemCapabilityReloadListenerMixin {


    @Inject(method = "deserializeWeapon(Lnet/minecraft/world/item/Item;Lnet/minecraft/nbt/CompoundTag;)Lyesman/epicfight/world/capabilities/item/CapabilityItem;", at = @At("RETURN"), cancellable = false, remap = false)
    private static void addHitStop(Item item, CompoundTag tag, CallbackInfoReturnable<CapabilityItem> cir){
        CapabilityItem result = cir.getReturnValue();

        if (tag.contains("hit_stop")) {
            CompoundTag hitStopTag = tag.getCompound("hit_stop");
            int duration = hitStopTag.getInt("duration");
            float speed = hitStopTag.getFloat("speed");
            ((ICapabilityItem)result).addHitStop(new ImpactfulCap.HitStop(duration, speed));
        }
    }
}
