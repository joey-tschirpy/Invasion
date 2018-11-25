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

public class MegaBomb extends Ability {
    private static final MegaBomb instance = new MegaBomb();

    private static final Invasion.DefAbilityEnum type = Invasion.DefAbilityEnum.MEGA_BOMB;

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

    static MegaBomb getInstance() {
        return instance;
    }

    private MegaBomb() {
        super(10, 2);

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
            capacity[i] = capacity[i - 1] + 1; // capacity: 1, 2, 3
        }
        setCapacity(capacity);
        resetAmount();

        // Capacity Cost
        int[] capacityCost = new int[getMaxCapacityLvl()];
        capacityCost[0] = 6400; // first upgrade cost
        for (int i = 1; i < capacityCost.length; i++) {
            capacityCost[i] = capacityCost[i - 1] * 4; // capacity cost: 6400, 25600
        }
        setCapacityCost(capacityCost);

        int maxDef = Invasion.DefTurretEnum.values().length >= Invasion.DefAbilityEnum.values().length
                ? Invasion.DefTurretEnum.values().length : Invasion.DefAbilityEnum.values().length;

        Texture texBtn = new Texture(Gdx.files.internal("Defences/DefBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, texBtn.getWidth() / 4, 0,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);
        texRegSel = new TextureRegion(texBtn, 3 * texBtn.getWidth() / 4, 0,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_MegaBomb.png")));
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

        Invasion.game.stage.addActor(cooldownLvlBar);
        Invasion.game.stage.addActor(capacityLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        cooldownUpgradeBtn.remove();
        capacityUpgradeBtn.remove();

        cooldownLvlBar.remove();
        capacityLvlBar.remove();

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
    }

    public void updateUpgradePage() {
        cooldownLvlBar.setValue(getCooldownLvl());
        capacityLvlBar.setValue(getCapacityLvl());

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
    }

    public int[] getData() {
        int[] data = new int[2];
        data[0] = getCooldownLvl();
        data[1] = getCapacityLvl();

        return data;
    }

    public void setData(int[] data) {
        setLevels(data[0], data[1]);
    }

    public void dispose() {
        texRegNorm.getTexture().dispose();
        texRegSel.getTexture().dispose();
        texRegUpgradeBtn[0].getTexture().dispose();
        numberTexRegs[0].getTexture().dispose();
    }
}
