package com.nameless.impactful.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HitStopProvider implements ICapabilityProvider {


    private final LazyOptional<HitStopCap> optional;
    private final HitStopCap hitStopCap;
    public HitStopProvider()
    {
        this.hitStopCap = new HitStopCap();
        optional = LazyOptional.of(() -> this.hitStopCap);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ImpactfulCapabilities.INSTANCE) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }
}
