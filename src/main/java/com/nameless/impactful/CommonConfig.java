package com.nameless.impactful;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> WEAPON_CATEGORIES_SETTING;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ANIMATIONS_SETTING;
    public static final ForgeConfigSpec.ConfigValue<Integer> GUARD_CAMERASHAKE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Float> GUARD_CAMERASHAKE_STRENGTH;
    public static final ForgeConfigSpec.ConfigValue<Integer> ADVANCEDGUARD_CAMERASHAKE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Float> ADVANCEDGUARD_CAMERASHAKE_STRENGTH;
    public static final ForgeConfigSpec.ConfigValue<Integer> GUARDBREAK_CAMERASHAKE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Float> GUARDBREAK_CAMERASHAKE_STRENGTH;
    public static final ForgeConfigSpec SPEC;

    public static final Map<WeaponCategory, Pair<Integer, Float>> hit_stop_by_weapon_categories = Maps.newHashMap();
    public static final Map<WeaponCategory, Pair<Integer, Float>> camera_shake_by_weapon_categories = Maps.newHashMap();
    public static final Map<StaticAnimation, Pair<Integer, Float>> hit_stop_by_animation = Maps.newHashMap();
    public static final Map<StaticAnimation, Pair<Integer, Float>> camera_shake_by_animation = Maps.newHashMap();

    private static final List<String> categories_default_setting = List.of(
            "greatsword 4 0.1 15 2",
            "longsword 3 0.25 10 1.5",
            "tachi 3 0.25 10 1.5",
            "uchigatana 3 0.2 10 1.3",
            "spear 3 0.2 15 1",
            "trident 3 0.2 15 1",
            "sword 3 0.45 8 0.75",
            "dagger 2 0.6 5 0.5",
            "fist 4 0.3 10 0.5"
    );

    static {
        BUILDER.push("weapon categories setting");
        BUILDER.comment(
                "format: weapon_categories hit_stop_duration speed_ratio camera_shake_duration camera_shake_strength",
                "example: greatsword 4 0.1 15 2",
                "weapon_categories: greatsword, longsword, sword, etc",
                "hit_stop_duration: duration of hit stop(tick)",
                "speed_ration: percentage rate, 0.0 - 1.0",
                "camera_shake_duration: duration of screen shake(tick)",
                "camera_shake_strength: amplitude of screen shake");
        WEAPON_CATEGORIES_SETTING = BUILDER.defineList("weapon_categories_setting", categories_default_setting, obj -> true);
        BUILDER.pop();

        BUILDER.push("animation setting");
        BUILDER.comment(
                "priority higher than weapon categories setting and override it",
                "format: attack_animation hit_stop_duration speed_ratio camera_shake_duration camera_shake_strength",
                "example: epicfight:biped/combat/greatsword_dash 4 0 20 5",
                "attack_animation: modid:path",
                "hit_stop_duration: duration of hit stop(tick)",
                "speed_ration: percentage rate, 0.0 - 1.0",
                "camera_shake_duration: duration of screen shake(tick)",
                "camera_shake_strength: amplitude of screen shake");
        ANIMATIONS_SETTING = BUILDER.defineList("animation_setting", ArrayList::new, obj -> true);
        BUILDER.pop();

        BUILDER.push("screen shake by guard");
        BUILDER.comment("screen shake when guard success");
        GUARD_CAMERASHAKE_TIME = BUILDER.define("guard_screen_shake_time", 20);
        GUARD_CAMERASHAKE_STRENGTH = BUILDER.define("guard_screen_shake_strength", 2.5F);
        BUILDER.pop();

        BUILDER.push("screen shake by advanced guard");
        BUILDER.comment("screen shake when advanced guard success, like impactful guard or parry");
        ADVANCEDGUARD_CAMERASHAKE_TIME = BUILDER.define("advanced_guard_screen_shake_time", 15);
        ADVANCEDGUARD_CAMERASHAKE_STRENGTH = BUILDER.define("advanced_guard_screen_shake_strength", 1.5F);
        BUILDER.pop();

        BUILDER.push("screen shake when guard break");
        BUILDER.comment("screen shake when guard break");
        GUARDBREAK_CAMERASHAKE_TIME = BUILDER.define("guard_break_screen_shake_time", 30);
        GUARDBREAK_CAMERASHAKE_STRENGTH = BUILDER.define("guard_break_screen_shake_strength", 5F);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void load(){
        List<? extends String> hit_stop_by_weapon_categories_list = WEAPON_CATEGORIES_SETTING.get();
        for(String set : hit_stop_by_weapon_categories_list){
            String[] entry = set.split(" ");
            WeaponCategory weaponCategory = WeaponCategory.ENUM_MANAGER.get(entry[0]);
            hit_stop_by_weapon_categories.put(weaponCategory, Pair.of(Integer.parseInt(entry[1]),Float.parseFloat(entry[2])));
            camera_shake_by_weapon_categories.put(weaponCategory, Pair.of(Integer.parseInt(entry[3]), Float.parseFloat(entry[4])));
        }
        List<? extends String> hit_stop_by_animation_list = ANIMATIONS_SETTING.get();
        for(String set : hit_stop_by_animation_list){
            String[] entry = set.split(" ");
            StaticAnimation animation = EpicFightMod.getInstance().animationManager.findAnimationByPath(entry[0]);
            hit_stop_by_animation.put(animation, Pair.of(Integer.parseInt(entry[1]),Float.parseFloat(entry[2])));
            camera_shake_by_animation.put(animation, Pair.of(Integer.parseInt(entry[3]), Float.parseFloat(entry[4])));
        }
    }
}
