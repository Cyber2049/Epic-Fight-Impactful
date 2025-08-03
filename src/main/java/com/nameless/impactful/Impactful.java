package com.nameless.impactful;

import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.client.RadialBlurEngine;
import com.nameless.impactful.config.ClientConfig;
import com.nameless.impactful.config.CommonConfig;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Impactful.MOD_ID)
public class Impactful {
    public static final String MOD_ID = "impactful";
    public Impactful(FMLJavaModLoadingContext context){
        IEventBus bus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        NetWorkManger.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        new CameraEngine();
        new RadialBlurEngine();
    }

}
