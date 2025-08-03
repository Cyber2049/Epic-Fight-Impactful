package com.nameless.impactful.config;

import net.minecraftforge.common.ForgeConfigSpec;


public class CommonConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_HIT_STOP;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("disable hit stop");
        DISABLE_HIT_STOP = builder.define("disable_hit_stop", false);
        builder.pop();

        SPEC = builder.build();
    }
}
