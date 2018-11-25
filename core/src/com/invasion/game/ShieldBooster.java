package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ShieldBooster extends Ability {
    private static final ShieldBooster instance = new ShieldBooster();

    private static final Invasion.DefAbilityEnum type = Invasion.DefAbilityEnum.SHIELD_BOOSTER;

    private static TextureRegion texRegNorm;
    private static TextureRegion texRegSel;
    private static TextureRegion[] texRegUpgradeBtn;
    private static TextureRegion[] numberTexRegs;

    private Image upgradeBgImg;

    // Cooldown
    private ProgressBar cooldownLvlBar;
    private ImageButton cooldownUpgradeBtn;
    private Image[] cooldownCostDigitImgs;

    // Capacity
    private ProgressBar capacityLvlBar;
    private ImageButton capacityUpgradeBtn;
    private Image[] capacityCostDigitImgs;

    // Boost
    private int boostLvl = 0;
    private final int MAX_BOOST_LVL = 10;
    private float[] boost;
    private int[] boostCost;

    private ProgressBar boostLvlBar;
    private ImageButton boostUpgradeBtn;
    private Image[] boostCostDigitImgs;

    static ShieldBooster getInstance() {
        return instance;
    }

    private ShieldBooster() {
        super(10, 4);

        // Cooldown
        float[] cooldown = new float[getMaxCooldownLvl() + 1];
        cooldown[0] = 20f; // base cooldown
        for (int i = 1; i < cooldown.length; i++) {
            cooldown[i] = cooldown[0] - (cooldown[0] * 0.05f * i); // cooldown: 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10
        }
        setCooldown(cooldown);
        resetCooldownTimer();

        // Cooldown Cost
        int[] cooldownCost = new int[getMaxCooldownLvl()];
        cooldownCost[0] = 100; // first upgrade cost
        for (int i = 1; i < cooldownCost.length; i++) {
            cooldownCost[i] = cooldownCost[i - 1] * 2; // cooldown cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }
        setCooldownCost(cooldownCost);

        // Capacity
        int[] capacity = new int[getMaxCapacityLvl() + 1];
        capacity[0] = 1; // base capacity
        for (int i = 1; i < capacity.length; i++) {
            capacity[i] = capacity[i - 1] + 1; // capacity: 1, 2, 3, 4, 5
        }
        setCapacity(capacity);
        resetAmount();

        // Capacity Cost
        int[] capacityCost = new int[getMaxCapacityLvl()];
        capacityCost[0] = 400; // first upgrade cost
        for (int i = 1; i < capacityCost.length; i++) {
            capacityCost[i] = capacityCost[i - 1] * 4; // capacity cost: 400, 1600, 6400, 25600
        }
        setCapacityCost(capacityCost);

        // Boost
        boost = new float[MAX_BOOST_LVL + 1];
        boost[0] = 0.5f; // base boost
        for (int i = 1; i < boost.length; i++) {
            boost[i] = boost[i - 1] + 0.05f; // boost: 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1
        }

        // Boost Cost
        boostCost = new int[MAX_BOOST_LVL];
        boostCost[0] = 100; // first upgrade cost
        for (int i = 1; i < boostCost.length; i++) {
            boostCost[i] = boostCost[i - 1] * 2; // boost cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        int maxDef = Invasion.DefTurretEnum.values().length >= Invasion.DefAbilityEnum.values().length
                ? Invasion.DefTurretEnum.values().length : Invasion.DefAbilityEnum.values().length;

        Texture texBtn = new Texture(Gdx.files.internal("Defences/DefBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, texBtn.getWidth() / 4, texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);
        texRegSel = new TextureRegion(texBtn, 3 * texBtn.getWidth() / 4, texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_ShieldBooster.png")));
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

        // Cooldown upgrade button
        cooldownUpgradeBtn = new ImageButton(upgradeBtnStyle);
        cooldownUpgradeBtn.setPosition(1560, 593);
        cooldownUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getCooldownCost())) {
                    if (!cooldownUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upCooldownLvl();
                }
            }
        });

        // Capacity upgrade button
        capacityUpgradeBtn = new ImageButton(upgradeBtnStyle);
        capacityUpgradeBtn.setPosition(1560, cooldownUpgradeBtn.getY() - 94);
        capacityUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getCapacityCost())) {
                    if (!capacityUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upCapacityLvl();
                    resetAmount();
                }
            }
        });

        // Boost upgrade button
        boostUpgradeBtn = new ImageButton(upgradeBtnStyle);
        boostUpgradeBtn.setPosition(1560, capacityUpgradeBtn.getY() - 94);
        boostUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getBoostCost())) {
                    if (!boostUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upBoostLvl();
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

        // Progress Bars
        cooldownLvlBar = new ProgressBar(0f, getMaxCooldownLvl(), 1f, false, progBarStyle);
        cooldownLvlBar.setBounds(1158, 595, (float)barWidth, (float)barHeight);
        cooldownLvlBar.setAnimateDuration(0.1f);

        capacityLvlBar = new ProgressBar(0f, getMaxCapacityLvl(), 1f, false, progBarStyle);
        capacityLvlBar.setBounds(1158, cooldownLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        capacityLvlBar.setAnimateDuration(0.1f);

        // Boost level Progress Bar
        boostLvlBar = new ProgressBar(0f, MAX_BOOST_LVL, 1f, false, progBarStyle);
        boostLvlBar.setBounds(1158, capacityLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        boostLvlBar.setAnimateDuration(0.1f);
    }

    public TextureRegion getTexRegNorm() {
        return texRegNorm;
    }

    public TextureRegion getTexRegSel() {
        return texRegSel;
    }

    public Invasion.DefAbilityEnum getType() {
        return type;
    }

    public void addUpgradePage() {
        Invasion.game.stage.addActor(upgradeBgImg);

        Invasion.game.stage.addActor(cooldownUpgradeBtn);
        Invasion.game.stage.addActor(capacityUpgradeBtn);
        Invasion.game.stage.addActor(boostUpgradeBtn);

        Invasion.game.stage.addActor(cooldownLvlBar);
        Invasion.game.stage.addActor(capacityLvlBar);
        Invasion.game.stage.addActor(boostLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        cooldownUpgradeBtn.remove();
        capacityUpgradeBtn.remove();
        boostUpgradeBtn.remove();

        cooldownLvlBar.remove();
        capacityLvlBar.remove();
        boostLvlBar.remove();

        if (cooldownCostDigitImgs != null) {
            for (int i = 0; i < cooldownCostDigitImgs.length; i++) {
                cooldownCostDigitImgs[i].remove();
            }
        }

        if (capacityCostDigitImgs != null) {
            for (int i = 0; i < capacityCostDigitImgs.length; i++) {
                capacityCostDigitImgs[i].remove();
            }
        }

        if (boostCostDigitImgs != null) {
            for (int i = 0; i < boostCostDigitImgs.length; i++) {
                boostCostDigitImgs[i].remove();
            }
        }
    }

    public void updateUpgradePage() {
        cooldownLvlBar.setValue(getCooldownLvl());
        capacityLvlBar.setValue(getCapacityLvl());
        boostLvlBar.setValue(boostLvl);

        if (getCooldownLvl() == getMaxCooldownLvl() || getCooldownCost() > Invasion.game.getCurrency()) {
            cooldownUpgradeBtn.setDisabled(true);
        }
        else {
            cooldownUpgradeBtn.setDisabled(false);
        }

        if (getCapacityLvl() == getMaxCapacityLvl() || getCapacityCost() > Invasion.game.getCurrency()) {
            capacityUpgradeBtn.setDisabled(true);
        }
        else {
            capacityUpgradeBtn.setDisabled(false);
        }

        if (boostLvl == MAX_BOOST_LVL || getBoostCost() > Invasion.game.getCurrency()) {
            boostUpgradeBtn.setDisabled(true);
        }
        else {
            boostUpgradeBtn.setDisabled(false);
        }

        if (cooldownCostDigitImgs != null) {
            for (int i = 0; i < cooldownCostDigitImgs.length; i++) {
                cooldownCostDigitImgs[i].remove();
            }
        }

        cooldownCostDigitImgs = new Image[("" + getCooldownCost()).length()];
        for (int i = 0; i < cooldownCostDigitImgs.length; i++){
            int number = (int)(getCooldownCost() / Math.pow(10, (cooldownCostDigitImgs.length - i - 1)));
            cooldownCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                cooldownCostDigitImgs[i].setPosition(1725, 608);
            }
            else {
                cooldownCostDigitImgs[i].setPosition(
                        cooldownCostDigitImgs[i - 1].getX() + cooldownCostDigitImgs[i].getWidth(),
                        cooldownCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(cooldownCostDigitImgs[i]);
        }

        if (capacityCostDigitImgs != null) {
            for (int i = 0; i < capacityCostDigitImgs.length; i++) {
                capacityCostDigitImgs[i].remove();
            }
        }

        capacityCostDigitImgs = new Image[("" + getCapacityCost()).length()];
        for (int i = 0; i < capacityCostDigitImgs.length; i++){
            int number = (int)(getCapacityCost() / Math.pow(10, (capacityCostDigitImgs.length - i - 1)));
            capacityCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                capacityCostDigitImgs[i].setPosition(cooldownCostDigitImgs[i].getX(), cooldownCostDigitImgs[i].getY() - 94);
            }
            else {
                capacityCostDigitImgs[i].setPosition(
                        capacityCostDigitImgs[i - 1].getX() + capacityCostDigitImgs[i].getWidth(),
                        capacityCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(capacityCostDigitImgs[i]);
        }

        if (boostCostDigitImgs != null) {
            for (int i = 0; i < boostCostDigitImgs.length; i++) {
                boostCostDigitImgs[i].remove();
            }
        }

        boostCostDigitImgs = new Image[("" + getBoostCost()).length()];
        for (int i = 0; i < boostCostDigitImgs.length; i++){
            int number = (int)(getBoostCost() / Math.pow(10, (boostCostDigitImgs.length - i - 1)));
            boostCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                boostCostDigitImgs[i].setPosition(capacityCostDigitImgs[i].getX(),
                        capacityCostDigitImgs[i].getY() - 94);
            }
            else {
                boostCostDigitImgs[i].setPosition(
                        boostCostDigitImgs[i - 1].getX() + boostCostDigitImgs[i].getWidth(),
                        boostCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(boostCostDigitImgs[i]);
        }
    }

    public float getBoost() {
        return boost[boostLvl];
    }

    public void upBoostLvl() {
        if (boostLvl < MAX_BOOST_LVL) {
            boostLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public int getBoostCost() {
        if (boostLvl == MAX_BOOST_LVL) return 0;
        return boostCost[boostLvl];
    }

    public int[] getData() {
        int[] data = new int[3];
        data[0] = getCooldownLvl();
        data[1] = getCapacityLvl();
        data[2] = boostLvl;

        return data;
    }

    public void setData(int[] data) {
        setLevels(data[0], data[1]);
        boostLvl = data[2];
    }

    public void dispose() {
        texRegNorm.getTexture().dispose();
        texRegSel.getTexture().dispose();
        texRegUpgradeBtn[0].getTexture().dispose();
        numberTexRegs[0].getTexture().dispose();
    }
}
