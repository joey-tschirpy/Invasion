package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

public abstract class Ability {
    private int cooldownLvl = 0;
    private int capacityLvl = 0;

    private final int MAX_COOLDOWN_LVL;
    private final int MAX_CAPACITY_LVL;

    private float[] cooldown;
    private int[] capacity;
    private int[] cooldownCost;
    private int[] capacityCost;

    private float cooldownTimer;
    private int amount;

    public Ability(final int MAX_CDL, final int MAX_CL)
    {
        MAX_COOLDOWN_LVL = MAX_CDL;
        MAX_CAPACITY_LVL = MAX_CL;

        cooldown = new float[MAX_CDL + 1];
        capacity = new int[MAX_CL + 1];

        cooldownCost = new int[MAX_CDL];
        capacityCost = new int[MAX_CL];
    }


    public abstract TextureRegion getTexRegNorm();
    public abstract TextureRegion getTexRegSel();
    public abstract Invasion.DefAbilityEnum getType();
    public abstract void addUpgradePage();
    public abstract void removeUpgradePage();
    public abstract void updateUpgradePage();

    public int getMaxCooldownLvl() {
        return MAX_COOLDOWN_LVL;
    }

    public int getMaxCapacityLvl() {
        return MAX_CAPACITY_LVL;
    }

    public int getCooldownLvl() {
        return cooldownLvl;
    }

    public int getCapacityLvl() {
        return capacityLvl;
    }

    public float getCooldown() {
        return cooldown[cooldownLvl];
    }

    public int getCapacity() {
        return capacity[capacityLvl];
    }

    public int getCooldownCost() {
        if (cooldownLvl == MAX_COOLDOWN_LVL) return 0;
        return cooldownCost[cooldownLvl];
    }

    public int getCapacityCost() {
        if (capacityLvl == MAX_CAPACITY_LVL) return 0;
        return capacityCost[capacityLvl];
    }

    public void setCooldown(float[] cooldown) {
        for (int i = 0; i <= MAX_COOLDOWN_LVL; i++) {
            this.cooldown[i] = cooldown[i];
        }
    }

    public void setCapacity(int[] capacity) {
        for (int i = 0; i <= MAX_CAPACITY_LVL; i++) {
            this.capacity[i] = capacity[i];
        }
    }

    public void setCooldownCost(int[] cooldownCost) {
        for (int i = 0; i < MAX_COOLDOWN_LVL; i++) {
            this.cooldownCost[i] = cooldownCost[i];
        }
    }

    public void setCapacityCost(int[] capacityCost) {
        for (int i = 0; i < MAX_CAPACITY_LVL; i++) {
            this.capacityCost[i] = capacityCost[i];
        }
    }

    public void upCooldownLvl() {
        if (cooldownLvl < MAX_COOLDOWN_LVL) {
            cooldownLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upCapacityLvl() {
        if (capacityLvl < MAX_CAPACITY_LVL) {
            capacityLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public float getCooldownTimer() {
        return cooldownTimer;
    }

    public void resetCooldownTimer() {
        cooldownTimer = 0f;
    }

    public void increaseCooldownTimer(float deltaTime) {
        if (amount > 0) {
            this.cooldownTimer += deltaTime;
        }
    }

    public boolean isUsable() {
        return cooldownTimer >= cooldown[cooldownLvl] && amount > 0;
    }

    public int getAmount() {
        return amount;
    }

    public void resetAmount() {
        amount = capacity[capacityLvl];
    }

    public boolean useAbility() {
        if (amount > 0) {
            amount -= 1;
            return true;
        }

        return false;
    }

    public abstract int[] getData();

    public abstract void setData(int[] data);

    public void setLevels(int CDLvl, int capLvl) {
        cooldownLvl = CDLvl;
        capacityLvl = capLvl;
    }

    public abstract void dispose();
}
