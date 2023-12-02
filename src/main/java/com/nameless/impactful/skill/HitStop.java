package com.nameless.impactful.skill;

import com.mojang.datafixers.util.Pair;
import com.nameless.impactful.CommonConfig;
import com.nameless.impactful.network.CameraShake;
import com.nameless.impactful.network.NetWorkManger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.damagesource.IndirectEpicFightDamageSource;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

public class HitStop extends Skill {
    public static final SkillDataManager.SkillDataKey<Boolean> HIT_STOP = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Integer> LAST_HIT_TICK = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    private int HIT_STOP_TIME;
    private static final UUID EVENT_UUID = UUID.fromString("a0081299-9a78-4aa2-8650-5496ea6cfe68");
    public static Skill.Builder<HitStop> createHitStopBuilder(){
        return (new Builder<HitStop>()).setCategory(Categories.HIT_STOP).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.NONE);
    }

    public HitStop(Builder<? extends Skill> builder){
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        container.getDataManager().registerData(HIT_STOP);
        container.getDataManager().registerData(LAST_HIT_TICK);

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
            if((event.getDamageSource() != null) && !(event.getDamageSource() instanceof IndirectEpicFightDamageSource)){
                StaticAnimation animation = event.getDamageSource().getAnimation();
                Pair<Integer, Float> cameraShake;
                if(CommonConfig.hit_stop_by_animation.containsKey(animation) && CommonConfig.camera_shake_by_animation.containsKey(animation)){
                    this.HIT_STOP_TIME = CommonConfig.hit_stop_by_animation.getOrDefault(animation,Pair.of(2, 0.5F)).getFirst();
                    cameraShake = CommonConfig.camera_shake_by_animation.getOrDefault(animation,Pair.of(0, 1F));

                } else {
                    WeaponCategory category = container.getExecuter().getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory();
                    this.HIT_STOP_TIME = CommonConfig.hit_stop_by_weapon_categories.getOrDefault(category, Pair.of(2, 0.5F)).getFirst();
                    cameraShake = CommonConfig.camera_shake_by_weapon_categories.getOrDefault(category, Pair.of(0, 1F));
                }

                container.getDataManager().setDataSync(HIT_STOP,true, (ServerPlayer) container.getExecuter().getOriginal());
                container.getDataManager().setData(LAST_HIT_TICK,  event.getPlayerPatch().getOriginal().tickCount);
                if(cameraShake.getFirst() > 0) {
                    NetWorkManger.sendToPlayer(new CameraShake(cameraShake.getFirst(), cameraShake.getSecond()), (ServerPlayer) container.getExecuter().getOriginal());
                }
            }
        });
    }

    @Override
    public void onRemoved(SkillContainer container) {
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST,EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        if(!container.getExecuter().isLogicalClient() && container.getDataManager().getDataValue(HIT_STOP) && container.getExecuter().getOriginal().tickCount - container.getDataManager().getDataValue(LAST_HIT_TICK) > this.HIT_STOP_TIME){
            container.getDataManager().setDataSync(HIT_STOP,false, (ServerPlayer) container.getExecuter().getOriginal());
        }
    }
}
