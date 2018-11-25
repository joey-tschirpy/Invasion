package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

public class Structure {
    private static final Structure instance = new Structure();

    private static TextureRegion texRegNorm;
    private static TextureRegion texRegSel;
    private static TextureRegion[] texRegUpgradeBtn;
    private static TextureRegion[] numberTexRegs;

    private Image upgradeBgImg;

    private float integrity;
    private float shield;

    // Integrity Capacity
    private int integrityCapLvl = 0;
    private final int MAX_INTEGRITY_CAP_LVL = 10;
    private float[] integrityCap;
    private int[] integrityCapCost;

    private ProgressBar integrityCapLvlBar;
    private ImageButton integrityCapUpgradeBtn;
    private Image[] integrityCapCostDigitImgs;

    // Repair
    private int repairLvl = 0;
    private final int MAX_REPAIR_LVL = 10;
    private float[] repair;
    private int[] repairCost;

    private ProgressBar repairLvlBar;
    private ImageButton repairUpgradeBtn;
    private Image[] repairCostDigitImgs;

    // Shield Capacity
    private int shieldCapLvl = 0;
    private final int MAX_SHIELD_CAP_LVL = 10;
    private float[] shieldCap;
    private int[] shieldCapCost;

    private ProgressBar shieldCapLvlBar;
    private ImageButton shieldCapUpgradeBtn;
    private Image[] shieldCapCostDigitImgs;

    // Recharge
    private int rechargeLvl = 0;
    private final int MAX_RECHARGE_LVL = 10;
    private float[] recharge;
    private int[] rechargeCost;

    private ProgressBar rechargeLvlBar;
    private ImageButton rechargeUpgradeBtn;
    private Image[] rechargeCostDigitImgs;

    static Structure getInstance() {
        return instance;
    }

