package com.nameless.impactful.skill;

import yesman.epicfight.skill.SkillCategory;

public enum Categories implements SkillCategory {
    HIT_STOP(false, false, false);

    boolean shouldSave;
    boolean shouldSyncronize;
    boolean modifiable;
    int id;

    Categories(boolean shouldSave, boolean shouldSyncronize, boolean modifiable) {
        this.shouldSave = shouldSave;
        this.shouldSyncronize = shouldSyncronize;
        this.modifiable = modifiable;
        this.id = this.ENUM_MANAGER.assign(this);
    }

    @Override
    public boolean shouldSave() {
        return this.shouldSave;
    }
    @Override
    public boolean shouldSynchronize() {
        return this.shouldSyncronize;
    }
    @Override
    public boolean learnable() {
        return this.modifiable;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }

}
