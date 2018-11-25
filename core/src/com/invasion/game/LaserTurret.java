package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class LaserTurret extends Turret {
    private static final LaserTurret instance = new LaserTurret();

    private static final Invasion.DefTurretEnum type = Invasion.DefTurretEnum.LASER;

    private static TextureRegion texRegNorm;
    private static TextureRegion texRegSel;
    private static TextureRegion[] texRegUpgradeBtn;
    private static TextureRegion[] numberTexRegs;

    private float knockback;

    private Image upgradeBgImg;

    // Damage
    private ProgressBar dmgLvlBar;
    private ImageButton dmgUpgradeBtn;
    private Image[] dmgCostDigitImgs;

    // FireRate
    private ProgressBar fireRateLvlBar;
    private ImageButton fireRateUpgradeBtn;
    private Image[] fireRateCostDigitImgs;

    static LaserTurret getInstance() {
        return instance;
    }

    private LaserTurret() {
        super(10, 10, 0);

        // Damage
        float[] damage = new float[getMaxDmgLvl() + 1];
        damage[0] = 4f; // base damage
        for (int i = 1; i < damage.length; i++) {
            damage[i] = damage[i - 1] + (i * damage[0]); // damage: 4, 8, 16, 28, 44, 64, 88, 116, 148, 184, 224
        }
        setDamage(damage);

        // Damage Cost
        int[] dmgCost = new int[getMaxDmgLvl()];
        dmgCost[0] = 100; // first upgrade cost
        for (int i = 1; i < dmgCost.length; i++) {
            dmgCost[i] = dmgCost[i - 1] * 2; // dmg cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }
        setDamageCost(dmgCost);

        // FireRate
        float[] fireRate = new float[getMaxFireRateLvl() + 1];
        fireRate[0] = 2f; // base fireRate
        for (int i = 1; i < fireRate.length; i++) {
            fireRate[i] = fireRate[i - 1] + 0.3f; // fireRate: 2.0, 2.3, 2.6, 2.9, 3.2, 3.5, 3.8, 4.1, 4.4, 4.7, 5.0
        }
        setFireRate(fireRate);

        // FireRate Cost
        int[] fireRateCost = new int[getMaxFireRateLvl()];
        fireRateCost[0] = 100; // first upgrade cost
        for (int i = 1; i < fireRateCost.length; i++) {
            fireRateCost[i] = fireRateCost[i - 1] * 2; // dmg cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }
        setFireRateCost(fireRateCost);

        // Bullet Speed
        float[] bulletSpeed = {2000f};
        setBulletSpeed(bulletSpeed);

        int maxDef = Invasion.DefTurretEnum.values().length >= Invasion.DefAbilityEnum.values().length
                ? Invasion.DefTurretEnum.values().length : Invasion.DefAbilityEnum.values().length;

        Texture texBtn = new Texture(Gdx.files.internal("Defences/DefBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, 0, 0,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);
        texRegSel = new TextureRegion(texBtn, texBtn.getWidth() / 2, 0,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        knockback = 6f;
        isRotatable = true;

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_LaserTurret.png")));
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

        // damage upgrade button
        dmgUpgradeBtn = new ImageButton(upgradeBtnStyle);
        dmgUpgradeBtn.setPosition(1560, 593);
        dmgUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getDamageCost())) {
                    if (!dmgUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upDamageLvl();
                }
            }
        });

        // fireRate upgrade button
        fireRateUpgradeBtn = new ImageButton(upgradeBtnStyle);
        fireRateUpgradeBtn.setPosition(1560, dmgUpgradeBtn.getY() - 94);
        fireRateUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getFireRateCost())) {
                    if (!fireRateUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upFireRateLvl();
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
        dmgLvlBar = new ProgressBar(0f, getMaxDmgLvl(), 1f, false, progBarStyle);
        dmgLvlBar.setBounds(1158, 595, (float)barWidth, (float)barHeight);
        dmgLvlBar.setAnimateDuration(0.1f);

        fireRateLvlBar = new ProgressBar(0f, getMaxFireRateLvl(), 1f, false, progBarStyle);
        fireRateLvlBar.setBounds(1158, dmgLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        fireRateLvlBar.setAnimateDuration(0.1f);
    }

    public TextureRegion getTexRegNorm() {
        return texRegNorm;
    }

    public TextureRegion getTexRegSel() {
        return texRegSel;
    }

    public Invasion.DefTurretEnum getType() {
        return type;
    }

    public void addUpgradePage() {
        Invasion.game.stage.addActor(upgradeBgImg);

        Invasion.game.stage.addActor(dmgUpgradeBtn);
        Invasion.game.stage.addActor(fireRateUpgradeBtn);

        Invasion.game.stage.addActor(dmgLvlBar);
        Invasion.game.stage.addActor(fireRateLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        dmgUpgradeBtn.remove();
        fireRateUpgradeBtn.remove();

        dmgLvlBar.remove();
        fireRateLvlBar.remove();

        if (dmgCostDigitImgs != null) {
            for (int i = 0; i < dmgCostDigitImgs.length; i++) {
                dmgCostDigitImgs[i].remove();
            }
        }

        if (fireRateCostDigitImgs != null) {
            for (int i = 0; i < fireRateCostDigitImgs.length; i++) {
                fireRateCostDigitImgs[i].remove();
            }
        }
    }

    public void updateUpgradePage() {
        dmgLvlBar.setValue(getDamageLvl());
        fireRateLvlBar.setValue(getFireRateLvl());

        if (getDamageLvl() == getMaxDmgLvl() || getDamageCost() > Invasion.game.getCurrency()) {
            dmgUpgradeBtn.setDisabled(true);
        }
        else {
            dmgUpgradeBtn.setDisabled(false);
        }

        if (getFireRateLvl() == getMaxFireRateLvl() || getFireRateCost() > Invasion.game.getCurrency()) {
            fireRateUpgradeBtn.setDisabled(true);
        }
        else {
            fireRateUpgradeBtn.setDisabled(false);
        }

        if (dmgCostDigitImgs != null) {
            for (int i = 0; i < dmgCostDigitImgs.length; i++) {
                dmgCostDigitImgs[i].remove();
            }
        }

        dmgCostDigitImgs = new Image[("" + getDamageCost()).length()];
        for (int i = 0; i < dmgCostDigitImgs.length; i++){
            int number = (int)(getDamageCost() / Math.pow(10, (dmgCostDigitImgs.length - i - 1)));
            dmgCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                dmgCostDigitImgs[i].setPosition(1725, 608);
            }
            else {
                dmgCostDigitImgs[i].setPosition(
                        dmgCostDigitImgs[i - 1].getX() + dmgCostDigitImgs[i].getWidth(),
                        dmgCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(dmgCostDigitImgs[i]);
        }

        if (fireRateCostDigitImgs != null) {
            for (int i = 0; i < fireRateCostDigitImgs.length; i++) {
                fireRateCostDigitImgs[i].remove();
            }
        }

        fireRateCostDigitImgs = new Image[("" + getFireRateCost()).length()];
        for (int i = 0; i < fireRateCostDigitImgs.length; i++){
            int number = (int)(getFireRateCost() / Math.pow(10, (fireRateCostDigitImgs.length - i - 1)));
            fireRateCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                fireRateCostDigitImgs[i].setPosition(dmgCostDigitImgs[i].getX(), dmgCostDigitImgs[i].getY() - 94);
            }
            else {
                fireRateCostDigitImgs[i].setPosition(
                        fireRateCostDigitImgs[i - 1].getX() + fireRateCostDigitImgs[i].getWidth(),
                        fireRateCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(fireRateCostDigitImgs[i]);
        }
    }

    public float getKnockback() {
        return knockback;
    }

    public void playAttackSound() {
    }

    public int[] getData() {
        int[] data = new int[2];
        data[0] = getDamageLvl();
        data[1] = getFireRateLvl();

        return data;
    }

    public void setData(int[] data) {
        setLevels(data[0], data[1], 0);
    }

    public void dispose() {
        texRegNorm.getTexture().dispose();
        texRegSel.getTexture().dispose();
        texRegUpgradeBtn[0].getTexture().dispose();
        numberTexRegs[0].getTexture().dispose();
    }
}
