package com.nameless.impactful;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.command.ShakeCameraCommand;
import com.nameless.impactful.config.ClientConfig;
import com.nameless.impactful.config.CommonConfig;
import com.nameless.impactful.gameassets.HitStopSkill;
import com.nameless.impactful.network.NetWorkManger;
import com.nameless.impactful.skill.Categories;
import com.nameless.impactful.skill.Slots;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yesman.epicfight.skill.SkillCategory;

@Mod(Impactful.MOD_ID)
public class Impactful {
    public static final String MOD_ID = "impactful";
    public Impactful(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::loadConfig);
        HitStopSkill.registerSkills();
        SkillCategory.ENUM_MANAGER.loadPreemptive(Categories.class);
        Slots.ENUM_MANAGER.loadPreemptive(Slots.class);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        NetWorkManger.register();
    }

    private void loadConfig(final FMLLoadCompleteEvent event) {
        CommonConfig.load();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        new CameraEngine();
    }

    private void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("impactful")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(ShakeCameraCommand.register()))
        );
    }

}