    private Structure() {
        // Integrity Capacity
        integrityCap = new float[MAX_INTEGRITY_CAP_LVL + 1];
        integrityCap[0] = 100; // base integrity capacity
        for (int i = 1; i < integrityCap.length; i++) {
            integrityCap[i] = integrityCap[i - 1] + (i * integrityCap[0]); // integrity capacity: 100, 200, 400, 800, 1400, 2200, 3200, 4400, 5800, 7400, 9200
        }

        // Integrity Capacity Cost
        integrityCapCost = new int[MAX_INTEGRITY_CAP_LVL];
        integrityCapCost[0] = 100; // first upgrade cost
        for (int i = 1; i < integrityCapCost.length; i++) {
            integrityCapCost[i] = integrityCapCost[i - 1] * 2; // integrity capacity cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        // Repair (percentage)
        repair = new float[MAX_REPAIR_LVL + 1];
        repair[0] = 0; // base repair
        for (int i = 1; i < repair.length; i++) {
            repair[i] = repair[i - 1] + 0.001f; // repair: 0, 0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.01
        }

        // Repair Cost
        repairCost = new int[MAX_REPAIR_LVL];
        repairCost[0] = 100; // first upgrade cost
        for (int i = 1; i < repairCost.length; i++) {
            repairCost[i] = repairCost[i - 1] * 2; // repair cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        // Shield Capacity
        shieldCap = new float[MAX_SHIELD_CAP_LVL + 1];
        shieldCap[0] = 100; // base shield capacity
        for (int i = 1; i < shieldCap.length; i++) {
            shieldCap[i] = shieldCap[i - 1] + (i * shieldCap[0]); // shield capacity: 100, 200, 400, 800, 1400, 2200, 3200, 4400, 5800, 7400, 9200
        }

        // Shield Capacity Cost
        shieldCapCost = new int[MAX_SHIELD_CAP_LVL];
        shieldCapCost[0] = 100; // first upgrade cost
        for (int i = 1; i < shieldCapCost.length; i++) {
            shieldCapCost[i] = shieldCapCost[i - 1] * 2; // shield capacity cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        // Recharge (percentage)
        recharge = new float[MAX_RECHARGE_LVL + 1];
        recharge[0] = 0; // base recharge
        for (int i = 1; i < recharge.length; i++) {
            recharge[i] = recharge[i - 1] + 0.001f; // recharge: 0, 0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 0.01
        }

        // Recharge Cost
        rechargeCost = new int[MAX_RECHARGE_LVL];
        rechargeCost[0] = 100; // first upgrade cost
        for (int i = 1; i < rechargeCost.length; i++) {
            rechargeCost[i] = rechargeCost[i - 1] * 2; // recharge cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        Texture texBtn = new Texture(Gdx.files.internal("Defences/StructureBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, 0, 0, texBtn.getWidth() / 2, texBtn.getHeight());
        texRegSel = new TextureRegion(texBtn, texBtn.getWidth() / 2, 0, texBtn.getWidth() / 2,
                texBtn.getHeight());

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_Structure.png")));
        upgradeBgImg.setPosition(1125, 117);

        // 0: norm, 1: over, 2: disabled
        Texture texUpgradeBtn = new Texture(Gdx.files.internal("Defences/UpgradeImgBtnSht.png"));
        texRegUpgradeBtn = new TextureRegion[3];
        TextureRegionDrawable[] upgradeBtnDrawables = new TextureRegionDrawable[texRegUpgradeBtn.length];
        for (int i = 0; i < texRegUpgradeBtn.length; i++) {
            texRegUpgradeBtn[i] = new TextureRegion(texUpgradeBtn, 0,
                    i * texUpgradeBtn.getHeight() / texRegUpgradeBtn.length,
                    texUpgradeBtn.getWidth(), texUpgradeBtn.getHeight() / texRegUpgradeBtn.length);
            upgradeBtnDrawables[i] = new TextureRegionDrawable(texRegUpgradeBtn[i]);
        }
        ImageButton.ImageButtonStyle upgradeBtnStyle = new ImageButton.ImageButtonStyle();
        upgradeBtnStyle.up = upgradeBtnDrawables[0];
        upgradeBtnStyle.down = upgradeBtnDrawables[1];
        upgradeBtnStyle.disabled = upgradeBtnDrawables[2];

        // Integrity Capacity upgrade button
        integrityCapUpgradeBtn = new ImageButton(upgradeBtnStyle);
        integrityCapUpgradeBtn.setPosition(1560, 593);
        integrityCapUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getIntegrityCapCost())) {
                    if (!integrityCapUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upIntegrityCapLvl();
                }
            }
        });

        // Repair upgrade button
        repairUpgradeBtn = new ImageButton(upgradeBtnStyle);
        repairUpgradeBtn.setPosition(1560, integrityCapUpgradeBtn.getY() - 94);
        repairUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getRepairCost())) {
                    if (!repairUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upRepairLvl();
                }
            }
        });

        // Shield Capacity upgrade button
        shieldCapUpgradeBtn = new ImageButton(upgradeBtnStyle);
        shieldCapUpgradeBtn.setPosition(1560, repairUpgradeBtn.getY() - 94);
        shieldCapUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getShieldCapCost())) {
                    if (!shieldCapUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upShieldCapLvl();
                }
            }
        });

        // Recharge upgrade button
        rechargeUpgradeBtn = new ImageButton(upgradeBtnStyle);
        rechargeUpgradeBtn.setPosition(1560, shieldCapUpgradeBtn.getY() - 94);
        rechargeUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getRechargeCost())) {
                    if (!rechargeUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upRechargeLvl();
                }
            }
        });

        // Level bars
        int barWidth = 384;
        int barHeight = 26;

        // Background
        Pixmap pm = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        pm.setColor(Color.DARK_GRAY);
        pm.fill();
        TextureRegionDrawable barDrawable = new TextureRegionDrawable(new TextureRegion(
                new Texture(pm)));
        pm.dispose();

        ProgressBar.ProgressBarStyle progBarStyle = new ProgressBar.ProgressBarStyle();
        progBarStyle.background = barDrawable;

        // Filled pixmap
        pm = new Pixmap(0, barHeight, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GREEN);
        pm.fill();
        barDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        progBarStyle.knob = barDrawable;

        // Empty pixmap
        pm = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GREEN);
        pm.fill();
        barDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        progBarStyle.knobBefore = barDrawable;

        // integrity capacity level Progress Bar
        integrityCapLvlBar = new ProgressBar(0f, MAX_INTEGRITY_CAP_LVL, 1f, false, progBarStyle);
        integrityCapLvlBar.setBounds(1158, 595, (float)barWidth, (float)barHeight);
        integrityCapLvlBar.setAnimateDuration(0.1f);

        // Repair level Progress Bar
        repairLvlBar = new ProgressBar(0f, MAX_REPAIR_LVL, 1f, false, progBarStyle);
        repairLvlBar.setBounds(1158, integrityCapLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        repairLvlBar.setAnimateDuration(0.1f);

        // Shield capacity level Progress Bar
        shieldCapLvlBar = new ProgressBar(0f, MAX_SHIELD_CAP_LVL, 1f, false, progBarStyle);
        shieldCapLvlBar.setBounds(1158, repairLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        shieldCapLvlBar.setAnimateDuration(0.1f);

        // Recharge level Progress Bar
        rechargeLvlBar = new ProgressBar(0f, MAX_RECHARGE_LVL, 1f, false, progBarStyle);
        rechargeLvlBar.setBounds(1158, shieldCapLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        rechargeLvlBar.setAnimateDuration(0.1f);
    }

    public TextureRegion getTexRegNorm() {
        return texRegNorm;
    }

    public TextureRegion getTexRegSel() {
        return texRegSel;
    }

    public void addUpgradePage() {
        Invasion.game.stage.addActor(upgradeBgImg);

        Invasion.game.stage.addActor(integrityCapUpgradeBtn);
        Invasion.game.stage.addActor(repairUpgradeBtn);
        Invasion.game.stage.addActor(shieldCapUpgradeBtn);
        Invasion.game.stage.addActor(rechargeUpgradeBtn);

        Invasion.game.stage.addActor(integrityCapLvlBar);
        Invasion.game.stage.addActor(repairLvlBar);
        Invasion.game.stage.addActor(shieldCapLvlBar);
        Invasion.game.stage.addActor(rechargeLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        integrityCapUpgradeBtn.remove();
        repairUpgradeBtn.remove();
        shieldCapUpgradeBtn.remove();
        rechargeUpgradeBtn.remove();

        integrityCapLvlBar.remove();
        repairLvlBar.remove();
        shieldCapLvlBar.remove();
        rechargeLvlBar.remove();

        if (integrityCapCostDigitImgs != null) {
            for (int i = 0; i < integrityCapCostDigitImgs.length; i++) {
                integrityCapCostDigitImgs[i].remove();
            }
        }

        if (repairCostDigitImgs != null) {
            for (int i = 0; i < repairCostDigitImgs.length; i++) {
                repairCostDigitImgs[i].remove();
            }
        }

        if (shieldCapCostDigitImgs != null) {
            for (int i = 0; i < shieldCapCostDigitImgs.length; i++) {
                shieldCapCostDigitImgs[i].remove();
            }
        }

        if (rechargeCostDigitImgs != null) {
            for (int i = 0; i < rechargeCostDigitImgs.length; i++) {
                rechargeCostDigitImgs[i].remove();
            }
        }
    }

    public void updateUpgradePage() {
        integrityCapLvlBar.setValue(integrityCapLvl);
        repairLvlBar.setValue(repairLvl);
        shieldCapLvlBar.setValue(shieldCapLvl);
        rechargeLvlBar.setValue(rechargeLvl);

        if (integrityCapLvl == MAX_INTEGRITY_CAP_LVL || getIntegrityCapCost() > Invasion.game.getCurrency()) {
            integrityCapUpgradeBtn.setDisabled(true);
        }
        else {
            integrityCapUpgradeBtn.setDisabled(false);
        }

        if (repairLvl == MAX_REPAIR_LVL || getRepairCost() > Invasion.game.getCurrency()) {
            repairUpgradeBtn.setDisabled(true);
        }
        else {
            repairUpgradeBtn.setDisabled(false);
        }

        if (shieldCapLvl == MAX_SHIELD_CAP_LVL || getShieldCapCost() > Invasion.game.getCurrency()) {
            shieldCapUpgradeBtn.setDisabled(true);
        }
        else {
            shieldCapUpgradeBtn.setDisabled(false);
        }

        if (rechargeLvl == MAX_RECHARGE_LVL || getRechargeCost() > Invasion.game.getCurrency()) {
            rechargeUpgradeBtn.setDisabled(true);
        }
        else {
            rechargeUpgradeBtn.setDisabled(false);
        }

        if (integrityCapCostDigitImgs != null) {
            for (int i = 0; i < integrityCapCostDigitImgs.length; i++) {
                integrityCapCostDigitImgs[i].remove();
            }
        }

        integrityCapCostDigitImgs = new Image[("" + getIntegrityCapCost()).length()];
        for (int i = 0; i < integrityCapCostDigitImgs.length; i++){
            int number = (int)(getIntegrityCapCost() / Math.pow(10,
                    (integrityCapCostDigitImgs.length - i - 1)));
            integrityCapCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                integrityCapCostDigitImgs[i].setPosition(1725, 608);
            }
            else {
                integrityCapCostDigitImgs[i].setPosition(
                        integrityCapCostDigitImgs[i - 1].getX() + integrityCapCostDigitImgs[i].getWidth(),
                        integrityCapCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(integrityCapCostDigitImgs[i]);
        }

        if (repairCostDigitImgs != null) {
            for (int i = 0; i < repairCostDigitImgs.length; i++) {
                repairCostDigitImgs[i].remove();
            }
        }

        repairCostDigitImgs = new Image[("" + getRepairCost()).length()];
        for (int i = 0; i < repairCostDigitImgs.length; i++){
            int number = (int)(getRepairCost() / Math.pow(10, (repairCostDigitImgs.length - i - 1)));
            repairCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                repairCostDigitImgs[i].setPosition(integrityCapCostDigitImgs[i].getX(),
                        integrityCapCostDigitImgs[i].getY() - 94);
            }
            else {
                repairCostDigitImgs[i].setPosition(
                        repairCostDigitImgs[i - 1].getX() + repairCostDigitImgs[i].getWidth(),
                        repairCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(repairCostDigitImgs[i]);
        }

        if (shieldCapCostDigitImgs != null) {
            for (int i = 0; i < shieldCapCostDigitImgs.length; i++) {
                shieldCapCostDigitImgs[i].remove();
            }
        }

        shieldCapCostDigitImgs = new Image[("" + getShieldCapCost()).length()];
        for (int i = 0; i < shieldCapCostDigitImgs.length; i++){
            int number = (int)(getShieldCapCost() / Math.pow(10,
                    (shieldCapCostDigitImgs.length - i - 1)));
            shieldCapCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                shieldCapCostDigitImgs[i].setPosition(repairCostDigitImgs[i].getX(),
                        repairCostDigitImgs[i].getY() - 94);
            }
            else {
                shieldCapCostDigitImgs[i].setPosition(
                        shieldCapCostDigitImgs[i - 1].getX() + shieldCapCostDigitImgs[i].getWidth(),
                        shieldCapCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(shieldCapCostDigitImgs[i]);
        }

        if (rechargeCostDigitImgs != null) {
            for (int i = 0; i < rechargeCostDigitImgs.length; i++) {
                rechargeCostDigitImgs[i].remove();
            }
        }

        rechargeCostDigitImgs = new Image[("" + getRechargeCost()).length()];
        for (int i = 0; i < rechargeCostDigitImgs.length; i++){
            int number = (int)(getRechargeCost() / Math.pow(10, (rechargeCostDigitImgs.length - i - 1)));
            rechargeCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                rechargeCostDigitImgs[i].setPosition(shieldCapCostDigitImgs[i].getX(),
                        shieldCapCostDigitImgs[i].getY() - 94);
            }
            else {
                rechargeCostDigitImgs[i].setPosition(
                        rechargeCostDigitImgs[i - 1].getX() + rechargeCostDigitImgs[i].getWidth(),
                        rechargeCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(rechargeCostDigitImgs[i]);
        }
    }

    public float getIntegrity() {
        return integrity;
    }

    public float getShield() {
        return shield;
    }

    public float getIntegrityCap() {
        return integrityCap[integrityCapLvl];
    }

    public float getRepair() {
        return repair[repairLvl];
    }

    public float getShieldCap() {
        return shieldCap[shieldCapLvl];
    }

    public float getRecharge() {
        return recharge[rechargeLvl];
    }

    public void upIntegrityCapLvl() {
        if (integrityCapLvl < MAX_INTEGRITY_CAP_LVL) {
            integrityCapLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upRepairLvl() {
        if (repairLvl < MAX_REPAIR_LVL) {
            repairLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upShieldCapLvl() {
        if (shieldCapLvl < MAX_SHIELD_CAP_LVL) {
            shieldCapLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upRechargeLvl() {
        if (rechargeLvl < MAX_RECHARGE_LVL) {
            rechargeLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public int getIntegrityCapCost() {
        if (integrityCapLvl == MAX_INTEGRITY_CAP_LVL) return 0;
        return integrityCapCost[integrityCapLvl];
    }

    public int getRepairCost() {
        if (repairLvl == MAX_REPAIR_LVL) return 0;
        return repairCost[repairLvl];
    }

    public int getShieldCapCost() {
        if (shieldCapLvl == MAX_SHIELD_CAP_LVL) return 0;
        return shieldCapCost[shieldCapLvl];
    }

    public int getRechargeCost() {
        if (rechargeLvl == MAX_RECHARGE_LVL) return 0;
        return rechargeCost[rechargeLvl];
    }

    public void resetIntegrity() {
        integrity = integrityCap[integrityCapLvl];
    }

    public void resetShield() {
        shield = shieldCap[shieldCapLvl];
    }

    public void addShield(float perc) {
        shield += shieldCap[shieldCapLvl] * perc;
        if (shield > shieldCap[shieldCapLvl]) shield = shieldCap[shieldCapLvl];
    }

    public void takeDamage(float dmg) {
        if (shield > dmg) {
            shield -= dmg;
        }
        else {
            dmg -= shield;
            shield = 0;
            integrity -= dmg;
        }
    }

    public void regen(float deltaTime) {
        integrity += integrityCap[integrityCapLvl] * repair[repairLvl] * deltaTime;
        shield += shieldCap[shieldCapLvl] * recharge[rechargeLvl] * deltaTime;

        if (integrity > integrityCap[integrityCapLvl]) {
            integrity = integrityCap[integrityCapLvl];
        }

        if (shield > shieldCap[shieldCapLvl]) {
            shield = shieldCap[shieldCapLvl];
        }
    }

    public int[] getData() {
        int[] data = new int[4];
        data[0] = integrityCapLvl;
        data[1] = repairLvl;
        data[2] = shieldCapLvl;
        data[3] = rechargeLvl;

        return data;
    }

    public void setData(int[] data) {
        integrityCapLvl = data[0];
        repairLvl = data[1];
        shieldCapLvl = data[2];
        rechargeLvl = data[3];
    }

//    public Json writeData(Json json) {
//        json.writeArrayStart();
//        json.writeValue(integrityCapLvl, int.class);
//        json.writeValue(repairLvl, int.class);
//        json.writeValue(shieldCapLvl, int.class);
//        json.writeValue(rechargeLvl, int.class);
//        json.writeArrayEnd();
//
//        return json;
//    }

//    public void readData(int[] data) {
////        int[] data = json.fromJson(int[].class, Invasion.game.file.readString());
//        integrityCapLvl = data[0];
//        repairLvl = data[1];
//        shieldCapLvl = data[2];
//        rechargeLvl = data[3];
//    }
//
//    public void dispose() {
//        texRegNorm.getTexture().dispose();
//        texRegSel.getTexture().dispose();
//        texRegUpgradeBtn[0].getTexture().dispose();
//        numberTexRegs[0].getTexture().dispose();
//    }
}
