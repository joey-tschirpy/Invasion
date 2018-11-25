package com.invasion.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;

public abstract class Turret {
    private Vector2 mid = new Vector2();

    private int damageLvl = 0;
    private int fireRateLvl = 0;
    private int bulletSpeedLvl = 0;

    private final int MAX_DMG_LVL;
    private final int MAX_FIRERATE_LVL;
    private final int MAX_BULLETSPEED_LVL;

    private float[] damage;
    private float[] fireRate;
    private float[] bulletSpeed;

    private int[] damageCost;
    private int[] fireRateCost;
    private int[] bulletSpeedCost;

    public boolean isRotatable;

    public Turret(final int MAX_DL, final int MAX_FRL, final int MAX_BSL)
    {
        MAX_DMG_LVL = MAX_DL;
        MAX_FIRERATE_LVL = MAX_FRL;
        MAX_BULLETSPEED_LVL = MAX_BSL;

        damage = new float[MAX_DL + 1];
        fireRate = new float[MAX_FRL + 1];
        bulletSpeed = new float[MAX_BSL + 1];

        damageCost = new int[MAX_DL];
        fireRateCost = new int[MAX_FRL];
        bulletSpeedCost = new int[MAX_BSL];
    }

    public abstract TextureRegion getTexRegNorm();
    public abstract TextureRegion getTexRegSel();
    public abstract Invasion.DefTurretEnum getType();
    public abstract void addUpgradePage();
    public abstract void removeUpgradePage();
    public abstract void updateUpgradePage();
    public abstract void playAttackSound();

    public Vector2 getCenter() {
        return mid;
    }

    public void setCenter(float x, float y) {
        mid.x = x;
        mid.y = y;
    }

    public int getMaxDmgLvl() {
        return MAX_DMG_LVL;
    }

    public int getMaxFireRateLvl() {
        return MAX_FIRERATE_LVL;
    }

    public int getMaxBulletSpeedLvl() {
        return MAX_BULLETSPEED_LVL;
    }

    public int getDamageLvl() {
        return damageLvl;
    }

    public int getFireRateLvl() {
        return fireRateLvl;
    }

    public int getBulletSpeedLvl() {
        return bulletSpeedLvl;
    }

    public float getDamage() {
        return damage[damageLvl];
    }

    public float getFireRate() {
        return fireRate[fireRateLvl];
    }

    public float getBulletSpeed() {
        return bulletSpeed[bulletSpeedLvl];
    }

    public int getDamageCost() {
        if (damageLvl == MAX_DMG_LVL) return 0;
        return damageCost[damageLvl];
    }

    public int getFireRateCost() {
        if (fireRateLvl == MAX_FIRERATE_LVL) return 0;
        return fireRateCost[fireRateLvl];
    }

    public int getBulletSpeedCost() {
        if (bulletSpeedLvl == MAX_BULLETSPEED_LVL) return 0;
        return bulletSpeedCost[bulletSpeedLvl];
    }

    public void setDamage(float[] damage) {
        for (int i = 0; i <= MAX_DMG_LVL; i++) {
            this.damage[i] = damage[i];
        }
    }

    public void setFireRate(float[] fireRate) {
        for (int i = 0; i <= MAX_FIRERATE_LVL; i++) {
            this.fireRate[i] = fireRate[i];
        }
    }

    public void setBulletSpeed(float[] bulletSpeed) {
        for (int i = 0; i <= MAX_BULLETSPEED_LVL; i++) {
            this.bulletSpeed[i] = bulletSpeed[i];
        }
    }

    public void setDamageCost(int[] dmgCost) {
        for (int i = 0; i < MAX_DMG_LVL; i++) {
            this.damageCost[i] = dmgCost[i];
        }
    }

    public void setFireRateCost(int[] fireRateCost) {
        for (int i = 0; i < MAX_FIRERATE_LVL; i++) {
            this.fireRateCost[i] = fireRateCost[i];
        }
    }

    public void setBulletSpeedCost(int[] bulletSpeedCost) {
        for (int i = 0; i < MAX_BULLETSPEED_LVL; i++) {
            this.bulletSpeedCost[i] = bulletSpeedCost[i];
        }
    }

    public void upDamageLvl() {
        if (damageLvl < MAX_DMG_LVL) {
            damageLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upFireRateLvl() {
        if (fireRateLvl < MAX_FIRERATE_LVL) {
            fireRateLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upBulletSpeedLvl() {
        if (bulletSpeedLvl < MAX_BULLETSPEED_LVL) {
            bulletSpeedLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public abstract int[] getData();

    public abstract void setData(int[] data);

    public void setLevels(int dmgLvl, int FRLvl, int BSLvl) {
        damageLvl = dmgLvl;
        fireRateLvl = FRLvl;
        bulletSpeedLvl = BSLvl;
    }

    public abstract void dispose();
}
