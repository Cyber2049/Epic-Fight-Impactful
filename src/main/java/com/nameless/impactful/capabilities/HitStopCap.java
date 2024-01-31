package com.nameless.impactful.capabilities;

import com.mojang.datafixers.util.Pair;
import com.nameless.impactful.config.CommonConfig;
import com.nameless.impactful.network.CameraShake;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

public class HitStopCap {
    public static final EntityDataAccessor<Boolean> HIT_STOP = new EntityDataAccessor<>(176, EntityDataSerializers.BOOLEAN);
    public int LAST_HIT_TICK = 0;
    private int HIT_STOP_TIME = 0;
    private static final UUID EVENT_UUID = UUID.fromString("a0081299-9a78-4aa2-8650-5496ea6cfe68");
    public HitStopCap(){}
    public void onInitiate(Player player) {
        player.getEntityData().define(HIT_STOP, false);
        player.getEntityData().set(HIT_STOP, false);

        PlayerPatch<?> playerPatch = (PlayerPatch<?>) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
        if(playerPatch != null){
            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
                if(event.getAnimation() instanceof AttackAnimation){
                    StaticAnimation animation = event.getAnimation();
                    if(CommonConfig.hit_stop_by_animation.containsKey(animation)){
                        this.HIT_STOP_TIME = CommonConfig.hit_stop_by_animation.getOrDefault(animation,Pair.of(2, 0.5F)).getFirst();
                    } else {
                        WeaponCategory category =playerPatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory();
                        this.HIT_STOP_TIME = CommonConfig.hit_stop_by_weapon_categories.getOrDefault(category, Pair.of(2, 0.5F)).getFirst();
                    }
                }
            });

            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
                if((event.getDamageSource() != null)){
                    StaticAnimation animation = event.getDamageSource().getAnimation();
                    Pair<Integer, Float> cameraShake;
                    if(CommonConfig.camera_shake_by_animation.containsKey(animation)){
                        cameraShake = CommonConfig.camera_shake_by_animation.getOrDefault(animation,Pair.of(0, 1F));
                    } else {
                        WeaponCategory category = playerPatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory();
                        cameraShake = CommonConfig.camera_shake_by_weapon_categories.getOrDefault(category, Pair.of(0, 1F));
                    }
                    if(this.HIT_STOP_TIME != 0 && !CommonConfig.DISABLE_HIT_STOP.get()) {
                        player.getEntityData().set(HIT_STOP, true);
                        LAST_HIT_TICK = player.tickCount;
                    }
                    if(cameraShake.getFirst() > 0) {
                        NetWorkManger.sendToPlayer(new CameraShake(cameraShake.getFirst(), cameraShake.getSecond(), 3), (ServerPlayer) player);
                    }
                }
            });
        }
    }


    public void onUpdate(Player player){
        if(player.getEntityData().get(HIT_STOP) && player.tickCount - this.LAST_HIT_TICK > this.HIT_STOP_TIME){
            player.getEntityData().set(HIT_STOP, false);
        }
    }
}
