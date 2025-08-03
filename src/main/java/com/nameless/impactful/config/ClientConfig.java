package com.nameless.impactful.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Double> SCREEN_SHAKE_AMPLITUDE_RATE;
    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_SCREEN_SHAKE;
    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_RADIAL_BLUR;
    public static ForgeConfigSpec.ConfigValue<Double> RADIAL_BLUR_INTENSITY_RATE;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("global setting");
        DISABLE_SCREEN_SHAKE = builder.define("disable_screen_shake", false);
        DISABLE_RADIAL_BLUR = builder.define("disable_radial_blur", false);
        SCREEN_SHAKE_AMPLITUDE_RATE = builder.defineInRange("screen_shake_amplitude",1D,0D,10D);
        RADIAL_BLUR_INTENSITY_RATE = builder.defineInRange("radial_blur_rate",0.025D,0D,10D);
        builder.pop();

        SPEC = builder.build();
    }
}
