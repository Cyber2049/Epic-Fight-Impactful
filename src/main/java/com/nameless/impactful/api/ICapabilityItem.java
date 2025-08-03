package com.nameless.impactful.api;

import com.nameless.impactful.capabilities.ImpactfulCap;

public interface ICapabilityItem {
    ImpactfulCap.HitStop getHitStopEntry();
    void addHitStop(ImpactfulCap.HitStop hitStop);
}
