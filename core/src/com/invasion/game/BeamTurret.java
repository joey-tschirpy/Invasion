package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

public class BeamTurret extends Turret {
    private static final BeamTurret instance = new BeamTurret();

    private static final Invasion.DefTurretEnum type = Invasion.DefTurretEnum.BEAM;

    private static TextureRegion texRegNorm;
    private static TextureRegion texRegSel;
    private static TextureRegion[] texRegUpgradeBtn;
    private static TextureRegion[] numberTexRegs;

    private Image upgradeBgImg;

    // Damage
    private ProgressBar dmgLvlBar;
    private ImageButton dmgUpgradeBtn;
    private Image[] dmgCostDigitImgs;

    // Beam Width
    private int beamWidthLvl = 0;
    private final int MAX_BEAM_WIDTH_LVL = 10;
    private float[] beamWidth;
    private int[] beamWidthCost;

    private ProgressBar beamWidthLvlBar;
    private ImageButton beamWidthUpgradeBtn;
    private Image[] beamWidthCostDigitImgs;

    private Sound attackSound;

    static BeamTurret getInstance() {
        return instance;
    }

    private BeamTurret() {
        super(10, 0, 0);

        // Damage
        float[] damage = new float[getMaxDmgLvl() + 1];
        damage[0] = 12f; // base damage
        for (int i = 1; i < damage.length; i++) {
            damage[i] = damage[i - 1] + (i * damage[0]); // damage: 12, 24, 48, 84, 132, 192, 264, 348, 444, 552, 672 (dps)
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
        float[] fireRate = {80f};
        setFireRate(fireRate);

        // Beam Width
        beamWidth = new float[MAX_BEAM_WIDTH_LVL + 1];
        beamWidth[0] = 1f; // base beam width
        for (int i = 1; i < beamWidth.length; i++) {
            beamWidth[i] = beamWidth[i - 1] + 0.2f; // beam width: 1, 1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6, 2.8, 3
        }

        // Beam Width Cost
        beamWidthCost = new int[MAX_BEAM_WIDTH_LVL];
        beamWidthCost[0] = 100; // first upgrade cost
        for (int i = 1; i < beamWidthCost.length; i++) {
            beamWidthCost[i] = beamWidthCost[i - 1] * 2; // beam width cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        int maxDef = Invasion.DefTurretEnum.values().length >= Invasion.DefAbilityEnum.values().length
                ? Invasion.DefTurretEnum.values().length : Invasion.DefAbilityEnum.values().length;

        Texture texBtn = new Texture(Gdx.files.internal("Defences/DefBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, 0, 3 * texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);
        texRegSel = new TextureRegion(texBtn, texBtn.getWidth() / 2, 3 * texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        isRotatable = true;

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_BeamTurret.png")));
        upgradeBgImg.setPosition(1125, 117);

        // 0: norm, 1: down, 2: disabled
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

        // Beam Width upgrade button
        beamWidthUpgradeBtn = new ImageButton(upgradeBtnStyle);
        beamWidthUpgradeBtn.setPosition(1560, dmgUpgradeBtn.getY() - 94);
        beamWidthUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getBeamWidthCost())) {
                    if (!beamWidthUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upBeamWidthLvl();
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

        // Jump level Progress Bar
        beamWidthLvlBar = new ProgressBar(0f, MAX_BEAM_WIDTH_LVL, 1f, false, progBarStyle);
        beamWidthLvlBar.setBounds(1158, dmgLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        beamWidthLvlBar.setAnimateDuration(0.1f);

        attackSound = Gdx.audio.newSound(Gdx.files.internal("Audio/beamAttack.mp3"));
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
        Invasion.game.stage.addActor(beamWidthUpgradeBtn);

        Invasion.game.stage.addActor(dmgLvlBar);
        Invasion.game.stage.addActor(beamWidthLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        dmgUpgradeBtn.remove();
        beamWidthUpgradeBtn.remove();

        dmgLvlBar.remove();
        beamWidthLvlBar.remove();

        if (dmgCostDigitImgs != null) {
            for (int i = 0; i < dmgCostDigitImgs.length; i++) {
                dmgCostDigitImgs[i].remove();
            }
        }

        if (beamWidthCostDigitImgs != null) {
            for (int i = 0; i < beamWidthCostDigitImgs.length; i++) {
                beamWidthCostDigitImgs[i].remove();
            }
        }
    }

    public void updateUpgradePage() {
        dmgLvlBar.setValue(getDamageLvl());
        beamWidthLvlBar.setValue(beamWidthLvl);

        if (getDamageLvl() == getMaxDmgLvl() || getDamageCost() > Invasion.game.getCurrency()) {
            dmgUpgradeBtn.setDisabled(true);
        }
        else {
            dmgUpgradeBtn.setDisabled(false);
        }

        if (beamWidthLvl == MAX_BEAM_WIDTH_LVL || getBeamWidthCost() > Invasion.game.getCurrency()) {
            beamWidthUpgradeBtn.setDisabled(true);
        }
        else {
            beamWidthUpgradeBtn.setDisabled(false);
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

        if (beamWidthCostDigitImgs != null) {
            for (int i = 0; i < beamWidthCostDigitImgs.length; i++) {
                beamWidthCostDigitImgs[i].remove();
            }
        }

        beamWidthCostDigitImgs = new Image[("" + getBeamWidthCost()).length()];
        for (int i = 0; i < beamWidthCostDigitImgs.length; i++){
            int number = (int)(getBeamWidthCost() / Math.pow(10,
                    beamWidthCostDigitImgs.length - i - 1));
            beamWidthCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                beamWidthCostDigitImgs[i].setPosition(dmgCostDigitImgs[i].getX(),
                        dmgCostDigitImgs[i].getY() - 94);
            }
            else {
                beamWidthCostDigitImgs[i].setPosition(
                        beamWidthCostDigitImgs[i - 1].getX() + beamWidthCostDigitImgs[i].getWidth(),
                        beamWidthCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(beamWidthCostDigitImgs[i]);
        }
    }

    public float getBeamWidth() {
        return beamWidth[beamWidthLvl];
    }

    public void upBeamWidthLvl() {
        if (beamWidthLvl < MAX_BEAM_WIDTH_LVL) {
            beamWidthLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public int getBeamWidthCost() {
        if (beamWidthLvl == MAX_BEAM_WIDTH_LVL) return 0;
        return beamWidthCost[beamWidthLvl];
    }

    public void playAttackSound() {
        attackSound.play(0.25f);
    }

    public int[] getData() {
        int[] data = new int[2];
        data[0] = getDamageLvl();
        data[1] = beamWidthLvl;

        return data;
    }

    public void setData(int[] data) {
        setLevels(data[0], 0, 0);
        beamWidthLvl = data[1];
    }

    public void dispose() {
        texRegNorm.getTexture().dispose();
        texRegSel.getTexture().dispose();
        texRegUpgradeBtn[0].getTexture().dispose();
        numberTexRegs[0].getTexture().dispose();

        attackSound.dispose();
    }
}
