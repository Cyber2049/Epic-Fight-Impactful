package com.nameless.impactful.skill;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum Slots implements SkillSlot {

    HIT_STOP(Categories.HIT_STOP);
    SkillCategory category;
    int id;

    Slots(yesman.epicfight.skill.SkillCategory category) {
        this.category = category;
        this.id = this.ENUM_MANAGER.assign(this);
    }

    @Override
    public SkillCategory category() {
        return this.category;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
