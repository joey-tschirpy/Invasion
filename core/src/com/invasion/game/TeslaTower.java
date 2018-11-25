package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TeslaTower extends Turret {
    private static final TeslaTower instance = new TeslaTower();

    private static final Invasion.DefTurretEnum type = Invasion.DefTurretEnum.TESLA;

    private static TextureRegion texRegNorm;
    private static TextureRegion texRegSel;
    private static TextureRegion[] texRegUpgradeBtn;
    private static TextureRegion[] numberTexRegs;

    private Image upgradeBgImg;

    // Damage
    private ProgressBar dmgLvlBar;
    private ImageButton dmgUpgradeBtn;
    private Image[] dmgCostDigitImgs;

    // Jump
    private int jumpLvl = 0;
    private final int MAX_JUMP_LVL = 6;
    private int[] jump;
    private int[] jumpCost;

    private ProgressBar jumpLvlBar;
    private ImageButton jumpUpgradeBtn;
    private Image[] jumpCostDigitImgs;

    // Jump Distance
    private int jumpDistLvl = 0;
    private final int MAX_JUMP_DIST_LVL = 10;
    private float[] jumpDist;
    private int[] jumpDistCost;

    private ProgressBar jumpDistLvlBar;
    private ImageButton jumpDistUpgradeBtn;
    private Image[] jumpDistCostDigitImgs;

    // Damage Drop
    private int damageDropLvl = 0;
    private final int MAX_DAMAGE_DROP_LVL = 10;
    private float[] damageDrop;
    private int[] damageDropCost;

    private ProgressBar damageDropLvlBar;
    private ImageButton damageDropUpgradeBtn;
    private Image[] damageDropCostDigitImgs;

    // Lightning effect variables
    private static float thickness = 4f;
    private static int numberOfBolts = 4;
    private static Color boltColor = new Color(0f/255f, 228f/255f, 228f/255f, 1);
    private static Texture boltTex;

    private Sound attackSound;

    static TeslaTower getInstance() {
        return instance;
    }

    private TeslaTower() {
        super(10, 0, 0);

        // Damage
        float[] damage = new float[getMaxDmgLvl() + 1];
        damage[0] = 0.75f; // base damage
        for (int i = 1; i < damage.length; i++) {
            damage[i] = damage[i - 1] + (i * damage[0]); // damage: 0.75, 1.5, 3, 5.25, 8.25, 12, 16.5, 21.75, 27.75, 34.5, 42
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
        float[] fireRate = {40f};
        setFireRate(fireRate);

        // Jump
        jump = new int[MAX_JUMP_LVL + 1];
        jump[0] = 2; // base jumps
        for (int i = 1; i < jump.length; i++) {
            jump[i] = jump[i - 1] + 1; // jumps: 2, 3, 4, 5, 6, 7, 8
        }

        // Jump Cost
        jumpCost = new int[MAX_JUMP_LVL];
        jumpCost[0] = 200; // first upgrade cost
        for (int i = 1; i < jumpCost.length; i++) {
            jumpCost[i] = jumpCost[i - 1] * 3; // damage cost: 200, 600, 1800, 5400, 16200, 48600
        }

        // Jump Distance
        jumpDist = new float[MAX_JUMP_DIST_LVL + 1];
        jumpDist[0] = 200; // base jumps
        for (int i = 1; i < jumpDist.length; i++) {
            jumpDist[i] = jumpDist[i - 1] + 20; // jumps: 200, 220, 240, 260, 280, 300, 320, 340, 360, 380, 400
        }

        // Jump Distance Cost
        jumpDistCost = new int[MAX_JUMP_DIST_LVL];
        jumpDistCost[0] = 100; // first upgrade cost
        for (int i = 1; i < jumpDistCost.length; i++) {
            jumpDistCost[i] = jumpDistCost[i - 1] * 2; // damage cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        // Damage Drop
        damageDrop = new float[MAX_DAMAGE_DROP_LVL + 1];
        damageDrop[0] = 0.5f; // base jumps
        for (int i = 1; i < damageDrop.length; i++) {
            damageDrop[i] = damageDrop[i - 1] - 0.05f; // damage drop: 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0
        }

        // Damage Drop Cost
        damageDropCost = new int[MAX_DAMAGE_DROP_LVL];
        damageDropCost[0] = 100; // first upgrade cost
        for (int i = 1; i < damageDropCost.length; i++) {
            damageDropCost[i] = damageDropCost[i - 1] * 2; // damage cost: 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200
        }

        boltTex = new Texture(Gdx.files.internal("Defences/LightningPointWhite.png"));

        int maxDef = Invasion.DefTurretEnum.values().length >= Invasion.DefAbilityEnum.values().length
                ? Invasion.DefTurretEnum.values().length : Invasion.DefAbilityEnum.values().length;

        Texture texBtn = new Texture(Gdx.files.internal("Defences/DefBtnImgSht.png"));
        texRegNorm = new TextureRegion(texBtn, 0, 2 * texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);
        texRegSel = new TextureRegion(texBtn, texBtn.getWidth() / 2, 2 * texBtn.getHeight() / maxDef,
                texBtn.getWidth() / 4, texBtn.getHeight() / maxDef);

        numberTexRegs = new TextureRegion[10];
        Texture numbersTex = new Texture(Gdx.files.internal("NumbersTxtSht_48.png"));
        for (int i = 0; i < numberTexRegs.length; i++) {
            numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                    numbersTex.getWidth() / 10, numbersTex.getHeight());
        }

        isRotatable = false;

        upgradeBgImg = new Image(new Texture(Gdx.files.internal("Defences/InfoBgImg_TeslaTower.png")));
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

        // Jump upgrade button
        jumpUpgradeBtn = new ImageButton(upgradeBtnStyle);
        jumpUpgradeBtn.setPosition(1560, dmgUpgradeBtn.getY() - 94);
        jumpUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getJumpCost())) {
                    if (!jumpUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upJumpLvl();
                }
            }
        });

        // Jump Distance upgrade button
        jumpDistUpgradeBtn = new ImageButton(upgradeBtnStyle);
        jumpDistUpgradeBtn.setPosition(1560, jumpUpgradeBtn.getY() - 94);
        jumpDistUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getJumpDistCost())) {
                    if (!jumpDistUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upJumpDistLvl();
                }
            }
        });

        // Jump Distance upgrade button
        damageDropUpgradeBtn = new ImageButton(upgradeBtnStyle);
        damageDropUpgradeBtn.setPosition(1560, jumpDistUpgradeBtn.getY() - 94);
        damageDropUpgradeBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Invasion.game.useCurrency(getDamageDropCost())) {
                    if (!damageDropUpgradeBtn.isDisabled()) Invasion.game.playHitBtnsound();

                    upDamageDropLvl();
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
        jumpLvlBar = new ProgressBar(0f, MAX_JUMP_LVL, 1f, false, progBarStyle);
        jumpLvlBar.setBounds(1158, dmgLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        jumpLvlBar.setAnimateDuration(0.1f);

        // Jump Distance level Progress Bar
        jumpDistLvlBar = new ProgressBar(0f, MAX_JUMP_DIST_LVL, 1f, false, progBarStyle);
        jumpDistLvlBar.setBounds(1158, jumpLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        jumpDistLvlBar.setAnimateDuration(0.1f);

        // Jump Distance level Progress Bar
        damageDropLvlBar = new ProgressBar(0f, MAX_DAMAGE_DROP_LVL, 1f, false, progBarStyle);
        damageDropLvlBar.setBounds(1158, jumpDistLvlBar.getY() - 94, (float)barWidth, (float)barHeight);
        damageDropLvlBar.setAnimateDuration(0.1f);

        attackSound = Gdx.audio.newSound(Gdx.files.internal("Audio/TeslaAttack.mp3"));
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
        Invasion.game.stage.addActor(jumpUpgradeBtn);
        Invasion.game.stage.addActor(jumpDistUpgradeBtn);
        Invasion.game.stage.addActor(damageDropUpgradeBtn);

        Invasion.game.stage.addActor(dmgLvlBar);
        Invasion.game.stage.addActor(jumpLvlBar);
        Invasion.game.stage.addActor(jumpDistLvlBar);
        Invasion.game.stage.addActor(damageDropLvlBar);

        updateUpgradePage();
    }

    public void removeUpgradePage() {
        upgradeBgImg.remove();

        dmgUpgradeBtn.remove();
        jumpUpgradeBtn.remove();
        jumpDistUpgradeBtn.remove();
        damageDropUpgradeBtn.remove();

        dmgLvlBar.remove();
        jumpLvlBar.remove();
        jumpDistLvlBar.remove();
        damageDropLvlBar.remove();

        if (dmgCostDigitImgs != null) {
            for (int i = 0; i < dmgCostDigitImgs.length; i++) {
                dmgCostDigitImgs[i].remove();
            }
        }

        if (jumpCostDigitImgs != null) {
            for (int i = 0; i < jumpCostDigitImgs.length; i++) {
                jumpCostDigitImgs[i].remove();
            }
        }

        if (jumpDistCostDigitImgs != null) {
            for (int i = 0; i < jumpDistCostDigitImgs.length; i++) {
                jumpDistCostDigitImgs[i].remove();
            }
        }

        if (damageDropCostDigitImgs != null) {
            for (int i = 0; i < damageDropCostDigitImgs.length; i++) {
                damageDropCostDigitImgs[i].remove();
            }
        }
    }

    public void updateUpgradePage() {
        dmgLvlBar.setValue(getDamageLvl());
        jumpLvlBar.setValue(jumpLvl);
        jumpDistLvlBar.setValue(jumpDistLvl);
        damageDropLvlBar.setValue(damageDropLvl);

        if (getDamageLvl() == getMaxDmgLvl() || getDamageCost() > Invasion.game.getCurrency()) {
            dmgUpgradeBtn.setDisabled(true);
        }
        else {
            dmgUpgradeBtn.setDisabled(false);
        }

        if (jumpLvl == MAX_JUMP_LVL || getJumpCost() > Invasion.game.getCurrency()) {
            jumpUpgradeBtn.setDisabled(true);
        }
        else {
            jumpUpgradeBtn.setDisabled(false);
        }

        if (jumpDistLvl == MAX_JUMP_DIST_LVL || getJumpDistCost() > Invasion.game.getCurrency()) {
            jumpDistUpgradeBtn.setDisabled(true);
        }
        else {
            jumpDistUpgradeBtn.setDisabled(false);
        }

        if (damageDropLvl == MAX_DAMAGE_DROP_LVL || getDamageDropCost() > Invasion.game.getCurrency()) {
            damageDropUpgradeBtn.setDisabled(true);
        }
        else {
            damageDropUpgradeBtn.setDisabled(false);
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

        if (jumpCostDigitImgs != null) {
            for (int i = 0; i < jumpCostDigitImgs.length; i++) {
                jumpCostDigitImgs[i].remove();
            }
        }

        jumpCostDigitImgs = new Image[("" + getJumpCost()).length()];
        for (int i = 0; i < jumpCostDigitImgs.length; i++){
            int number = (int)(getJumpCost() / Math.pow(10, (jumpCostDigitImgs.length - i - 1)));
            jumpCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                jumpCostDigitImgs[i].setPosition(dmgCostDigitImgs[i].getX(),
                        dmgCostDigitImgs[i].getY() - 94);
            }
            else {
                jumpCostDigitImgs[i].setPosition(
                        jumpCostDigitImgs[i - 1].getX() + jumpCostDigitImgs[i].getWidth(),
                        jumpCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(jumpCostDigitImgs[i]);
        }

        if (jumpDistCostDigitImgs != null) {
            for (int i = 0; i < jumpDistCostDigitImgs.length; i++) {
                jumpDistCostDigitImgs[i].remove();
            }
        }

        jumpDistCostDigitImgs = new Image[("" + getJumpDistCost()).length()];
        for (int i = 0; i < jumpDistCostDigitImgs.length; i++){
            int number = (int)(getJumpDistCost() / Math.pow(10, (jumpDistCostDigitImgs.length - i - 1)));
            jumpDistCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                jumpDistCostDigitImgs[i].setPosition(jumpCostDigitImgs[i].getX(),
                        jumpCostDigitImgs[i].getY() - 94);
            }
            else {
                jumpDistCostDigitImgs[i].setPosition(
                        jumpDistCostDigitImgs[i - 1].getX() + jumpDistCostDigitImgs[i].getWidth(),
                        jumpDistCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(jumpDistCostDigitImgs[i]);
        }

        if (damageDropCostDigitImgs != null) {
            for (int i = 0; i < damageDropCostDigitImgs.length; i++) {
                damageDropCostDigitImgs[i].remove();
            }
        }

        damageDropCostDigitImgs = new Image[("" + getDamageDropCost()).length()];
        for (int i = 0; i < damageDropCostDigitImgs.length; i++){
            int number = (int)(getDamageDropCost() / Math.pow(10,
                    (damageDropCostDigitImgs.length - i - 1)));
            damageDropCostDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                damageDropCostDigitImgs[i].setPosition(jumpDistCostDigitImgs[i].getX(),
                        jumpDistCostDigitImgs[i].getY() - 94);
            }
            else {
                damageDropCostDigitImgs[i].setPosition(
                        damageDropCostDigitImgs[i - 1].getX() + damageDropCostDigitImgs[i].getWidth(),
                        damageDropCostDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(damageDropCostDigitImgs[i]);
        }
    }

    public int getJump() {
        return jump[jumpLvl];
    }

    public float getJumpDist() {
        return jumpDist[jumpDistLvl];
    }

    public float getDamageDrop() {
        return damageDrop[damageDropLvl];
    }

    public void upJumpLvl() {
        if (jumpLvl < MAX_JUMP_LVL) {
            jumpLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upJumpDistLvl() {
        if (jumpDistLvl < MAX_JUMP_DIST_LVL) {
            jumpDistLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public void upDamageDropLvl() {
        if (damageDropLvl < MAX_DAMAGE_DROP_LVL) {
            damageDropLvl += 1;

            Invasion.game.playUpgradeSound();
        }
    }

    public static void drawChainLightning(Batch batch, Vector2 endPoint) {
        drawP2PLightning(batch, instance.getCenter(), endPoint,
                MathUtils.random(60f, 140f), MathUtils.random(0.8f, 3.8f));
    }

    public static void drawChainLightning(Batch batch, Vector2 startPoint, Vector2 endPoint) {
        drawP2PLightning(batch, startPoint, endPoint,
                MathUtils.random(60f, 140f), MathUtils.random(0.8f, 3.8f));
    }

    private static void drawP2PLightning(Batch batch, Vector2 pOne, Vector2 pTwo,
                                        float displace, float detail) {
//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < numberOfBolts; i++) {
            batch.setColor(boltColor);
            drawSingleP2PLightning(batch, pOne, pTwo, displace, detail);
        }
//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void drawSingleP2PLightning(Batch batch, Vector2 pOne, Vector2 pTwo,
                                              float displace, float detail) {
        if (displace < detail) {
            float length = pOne.dst(pTwo);
            float dx = pOne.x - pTwo.x;
            float dy = pOne.y - pTwo.y;
            float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx) - 180;

            for (int i = 0; i < 1; i++) {
                batch.draw(boltTex, pOne.x, pOne.y, 0f, thickness * 0.5f, length, thickness, 1f, 1f,
                        angle, 0, 0, boltTex.getWidth(), boltTex.getHeight(), false, false);
            }
        }
        else {
            Vector2 mid = new Vector2((pOne.x + pTwo.x) * 0.5f + (float)(Math.random() - 0.5f) * displace,
                    (pOne.y + pTwo.y) * 0.5f + (float)(Math.random() - 0.5f) * displace);

            drawSingleP2PLightning(batch, pOne, mid, displace * 0.5f, detail);
            drawSingleP2PLightning(batch, pTwo, mid, displace * 0.5f, detail);
        }
    }

    public int getJumpCost() {
        if (jumpLvl == MAX_JUMP_LVL) return 0;
        return jumpCost[jumpLvl];
    }

    public int getJumpDistCost() {
        if (jumpDistLvl == MAX_JUMP_DIST_LVL) return 0;
        return jumpDistCost[jumpDistLvl];
    }

    public int getDamageDropCost() {
        if (damageDropLvl == MAX_DAMAGE_DROP_LVL) return 0;
        return damageDropCost[damageDropLvl];
    }

    public void playAttackSound() {
        attackSound.play(0.25f);
    }

    public int[] getData() {
        int[] data = new int[4];
        data[0] = getDamageLvl();
        data[1] = jumpLvl;
        data[2] = jumpDistLvl;
        data[3] = damageDropLvl;

        return data;
    }

    public void setData(int[] data) {
        setLevels(data[0], 0, 0);
        jumpLvl = data[1];
        jumpDistLvl = data[2];
        damageDropLvl = data[3];
    }

    public void dispose() {
        texRegNorm.getTexture().dispose();
        texRegSel.getTexture().dispose();
        texRegUpgradeBtn[0].getTexture().dispose();
        numberTexRegs[0].getTexture().dispose();

        attackSound.dispose();
    }
}
