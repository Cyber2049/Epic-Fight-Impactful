package com.nameless.impactful;

import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.config.ClientConfig;
import com.nameless.impactful.config.CommonConfig;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Impactful.MOD_ID)
public class Impactful {
    public static final String MOD_ID = "impactful";
    public Impactful(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::setupComplete);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        NetWorkManger.register();
    }

    private void setupComplete(final FMLLoadCompleteEvent event){
        CommonConfig.load();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        new CameraEngine();
    }
}
