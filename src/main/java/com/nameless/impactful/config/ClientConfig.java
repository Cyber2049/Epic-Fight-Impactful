package com.nameless.impactful.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Float> SCREEN_SHAKE_AMPLITUDE_MULTIPLY;
    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_SCREEN_SHAKE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("client screen shake setting");
        DISABLE_SCREEN_SHAKE = builder.define("disable_screen_shake", false);
        SCREEN_SHAKE_AMPLITUDE_MULTIPLY = builder.define("global_screen_shake_amplitude",1F);
        builder.pop();
        SPEC = builder.build();
    }
}
