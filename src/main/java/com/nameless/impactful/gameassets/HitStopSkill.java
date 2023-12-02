package com.nameless.impactful.gameassets;

import com.nameless.impactful.Impactful;
import com.nameless.impactful.skill.HitStop;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

@Mod.EventBusSubscriber(modid = Impactful.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HitStopSkill {
    public static Skill HIT_STOP;

    public static void registerSkills(){
        SkillManager.register(HitStop::new, HitStop.createHitStopBuilder(), Impactful.MOD_ID,"hit_stop");
    }
    @SubscribeEvent
    public static void buildSkills(SkillBuildEvent event){
        HIT_STOP = event.build(Impactful.MOD_ID,"hit_stop");
    }
}
