package com.nameless.impactful.network;

import com.nameless.impactful.Impactful;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetWorkManger {
    private static SimpleChannel INSTANCE;
    private static int ID;
    private static int id(){
        return ID++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Impactful.MOD_ID, "message"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(CameraShake.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CameraShake::new)
                .encoder(CameraShake::encode)
                .consumer(CameraShake::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
