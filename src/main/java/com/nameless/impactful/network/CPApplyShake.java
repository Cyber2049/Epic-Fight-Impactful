package com.nameless.impactful.network;

import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPApplyShake {
    private final int time;
    private final float strength;
    private final float frequency;
    public CPApplyShake(int time, float strength, float frequency){
        this.time = time;
        this.strength = strength;
        this.frequency = frequency;
    }
    public CPApplyShake(FriendlyByteBuf buf){
        this.time = buf.readInt();
        this.strength = buf.readFloat();
        this.frequency = buf.readFloat();
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeInt(time);
        buf.writeFloat(strength);
        buf.writeFloat(frequency);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            if(!ClientConfig.DISABLE_SCREEN_SHAKE.get())
                CameraEngine.getInstance().shakeCamera(strength, time, frequency);
        });
        context.get().setPacketHandled(true);
    }
}
