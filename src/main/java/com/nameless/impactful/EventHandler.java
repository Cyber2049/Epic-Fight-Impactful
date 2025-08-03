package com.nameless.impactful;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nameless.impactful.capabilities.ImpactfulCap;
import com.nameless.impactful.capabilities.ImpactfulCapabilities;
import com.nameless.impactful.capabilities.ImpactfulProvider;
import com.nameless.impactful.command.RadialBlurCommand;
import com.nameless.impactful.command.ShakeCameraCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.nameless.impactful.capabilities.ImpactfulCap.HIT_STOP;

public class EventHandler {

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("impactful")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(ShakeCameraCommand.register())
                                .then(RadialBlurCommand.register())
                        )
        );
    }

    @SubscribeEvent
    public void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player){
            ImpactfulProvider provider = new ImpactfulProvider();
            event.addCapability(ResourceLocation.fromNamespaceAndPath(Impactful.MOD_ID, "hit_stop"), provider);
            player.getEntityData().define(HIT_STOP, false);
            player.getEntityData().set(HIT_STOP, false);
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player player){
            ImpactfulCap impactfulCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
            if(impactfulCap != null){
                impactfulCap.onInitiate(player);
            }
        }
    }
    /*
    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingTickEvent event){
        if(event.getEntity() instanceof Player player) {
            if(player.level().isClientSide() || CommonConfig.DISABLE_HIT_STOP.get()) return;
            ImpactfulCap impactfulCap = player.getCapability(ImpactfulCapabilities.INSTANCE).orElse(null);
            if(impactfulCap != null){
                impactfulCap.onUpdate(player);
            }
        }
    }

     */
}
