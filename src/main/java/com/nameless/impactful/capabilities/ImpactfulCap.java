package com.nameless.impactful.capabilities;

import com.google.common.collect.Maps;
import com.nameless.impactful.api.HitStopPropertiesReader;
import com.nameless.impactful.api.ICapabilityItem;
import com.nameless.impactful.client.CameraEngine;
import com.nameless.impactful.client.RadialBlurEngine;
import com.nameless.impactful.network.CPApplyVFX;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Map;
import java.util.UUID;

public class ImpactfulCap {
    public static final EntityDataAccessor<Boolean> HIT_STOP = new EntityDataAccessor<>(134, EntityDataSerializers.BOOLEAN);
    public int HIT_STOP_TIME = 0;
    public float HIT_STOP_SPEED = 1;
    private static final UUID EVENT_UUID = UUID.fromString("a0081299-9a78-4aa2-8650-5496ea6cfe68");
    public static final Map<WeaponCategory, HitStop> hit_stop_by_weapon_categories = Maps.newHashMap();

    public void onInitiate(Player player) {
        PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
        if(playerPatch != null){
            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID, (event) -> {
                if(event.getDamageSource() != null && event.getDamageSource().getAnimation().get() instanceof AttackAnimation animation){
                    int weaponCategoryId = event.getPlayerPatch().getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory().universalOrdinal();
                    int animationId = animation.getId();
                    AnimationPlayer animationPlayer = event.getPlayerPatch().getAnimator().getPlayerFor(event.getDamageSource().getAnimation());
                    float elapsedTime = animationPlayer != null ? animationPlayer.getElapsedTime() : 0f;
                    NetWorkManger.sendToPlayer(new CPApplyVFX(weaponCategoryId, animationId, elapsedTime), (ServerPlayer) player);
                }
            });

            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_ATTACK, EVENT_UUID, (event) -> {
                if(event.getDamageSource() != null && event.getDamageSource().getAnimation().get() instanceof AttackAnimation){
                    event.getPlayerPatch().getOriginal().getEntityData().set(HIT_STOP, true);
                }
            });


            //初始化
            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.ANIMATION_BEGIN_EVENT, EVENT_UUID, (event) -> {
                if(event.getAnimation() instanceof AttackAnimation animation){
                    CapabilityItem capabilityItem = playerPatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND);

                    AnimationPlayer animationPlayer = event.getPlayerPatch().getAnimator().getPlayerFor(animation.getRealAnimation());
                    float elapsedTime = animationPlayer != null ? animationPlayer.getElapsedTime() : 0f;
                    HitStop hitStop = animation.getPhaseByTime(elapsedTime).getProperty(HitStopPropertiesReader.HIT_STOP).orElse(((ICapabilityItem)capabilityItem).getHitStopEntry());
                    if(hitStop != null) {
                        this.HIT_STOP_TIME = hitStop.duration();
                        this.HIT_STOP_SPEED = hitStop.speed();
                    }
                }
            });


            /*
            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID, (event) -> {
                if(event.getPlayerPatch().getOriginal().getEntityData().get(HIT_STOP)){
                    if(this.HIT_STOP_TIME > 0) {
                        event.setAttackSpeed(event.getAttackSpeed() * this.HIT_STOP_SPEED);
                        this.HIT_STOP_TIME--;
                    } else {
                        event.getPlayerPatch().getOriginal().getEntityData().set(HIT_STOP, false);
                    }
                }
            }, 99);

             */

            playerPatch.getEventListener().addEventListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
                event.getPlayerPatch().getOriginal().getEntityData().set(HIT_STOP, false);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void ShakeCam(CameraEngine.ShakeEntry entry){
        CameraEngine.getInstance().shakeCamera(entry);
    }

    @OnlyIn(Dist.CLIENT)
    public void applyBlur(RadialBlurEngine.RadialBlur blur){
        RadialBlurEngine.getInstance().applyRadialBlurByAnim(blur);
    }

    public record HitStop(int duration, float speed){
        public HitStop copy(){
            return new HitStop(this.duration, this.speed);
        }
    }
}
