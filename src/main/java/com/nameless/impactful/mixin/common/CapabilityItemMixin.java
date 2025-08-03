package com.nameless.impactful.mixin.common;

import com.nameless.impactful.capabilities.ImpactfulCap;
import com.nameless.impactful.client.ICapabilityItem;
import org.spongepowered.asm.mixin.Mixin;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@Mixin(CapabilityItem.class)
public class CapabilityItemMixin implements ICapabilityItem {
    private ImpactfulCap.HitStop hitStop;

    public void addHitStop(ImpactfulCap.HitStop hitStop) {
        this.hitStop = hitStop;
    }

    @Override
    public ImpactfulCap.HitStop getHitStopEntry() {
        return this.hitStop;
    }
}
