package com.nameless.impactful.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImpactfulProvider implements ICapabilityProvider {


    private final LazyOptional<ImpactfulCap> optional;
    private final ImpactfulCap impactfulCap;
    public ImpactfulProvider()
    {
        this.impactfulCap = new ImpactfulCap();
        optional = LazyOptional.of(() -> this.impactfulCap);
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
