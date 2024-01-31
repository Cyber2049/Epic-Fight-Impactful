package com.nameless.impactful;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nameless.impactful.capabilities.HitStopCap;
import com.nameless.impactful.capabilities.HitStopProvider;
import com.nameless.impactful.capabilities.ImpactfulCapabilities;
import com.nameless.impactful.command.ShakeCameraCommand;
import com.nameless.impactful.config.CommonConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("impactful")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(ShakeCameraCommand.register()))
        );
    }

    @SubscribeEvent
    public void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player){
            HitStopProvider provider = new HitStopProvider();
            event.addCapability(new ResourceLocation(Impactful.MOD_ID, "hit_stop"), provider);
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof Player player){
            HitStopCap hitStopCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
            if(hitStopCap != null){
                hitStopCap.onInitiate(player);
            }
        }
    }
    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event){
        if(event.getEntity() instanceof Player player) {
            if(player.level.isClientSide || CommonConfig.DISABLE_HIT_STOP.get()) return;
            HitStopCap hitStopCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
            if(hitStopCap != null){
                hitStopCap.onUpdate(player);
            }
        }
    }
}
