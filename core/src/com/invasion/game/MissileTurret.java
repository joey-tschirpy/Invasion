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

public class MissileTurret extends Turret {
    private static final MissileTurret instance = new MissileTurret();

    private static final Invasion.DefTurretEnum type = Invasion.DefTurretEnum.MISSILE;

    private static TextureRegion texRegNorm;
    private static TextureRegion texRegSel;
    private static TextureRegion[] texRegUpgradeBtn;
    private static TextureRegion[] numberTexRegs;

    private Image upgradeBgImg;

    // Damage
    private ProgressBar dmgLvlBar;
    private ImageButton dmgUpgradeBtn;
    private Image[] dmgCostDigitImgs;

    // FireRate
    private ProgressBar fireRateLvlBar;
    private ImageButton fireRateUpgradeBtn;
    private Image[] fireRateCostDigitImgs;

    // BulletSpeed
    private ProgressBar bulletSpeedLvlBar;
    private ImageButton bulletSpeedUpgradeBtn;
    private Image[] bulletSpeedCostDigitImgs;

    static MissileTurret getInstance() {
        return instance;
    }

    private MissileTurret() {
        super(10, 10, 10);

        // Damage
        float[] damage = new float[getMaxDmgLvl() + 1];
        damage[0] = 10f; // base damage
        for (int i = 1; i < damage.length; i++) {
            damage[i] = damage[i - 1] + (i * damage[0]); // damage: 10, 20, 40, 70, 110, 160, 220, 290, 370, 460, 560
        }
        setDamage(damage);

        // Damage Cost
        int[] dmgCost = new int[getMaxDmgLvl()];
        dmgCost[0] = 100; // first upgrade cost
        for (int i = 1; i < dmgCost.length; i++) {
            dmgCost[i] = dmgCost[i - 1] * 2; // damage cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }
        setDamageCost(dmgCost);

        // FireRate
        float[] fireRate = new float[getMaxFireRateLvl() + 1];
        fireRate[0] = 1.5f; // base fireRate
        for (int i = 1; i < fireRate.length; i++) {
            fireRate[i] = fireRate[i - 1] + 0.1f; // fireRate: 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5
        }
        setFireRate(fireRate);

        // FireRate Cost
        int[] fireRateCost = new int[getMaxFireRateLvl()];
        fireRateCost[0] = 100; // first upgrade cost
        for (int i = 1; i < fireRateCost.length; i++) {
            fireRateCost[i] = fireRateCost[i - 1] * 2; // fireRate cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }
        setFireRateCost(fireRateCost);

        // Bullet Speed
        float[] bulletSpeed = new float[getMaxFireRateLvl() + 1];
        bulletSpeed[0] = 1000f; // base fireRate
        for (int i = 1; i < fireRate.length; i++) {
            bulletSpeed[i] = bulletSpeed[i - 1] + 50f; // Bullet speed: 1000, 1050, 1100, 1150, 1200, 1250, 1300, 1350, 1400, 1450, 1500
        }
        setBulletSpeed(bulletSpeed);

        // Bullet Speed Cost
        int[] bulletSpeedCost = new int[getMaxFireRateLvl()];
        bulletSpeedCost[0] = 100; // first upgrade cost
        for (int i = 1; i < fireRateCost.length; i++) {
            bulletSpeedCost[i] = bulletSpeedCost[i - 1] * 2; // Bullet speed cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }
        setBulletSpeedCost(bulletSpeedCost);

        int maxDef = Invasion.DefTurretEnum.values().length >= Invasion.DefAbilityEnum.values().length
                ? Invasion.DefTurretEnum.values().length : Invasion.DefAbilityEnum.values().length;

        Texture texBtn = new Texture(Gdx.files.internal("Defences/DefBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, 0, texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);
        texRegSel = new TextureRegion(texBtn, texBtn.getWidth() / 2, texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        isRotatable = true;

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_MissileTurret.png")));
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
                    if (dmgUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

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

        // bulletSpeed upgrade button
        bulletSpeedUpgradeBtn = new ImageButton(upgradeBtnStyle);
        bulletSpeedUpgradeBtn.setPosition(1560, fireRateUpgradeBtn.getY() - 94);
        bulletSpeedUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getBulletSpeedCost())) {
                    if (!bulletSpeedUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upBulletSpeedLvl();
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

        // Damage level Progress Bar
        dmgLvlBar = new ProgressBar(0f, getMaxDmgLvl(), 1f, false, progBarStyle);
        dmgLvlBar.setBounds(1158, 595, (float)barWidth, (float)barHeight);
        dmgLvlBar.setAnimateDuration(0.1f);

        // FireRate level Progress Bar
        fireRateLvlBar = new ProgressBar(0f, getMaxFireRateLvl(), 1f, false, progBarStyle);
        fireRateLvlBar.setBounds(1158, dmgLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        fireRateLvlBar.setAnimateDuration(0.1f);

        // BulletSpeed level Progress Bar
        bulletSpeedLvlBar = new ProgressBar(0f, getMaxBulletSpeedLvl(), 1f, false, progBarStyle);
        bulletSpeedLvlBar.setBounds(1158, fireRateLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        bulletSpeedLvlBar.setAnimateDuration(0.1f);
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
        Invasion.game.stage.addActor(bulletSpeedUpgradeBtn);

        Invasion.game.stage.addActor(dmgLvlBar);
        Invasion.game.stage.addActor(fireRateLvlBar);
        Invasion.game.stage.addActor(bulletSpeedLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        dmgUpgradeBtn.remove();
        fireRateUpgradeBtn.remove();
        bulletSpeedUpgradeBtn.remove();

        dmgLvlBar.remove();
        fireRateLvlBar.remove();
        bulletSpeedLvlBar.remove();

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

        if (bulletSpeedCostDigitImgs != null) {
            for (int i = 0; i < bulletSpeedCostDigitImgs.length; i++) {
                bulletSpeedCostDigitImgs[i].remove();
            }
        }
    }

    public void updateUpgradePage() {
        dmgLvlBar.setValue(getDamageLvl());
        fireRateLvlBar.setValue(getFireRateLvl());
        bulletSpeedLvlBar.setValue(getBulletSpeedLvl());

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

        if (getBulletSpeedLvl() == getMaxBulletSpeedLvl() || getBulletSpeedCost() > Invasion.game.getCurrency()) {
            bulletSpeedUpgradeBtn.setDisabled(true);
        }
        else {
            bulletSpeedUpgradeBtn.setDisabled(false);
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
                fireRateCostDigitImgs[i].setPosition(dmgCostDigitImgs[i].getX(),
                        dmgCostDigitImgs[i].getY() - 94);
            }
            else {
                fireRateCostDigitImgs[i].setPosition(
                        fireRateCostDigitImgs[i - 1].getX() + fireRateCostDigitImgs[i].getWidth(),
                        fireRateCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(fireRateCostDigitImgs[i]);
        }

        if (bulletSpeedCostDigitImgs != null) {
            for (int i = 0; i < bulletSpeedCostDigitImgs.length; i++) {
                bulletSpeedCostDigitImgs[i].remove();
            }
        }

        bulletSpeedCostDigitImgs = new Image[("" + getBulletSpeedCost()).length()];
        for (int i = 0; i < bulletSpeedCostDigitImgs.length; i++){
            int number = (int)(getBulletSpeedCost() / Math.pow(10, (bulletSpeedCostDigitImgs.length - i - 1)));
            bulletSpeedCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                bulletSpeedCostDigitImgs[i].setPosition(fireRateCostDigitImgs[i].getX(),
                        fireRateCostDigitImgs[i].getY() - 94);
            }
            else {
                bulletSpeedCostDigitImgs[i].setPosition(
                        bulletSpeedCostDigitImgs[i - 1].getX() + bulletSpeedCostDigitImgs[i].getWidth(),
                        bulletSpeedCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(bulletSpeedCostDigitImgs[i]);
        }
    }

    public void playAttackSound() {

    }

    public int[] getData() {
        int[] data = new int[3];
        data[0] = getDamageLvl();
        data[1] = getFireRateLvl();
        data[2] = getBulletSpeedLvl();

        return data;
    }

    public void setData(int[] data) {
        setLevels(data[0], data[1], data[2]);
    }

    public void dispose() {
        texRegNorm.getTexture().dispose();
        texRegSel.getTexture().dispose();
        texRegUpgradeBtn[0].getTexture().dispose();
        numberTexRegs[0].getTexture().dispose();
    }
}
