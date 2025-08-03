package com.nameless.impactful.network;

import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.client.RadialBlurEngine;
import com.nameless.impactful.config.ClientConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPApplyVFX {
    private final int weaponCategoryId;
    private final int animationId;
    private final float elapsedTime;
    public CPApplyVFX(int weaponCategoryId, int animationId, float elapsedTime){
        this.weaponCategoryId = weaponCategoryId;
        this.animationId = animationId;
        this.elapsedTime = elapsedTime;
    }
    public CPApplyVFX(FriendlyByteBuf buf){
        this.weaponCategoryId = buf.readInt();
        this.animationId = buf.readInt();
        this.elapsedTime = buf.readFloat();
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeInt(weaponCategoryId);
        buf.writeInt(animationId);
        buf.writeFloat(elapsedTime);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            if(!ClientConfig.DISABLE_SCREEN_SHAKE.get())
                CameraEngine.getInstance().shakeCameraByAnim(this.animationId, this.elapsedTime);
            if(!ClientConfig.DISABLE_RADIAL_BLUR.get())
                RadialBlurEngine.getInstance().applyRadialBlurByAnim(this.animationId, this.elapsedTime);
        });
        context.get().setPacketHandled(true);
    }
}
