package com.nameless.impactful.mixin;


import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin
{
    private boolean better_third_person_compat;

    private final List<String> better_third_person_compat_mixin_class = List.of(
            "com.nameless.impactful.mixin.BTPCompat.MixinCustomCameraManager",
            "com.nameless.impactful.mixin.BTPCompat.MixinLocalPlayerPatch",
            "com.nameless.impactful.mixin.BTPCompat.MixinMouseHandler",
            "com.nameless.impactful.mixin.BTPCompat.MixinRenderEngine"
    );

    @Override
    public void onLoad(String mixinPackage)
    {
        try
        {
            Class.forName("io.socol.betterthirdperson.BetterThirdPerson");
            this.better_third_person_compat = true;
        }
        catch (ClassNotFoundException e)
        {
            this.better_third_person_compat = false;
        }
    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (!this.better_third_person_compat) {
            for (String name : this.better_third_person_compat_mixin_class) {
                    return !mixinClassName.equals(name);
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {

    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }
}
