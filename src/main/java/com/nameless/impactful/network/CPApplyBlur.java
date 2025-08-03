package com.nameless.impactful.network;

import com.nameless.impactful.client.RadialBlurEngine;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPApplyBlur {
    private final int time;
    private final float strength;

    public CPApplyBlur(int time, float strength){
        this.time = time;
        this.strength = strength;
    }
    public CPApplyBlur(FriendlyByteBuf buf){
        this.time = buf.readInt();
        this.strength = buf.readFloat();
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeInt(time);
        buf.writeFloat(strength);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            if(!ClientConfig.DISABLE_RADIAL_BLUR.get())
                RadialBlurEngine.getInstance().applyRadialBlur(time, strength);
        });
        context.get().setPacketHandled(true);
    }
}
