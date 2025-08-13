package com.nameless.impactful.network;

import com.nameless.impactful.client.RadialBlurEngine;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPApplyBlur {
    private final int time;
    private final float strength;
    private final int decay_time;

    public CPApplyBlur(int time, float strength, int decay_time){
        this.time = time;
        this.strength = strength;
        this.decay_time = decay_time;
    }
    public CPApplyBlur(FriendlyByteBuf buf){
        this.time = buf.readInt();
        this.strength = buf.readFloat();
        this.decay_time = buf.readInt();
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeInt(time);
        buf.writeFloat(strength);
        buf.writeInt(decay_time);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            if(!ClientConfig.DISABLE_RADIAL_BLUR.get())
                RadialBlurEngine.getInstance().applyRadialBlur(time, strength, decay_time);
        });
        context.get().setPacketHandled(true);
    }
}
