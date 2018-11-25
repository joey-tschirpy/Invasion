package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameScreen implements Screen {
    private boolean teslaAttack;
    private boolean beamAttack;
    private Image beamImage;
    private TextureRegion[] beamTexRegs;

    private float deltaTime, enemySpawnTimer, turretTimer, particleTimer, shootTimer, delayTimer,
            beamSoundTimer, bossTimer, bossExplosionTimer;
    private Vector2 touchPos = new Vector2(0, 0);
    private int selectedIndex;

    private static TextureRegion[] numberTexRegs;

    int coinPIndex;
    private Image[] currencyDigitImgs;

//    private Image[] minDigitImgs;
//    private Image[] secDigitImgs;

    private int level;
    private boolean levelUp;
    private Image levelUpImg;
    private Image[] levelDigitImgs;

    private Image bossWarningImg;
    private float enemySpawnCounter;
    private boolean boss;
    private Image bossHealthFrame;
    private ProgressBar bossHealthBar;
    private int bossIndex;
    private boolean bossSpawned;
    private boolean bossDead;

    private Image[] finalMinDigitImgs;
    private Image[] finalSecDigitImgs;
    private Image[] finalLevelDigitImgs;
    private Image[] highLevelDigitImgs;

    private boolean gamePaused;
    private ImageButton pauseBtn;

    private Image pauseBg;
    private Image failTxtImg;
    private ImageButton continueBtn;
    private ImageButton restartBtn;
    private ImageButton workshopBtn;

    private Image wallImg;

    private ImageButton[] turretBtns;
    private Image[] abilityImgs;

    private Image[][] abilityLightImgs;
    private TextureRegion[] lightTexRegs = new TextureRegion[2];
    private ProgressBar[] abilityBars;

    private float wave;
    private boolean megaBomb;
    private boolean freeze;

    private ProgressBar integrityBar;
    private ProgressBar shieldBar;

    private Sound bossWarningSound;
    private Sound levelUpSound;
    private Sound shieldBoosterSound;
    private Sound explosionSound;
    private Music megaBombMusic;
    private Music freezeMusic;

    private boolean isFailState;

    public GameScreen() {
    }

    public void create() {
        Invasion.game.stopMusic();
        Invasion.game.playGameMusic();

        gamePaused = false;
        isFailState = false;

        delayTimer = 0;
        enemySpawnTimer = 0;
        particleTimer = 0;
        megaBomb = false;

        level = Invasion.game.getCurrentLevel();
        levelUp = false;
        enemySpawnCounter = 0;
        bossTimer = 0;
        bossExplosionTimer = 0f;
        boss = false;
        bossSpawned = false;
        bossDead = false;

        beamSoundTimer = Float.MAX_VALUE;

        Structure.getInstance().resetIntegrity();
        Structure.getInstance().resetShield();

        Invasion.game.projectiles.reset();
        Invasion.game.particles.reset();
        Invasion.game.enemies.reset();

        turretBtns = new ImageButton[Invasion.game.MAX_TURRET_LOADOUT];
        abilityImgs = new Image[Invasion.game.MAX_ABILITY_LOADOUT];

        // Setting up texture assets
        Invasion.game.assetManager.load("GameScreen/Level1BgImg.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/bgImgFrame.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/beams.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/chargeSpritesheet.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/PauseImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/ContinueImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/RestartImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/WorkshopImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/FailTxtImg.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/LevelUpImg.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/BossWarningImg.png", Texture.class);
        Invasion.game.assetManager.load("GameScreen/BossHPImgFrame.png", Texture.class);
        Invasion.game.assetManager.load("NumbersTxtSht_72.png", Texture.class);
        Invasion.game.assetManager.finishLoading();

        // Background image
        Image bgImg;
        if (Invasion.game.assetManager.isLoaded("GameScreen/Level1BgImg.png")) {
            bgImg = new Image(new Sprite(Invasion.game.assetManager.get("GameScreen/Level1BgImg.png",
                    Texture.class)));

            Invasion.game.stage.addActor(bgImg);
        }

        // Background frame image
        Image bgFrameImg;
        if (Invasion.game.assetManager.isLoaded("GameScreen/bgImgFrame.png")) {
            bgFrameImg = new Image(new Sprite(Invasion.game.assetManager.get("GameScreen/bgImgFrame.png",
                    Texture.class)));

            Invasion.game.stage.addActor(bgFrameImg);
        }

        // Number images - Black 72
        if (Invasion.game.assetManager.isLoaded("NumbersTxtSht_72.png")) {
            numberTexRegs = new TextureRegion[10];
            Texture numbersTex = Invasion.game.assetManager.get("NumbersTxtSht_72.png", Texture.class);
            for (int i = 0; i < numberTexRegs.length; i++) {
                numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                        numbersTex.getWidth() / 10, numbersTex.getHeight());
            }
        }

        // Timer number images
//        minDigitImgs = new Image[2];
//        secDigitImgs = new Image[2];
//
//        minDigitImgs[0] = new Image(numberTexRegs[0]);
//        minDigitImgs[1] = new Image(numberTexRegs[0]);
//        secDigitImgs[0] = new Image(numberTexRegs[0]);
//        secDigitImgs[1] = new Image(numberTexRegs[0]);
//
//        minDigitImgs[0].setPosition(1668f, 1012);
//        minDigitImgs[1].setPosition(minDigitImgs[0].getX() + minDigitImgs[0].getWidth(),
//                minDigitImgs[0].getY());
//        secDigitImgs[0].setPosition(minDigitImgs[1].getX() + minDigitImgs[1].getWidth() + 26f,
//                minDigitImgs[1].getY());
//        secDigitImgs[1].setPosition(secDigitImgs[0].getX() + secDigitImgs[0].getWidth(),
//                secDigitImgs[0].getY());
//
//        Invasion.game.stage.addActor(minDigitImgs[0]);
//        Invasion.game.stage.addActor(minDigitImgs[1]);
//        Invasion.game.stage.addActor(secDigitImgs[0]);
//        Invasion.game.stage.addActor(secDigitImgs[1]);

        // Pause background
        Pixmap pauseBgPixmap = new Pixmap((int)Invasion.game.GW_WIDTH, (int)Invasion.game.GW_HEIGHT,
                Pixmap.Format.RGBA8888);
        pauseBgPixmap.setColor(new Color(0f, 0f, 0f, 0.8f));
        pauseBgPixmap.fill();
        pauseBg = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(pauseBgPixmap))));
        pauseBgPixmap.dispose();

        // Fail text image
        if (Invasion.game.assetManager.isLoaded("GameScreen/FailTxtImg.png")) {
            failTxtImg = new Image(new Sprite(Invasion.game.assetManager.get("GameScreen/FailTxtImg.png",
                    Texture.class)));
            failTxtImg.setPosition(492, 636);
        }

        // Pause Menu Buttons

        // Continue button
        if (Invasion.game.assetManager.isLoaded("GameScreen/ContinueImgBtnSht.png")) {
            Texture tex = Invasion.game.assetManager.get("GameScreen/ContinueImgBtnSht.png", Texture.class);

            TextureRegion contTexRegUp = new TextureRegion(tex, 0, 0,
                    tex.getWidth(), tex.getHeight() / 2);
            TextureRegion contTexRegDown = new TextureRegion(tex, 0, tex.getHeight() / 2,
                    tex.getWidth(), tex.getHeight() / 2);

            ImageButton.ImageButtonStyle contBtnStyle = new ImageButton.ImageButtonStyle();
            contBtnStyle.up = new TextureRegionDrawable(contTexRegUp);
            contBtnStyle.down = new TextureRegionDrawable(contTexRegDown);

            continueBtn = new ImageButton(contBtnStyle);
            continueBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();

                    Invasion.game.scaleGameMusicVolume(2f);

                    if (megaBomb) {
                        megaBombMusic.play();
                    }

                    if (freeze) {
                        freezeMusic.play();
                    }

                    gamePaused = false;
                    pauseBg.remove();
                    continueBtn.remove();
                    restartBtn.remove();
                    workshopBtn.remove();
                }
            });
            continueBtn.setPosition(Invasion.game.GW_WIDTH / 2f - continueBtn.getWidth() / 2f,
                    Invasion.game.GW_HEIGHT / 2f - continueBtn.getHeight() / 2f + 1.5f * continueBtn.getHeight());
        }

        // Restart button
        if (Invasion.game.assetManager.isLoaded("GameScreen/RestartImgBtnSht.png")) {
            Texture tex = Invasion.game.assetManager.get("GameScreen/RestartImgBtnSht.png", Texture.class);

            TextureRegion restTexRegUp = new TextureRegion(tex, 0, 0,
                    tex.getWidth(), tex.getHeight() / 2);
            TextureRegion restTexRegDown = new TextureRegion(tex, 0, tex.getHeight() / 2,
                    tex.getWidth(), tex.getHeight() / 2);

            ImageButton.ImageButtonStyle restBtnStyle = new ImageButton.ImageButtonStyle();
            restBtnStyle.up = new TextureRegionDrawable(restTexRegUp);
            restBtnStyle.down = new TextureRegionDrawable(restTexRegDown);

            restartBtn = new ImageButton(restBtnStyle);
            restartBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();

                    Invasion.game.assetManager.clear();
                    Invasion.game.stage.clear();
                    create();
                }
            });
            restartBtn.setPosition(Invasion.game.GW_WIDTH / 2f - restartBtn.getWidth() / 2f,
                    Invasion.game.GW_HEIGHT / 2f - restartBtn.getHeight() / 2f);
        }

        // Workshop button
        if (Invasion.game.assetManager.isLoaded("GameScreen/WorkshopImgBtnSht.png")) {
            Texture tex = Invasion.game.assetManager.get("GameScreen/WorkshopImgBtnSht.png", Texture.class);

            TextureRegion workTexRegUp = new TextureRegion(tex, 0, 0,
                    tex.getWidth(), tex.getHeight() / 2);
            TextureRegion workTexRegDown = new TextureRegion(tex, 0, tex.getHeight() / 2,
                    tex.getWidth(), tex.getHeight() / 2);

            ImageButton.ImageButtonStyle workBtnStyle = new ImageButton.ImageButtonStyle();
            workBtnStyle.up = new TextureRegionDrawable(workTexRegUp);
            workBtnStyle.down = new TextureRegionDrawable(workTexRegDown);

            workshopBtn = new ImageButton(workBtnStyle);
            workshopBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();

                    Invasion.game.setScreen(Invasion.workshopScreen);
                }
            });
            workshopBtn.setPosition(Invasion.game.GW_WIDTH / 2f - workshopBtn.getWidth() / 2f,
                    Invasion.game.GW_HEIGHT / 2f - workshopBtn.getHeight() / 2f - 1.5f * workshopBtn.getHeight());
        }

        // Pause button
        if (Invasion.game.assetManager.isLoaded("GameScreen/PauseImgBtnSht.png")) {
            Texture tex = Invasion.game.assetManager.get("GameScreen/PauseImgBtnSht.png", Texture.class);

            TextureRegion pauseTexRegUp = new TextureRegion(tex, 0, 0, tex.getWidth(), tex.getHeight() / 2);
            TextureRegion pauseTexRegDown = new TextureRegion(tex, 0, tex.getHeight() / 2, tex.getWidth(),
                    tex.getHeight() / 2);

            ImageButton.ImageButtonStyle btnStyle = new ImageButton.ImageButtonStyle();
            btnStyle.up = new TextureRegionDrawable(pauseTexRegUp);
            btnStyle.down = new TextureRegionDrawable(pauseTexRegDown);

            pauseBtn = new ImageButton(btnStyle);
            pauseBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();
                    Invasion.game.scaleGameMusicVolume(0.5f);
                    megaBombMusic.pause();
                    freezeMusic.pause();

                    gamePaused = true;
                    Invasion.game.stage.addActor(pauseBg);
                    Invasion.game.stage.addActor(continueBtn);
                    Invasion.game.stage.addActor(restartBtn);
                    Invasion.game.stage.addActor(workshopBtn);
                }
            });

            pauseBtn.setPosition(63f - pauseBtn.getWidth() / 2f, 1002f - pauseBtn.getHeight() / 2f);

            Invasion.game.stage.addActor(pauseBtn);
        }

        Texture wall = new Texture("GameScreen/wallImg.jpg");
        TextureRegion wallTextureRegion = new TextureRegion(wall);
        TextureRegionDrawable wallTexRegionDrawable = new TextureRegionDrawable(wallTextureRegion);

        wallImg = new Image(wallTexRegionDrawable);
        wallImg.setHeight(Invasion.game.GW_HEIGHT);
        wallImg.setPosition(500, 0);

        Invasion.game.stage.addActor(wallImg);

        wave = getWallEdge();

        // Turret Buttons
        for (int i = 0; i < turretBtns.length; i++) {
            TextureRegionDrawable texRegDrawableNorm = new TextureRegionDrawable(
                    Invasion.game.turretLoadout[i].getTexRegNorm());
            TextureRegionDrawable texRegDrawableSel = new TextureRegionDrawable(
                    Invasion.game.turretLoadout[i].getTexRegSel());

            ImageButton.ImageButtonStyle btnStyle = new ImageButton.ImageButtonStyle();
            btnStyle.up = texRegDrawableNorm;
            btnStyle.checked = texRegDrawableSel;

            turretBtns[i] = new ImageButton(btnStyle);
            turretBtns[i].addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    for (ImageButton turretBtn : turretBtns) {
                        turretBtn.setChecked(false);
                        turretBtn.setRotation(0f);
                    }
                    ((ImageButton)event.getTarget()).setChecked(true);
                }
            });

            turretBtns[i].setTransform(true);
            turretBtns[i].setOrigin(turretBtns[i].getWidth() / 2f, turretBtns[i].getHeight() / 2f);
            turretBtns[i].setPosition(0, Invasion.game.GW_HEIGHT / 2f + turretBtns[i].getHeight() /
                    2f - (i * turretBtns[i].getHeight()));
//            turretBtns[i].setPosition(1f * Invasion.game.GW_WIDTH / 20f,
//                    (7.5f - i * 2.5f) * Invasion.game.GW_HEIGHT / 10f - turretBtns[i].getWidth() / 2f);

            Invasion.game.turretLoadout[i].setCenter(turretBtns[i].getX() + turretBtns[i].getOriginX(),
                    turretBtns[i].getY() + turretBtns[i].getOriginY());

            Invasion.game.stage.addActor(turretBtns[i]);
        }
        turretBtns[0].setChecked(true); // 1st turret selected from start

        // Ability Images
        for (int i = 0; i < abilityImgs.length; i++) {
            abilityImgs[i] = new Image(new TextureRegionDrawable(
                    Invasion.game.abilityLoadout[i].getTexRegNorm()));

            abilityImgs[i].setPosition(turretBtns[i].getX() + turretBtns[i].getWidth(), turretBtns[i].getY());

            Invasion.game.stage.addActor(abilityImgs[i]);

            final int index = i;
            switch(Invasion.game.abilityLoadout[i].getType()) {
                case MEGA_BOMB:
                    abilityImgs[i].addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            MegaBomb mb = MegaBomb.getInstance();

                            if (mb.isUsable() && mb.useAbility()) {
                                // Cooldown
                                mb.resetCooldownTimer();
                                ((Image)event.getTarget()).setDrawable(
                                        new TextureRegionDrawable(mb.getTexRegNorm()));

                                megaBomb = true;
                                megaBombMusic.play();

                                abilityLightImgs[index][mb.getAmount()].setDrawable(
                                        new TextureRegionDrawable(lightTexRegs[1]));

                                abilityBars[index].setValue(0f);
                            }
                        }
                    });
                    break;
                case SHIELD_BOOSTER:
                    abilityImgs[i].addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            ShieldBooster sb = ShieldBooster.getInstance();

                            if (sb.isUsable() && sb.useAbility()) {
                                shieldBoosterSound.play(0.2f);

                                // Cooldown
                                sb.resetCooldownTimer();
                                ((Image)event.getTarget()).setDrawable(
                                        new TextureRegionDrawable(sb.getTexRegNorm()));

                                Structure.getInstance().addShield(sb.getBoost());

                                abilityLightImgs[index][sb.getAmount()].setDrawable(
                                        new TextureRegionDrawable(lightTexRegs[1]));

                                abilityBars[index].setValue(0f);
                            }
                        }
                    });
                    break;
                case FREEZE:
                    abilityImgs[i].addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            Freeze f = Freeze.getInstance();

                            if (f.isUsable() && f.useAbility()) {
                                freezeMusic.play();

                                // Cooldown
                                f.resetCooldownTimer();
                                ((Image)event.getTarget()).setDrawable(
                                        new TextureRegionDrawable(f.getTexRegNorm()));

                                //Duration
                                freeze = true;
                                f.resetFreezeTimer();

                                abilityLightImgs[index][f.getAmount()].setDrawable(
                                        new TextureRegionDrawable(lightTexRegs[1]));

                                abilityBars[index].setValue(0f);

                                Invasion.game.enemies.freeze();
                            }
                        }
                    });
                    break;
            }

            Invasion.game.abilityLoadout[i].resetAmount();
            Invasion.game.abilityLoadout[i].resetCooldownTimer();
        }

        // Ability Counter lights
        if (Invasion.game.assetManager.isLoaded("GameScreen/chargeSpritesheet.png")) {
            abilityLightImgs = new Image[abilityImgs.length][];

            for (int i = 0; i < abilityImgs.length; i ++) {
                switch(Invasion.game.abilityLoadout[i].getType()) {
                    case MEGA_BOMB:
                        abilityLightImgs[i] = new Image[MegaBomb.getInstance().getCapacity()];
                        break;
                    case SHIELD_BOOSTER:
                        abilityLightImgs[i] = new Image[ShieldBooster.getInstance().getCapacity()];
                        break;
                    case FREEZE:
                        abilityLightImgs[i] = new Image[Freeze.getInstance().getCapacity()];
                        break;
                }

                Texture tex = Invasion.game.assetManager.get("GameScreen/chargeSpritesheet.png",
                        Texture.class);

                // 0: Active, 1: Inactive
                lightTexRegs[0] = new TextureRegion(tex, 0, 0, 36, 36);
                lightTexRegs[1] = new TextureRegion(tex, 36, 0, 36, 36);
//                lightTexRegs[0] = new TextureRegion(tex, 0, 0, tex.getWidth() / 2f, tex.getHeight());
//                lightTexRegs[1] = new TextureRegion(tex, tex.getWidth() / 2f, 0,
//                        tex.getWidth() / 2f,tex.getHeight());

                for (int j = 0; j < abilityLightImgs[i].length; j++) {
                    abilityLightImgs[i][j] = new Image(new TextureRegionDrawable(lightTexRegs[0]));
                    abilityLightImgs[i][j].setPosition(
                            abilityImgs[i].getX() + abilityImgs[i].getWidth() / 2f -
                                    abilityLightImgs[i].length * abilityLightImgs[i][j].getWidth() / 2f +
                                    j * abilityLightImgs[i][j].getWidth(), abilityImgs[i].getY() + 201);

                    Invasion.game.stage.addActor(abilityLightImgs[i][j]);
                }
            }
        }

        // Ability cooldown bars
        int barWidth = 168;
        int barHeight = 24;

        Pixmap pixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        TextureRegionDrawable barDrawable = new TextureRegionDrawable(new TextureRegion(
                new Texture(pixmap)));
        pixmap.dispose();

        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle();
        barStyle.background = barDrawable;

        // Filled pixmap
        pixmap = new Pixmap(0, barHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        barDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        barStyle.knob = barDrawable;

        // Empty pixmap
        pixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        barDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        barStyle.knobBefore = barDrawable;

        abilityBars = new ProgressBar[abilityImgs.length];
        for (int i = 0; i < abilityBars.length; i++) {
            switch (Invasion.game.abilityLoadout[i].getType()) {
                case MEGA_BOMB:
                    abilityBars[i] = new ProgressBar(0f, MegaBomb.getInstance().getCooldown(),
                            0.01f, false, barStyle);
                    break;
                case SHIELD_BOOSTER:
                    abilityBars[i] = new ProgressBar(0f, ShieldBooster.getInstance().getCooldown(),
                            0.01f, false, barStyle);
                    break;
                case FREEZE:
                    abilityBars[i] = new ProgressBar(0f, Freeze.getInstance().getCooldown(),
                            0.01f, false, barStyle);
                    break;
            }

            abilityBars[i].setBounds(abilityImgs[i].getX() + 44, abilityImgs[i].getY() + 24,
                    (float)barWidth, (float)barHeight);

            Invasion.game.stage.addActor(abilityBars[i]);
        }

        // Status bars
        barWidth = 453;
        barHeight = 42;

        // Health bar
        Pixmap hpPixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        hpPixmap.setColor(Color.DARK_GRAY);
        hpPixmap.fill();
        TextureRegionDrawable hpBarDrawable = new TextureRegionDrawable(new TextureRegion(
                new Texture(hpPixmap)));
        hpPixmap.dispose();

        ProgressBar.ProgressBarStyle hpBarStyle = new ProgressBar.ProgressBarStyle();
        hpBarStyle.background = hpBarDrawable;

        // Filled pixmap
        hpPixmap = new Pixmap(0, barHeight, Pixmap.Format.RGBA8888);
        hpPixmap.setColor(Color.RED);
        hpPixmap.fill();
        hpBarDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(hpPixmap)));
        hpPixmap.dispose();
        hpBarStyle.knob = hpBarDrawable;

        // Empty pixmap
        hpPixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        hpPixmap.setColor(Color.RED);
        hpPixmap.fill();
        hpBarDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(hpPixmap)));
        hpPixmap.dispose();
        hpBarStyle.knobBefore = hpBarDrawable;

        // Status progress bars
        integrityBar = new ProgressBar(0f, Structure.getInstance().getIntegrityCap(), 1f, false, hpBarStyle);
        integrityBar.setBounds(24f, 24f, (float)barWidth, (float)barHeight);
        integrityBar.setAnimateDuration(0.1f);

        Invasion.game.stage.addActor(integrityBar);

        // Shield bar
        Pixmap shieldPixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        shieldPixmap.setColor(Color.DARK_GRAY);
        shieldPixmap.fill();
        TextureRegionDrawable shieldBarDrawable = new TextureRegionDrawable(new TextureRegion(
                new Texture(shieldPixmap)));
        shieldPixmap.dispose();

        ProgressBar.ProgressBarStyle shieldBarStyle = new ProgressBar.ProgressBarStyle();
        shieldBarStyle.background = shieldBarDrawable;

        // Filled pixmap
        shieldPixmap = new Pixmap(0, barHeight, Pixmap.Format.RGBA8888);
        shieldPixmap.setColor(new Color(0f, 0.5f, 1f, 1f));
        shieldPixmap.fill();
        shieldBarDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(shieldPixmap)));
        shieldPixmap.dispose();
        shieldBarStyle.knob = shieldBarDrawable;

        // Empty pixmap
        shieldPixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        shieldPixmap.setColor(new Color(0f, 0.5f, 1f, 1f));
        shieldPixmap.fill();
        shieldBarDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(shieldPixmap)));
        shieldPixmap.dispose();
        shieldBarStyle.knobBefore = shieldBarDrawable;

        // Status progress bars
        shieldBar = new ProgressBar(0f, Structure.getInstance().getShieldCap(), 1f, false, shieldBarStyle);
        shieldBar.setBounds(24f, 90f, (float)barWidth, (float)barHeight);
        shieldBar.setAnimateDuration(0.1f);

        Invasion.game.stage.addActor(shieldBar);

        // image sheet with 8 beam images
        final int REG_NUM = 8;
        beamTexRegs = new TextureRegion[REG_NUM];
        if (Invasion.game.assetManager.isLoaded("GameScreen/beams.png")) {
            for (int i = 0; i < REG_NUM; i++) {
                Texture beamTex = Invasion.game.assetManager.get("GameScreen/beams.png", Texture.class);

                beamTexRegs[i] = new TextureRegion(beamTex, 0, i * beamTex.getHeight() / REG_NUM,
                        beamTex.getWidth(), beamTex.getHeight() / REG_NUM);
            }
        }
        beamImage = new Image(beamTexRegs[0]);

        coinPIndex = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
        Invasion.game.particles.velocity[coinPIndex].set(0f, 0f);
        Invasion.game.particles.image[coinPIndex].setScale(0.14f, 0.14f);
        Invasion.game.particles.size[coinPIndex].set(
                Invasion.game.particles.image[coinPIndex].getWidth() *
                        Invasion.game.particles.image[coinPIndex].getScaleX(),
                Invasion.game.particles.image[coinPIndex].getHeight() *
                        Invasion.game.particles.image[coinPIndex].getScaleY());
        Invasion.game.particles.position[coinPIndex].set(
                175f - Invasion.game.particles.size[coinPIndex].x / 2f,
                1001.5f - Invasion.game.particles.size[coinPIndex].y / 2f);

        // Level Up label image
        if (Invasion.game.assetManager.isLoaded("GameScreen/LevelUpImg.png")) {
            levelUpImg = new Image(new Sprite(Invasion.game.assetManager.get("GameScreen/LevelUpImg.png",
                    Texture.class)));
            levelUpImg.setColor(1f, 0f, 0f, 1f);
            levelUpImg.setPosition(Invasion.game.GW_WIDTH -
                    (Invasion.game.GW_WIDTH - getWallEdge()) / 2f - levelUpImg.getWidth() / 2f,
                    Invasion.game.GW_HEIGHT / 2f - levelUpImg.getHeight() / 2f);
        }

        // Boss warning label image
        if (Invasion.game.assetManager.isLoaded("GameScreen/BossWarningImg.png")) {
            bossWarningImg = new Image(new Sprite(Invasion.game.assetManager.get("GameScreen/BossWarningImg.png",
                    Texture.class)));
            bossWarningImg.setColor(1f, 0f, 0f, 1f);
            bossWarningImg.setPosition(Invasion.game.GW_WIDTH -
                            (Invasion.game.GW_WIDTH - getWallEdge()) / 2f - bossWarningImg.getWidth() / 2f,
                    Invasion.game.GW_HEIGHT / 2f - bossWarningImg.getHeight() / 2f);
        }

        // Boss health bar frame
        if (Invasion.game.assetManager.isLoaded("GameScreen/BossHPImgFrame.png")) {
            bossHealthFrame = new Image(new Sprite(Invasion.game.assetManager.get("GameScreen/BossHPImgFrame.png",
                    Texture.class)));
            bossHealthFrame.setPosition(getWallEdge(), 0f);
        }

        bossWarningSound = Gdx.audio.newSound(Gdx.files.internal("Audio/bossSiren.wav"));
        levelUpSound = Gdx.audio.newSound(Gdx.files.internal("Audio/level_up.wav"));
        shieldBoosterSound = Gdx.audio.newSound(Gdx.files.internal("Audio/shieldBoosterSound.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("Audio/explosion_sound.wav"));

        megaBombMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/megaBomb.wav"));
        freezeMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/freeze.wav"));
    }

    public void render (float f) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();

        if (!gamePaused) {
            enemySpawnTimer += deltaTime;
            delayTimer += deltaTime;

            Invasion.game.enemies.update(deltaTime, freeze);
            Invasion.game.projectiles.update(deltaTime);
            Invasion.game.particles.update(deltaTime);
            update(deltaTime);

            Invasion.game.particles.bringCoinToTop();
            Invasion.game.projectiles.bringToTop();
            bringTurretsToTop();
        }

        if (Structure.getInstance().getIntegrity() <= 0 && !isFailState) {
            failState();
            isFailState = true;
        }

        Invasion.game.stage.act();
        Invasion.game.stage.draw();

        Invasion.game.stage.getBatch().begin();

        if (teslaAttack) updateTeslaAttack();
        teslaAttack = false;

        if (beamAttack) updateBeamAttack();
        beamAttack = false;

        Invasion.game.stage.getBatch().end();
    }

    @Override
    public void dispose() {
        bossWarningSound.dispose();
        levelUpSound.dispose();
        shieldBoosterSound.dispose();
        explosionSound.dispose();

        megaBombMusic.dispose();
        freezeMusic.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {
        create();
    }

    @Override
    public void hide() {
        Invasion.game.assetManager.clear();
        Invasion.game.stage.clear();
    }

    // returns random integer between lower and upper inclusive
    private int rnd(int lower, int upper) {
        return (int)(Math.random() * (upper - lower + 1) + lower);
    }

    private void update(float deltaTime) {
        if (enemySpawnCounter >= 100) {
            boss = true;
            enemySpawnCounter = 0;
            bossTimer = 0;
            delayTimer = 0f;

            Invasion.game.stage.addActor(bossWarningImg);

            bossWarningSound.play();
        }

        // Boss fight
        if (boss && delayTimer >= 3f) {
            bossTimer += deltaTime;

            if (!bossSpawned && bossTimer >= 2f) {
                spawnBoss();
                bossSpawned = true;
            }

            if (bossSpawned) {
                // Boss health bar update
                bossHealthBar.setValue(Invasion.game.enemies.health[bossIndex]);

                if (!bossDead && Invasion.game.enemies.health[bossIndex] <= 0) {
                    bossDead = true;
                    bossTimer = 0;
                }
            }

            if (bossDead) {
                bossExplosionTimer += deltaTime;

                if (bossExplosionTimer >= 0.5f) {
                    int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.MISSILE_EXPLOSION);
                    Invasion.game.particles.position[pIndex].set(
                            random(Invasion.game.enemies.position[bossIndex].x -
                                    Invasion.game.particles.size[pIndex].x / 2f,
                                    Invasion.game.enemies.position[bossIndex].x +
                                            Invasion.game.enemies.size[bossIndex].x -
                                            Invasion.game.particles.size[pIndex].x / 2f),
                            random(Invasion.game.enemies.position[bossIndex].y -
                                    Invasion.game.particles.size[pIndex].y / 2f,
                                    Invasion.game.enemies.position[bossIndex].y +
                                            Invasion.game.enemies.size[bossIndex].y -
                                            Invasion.game.particles.size[pIndex].y / 2f));

                    explosionSound.play();

                    bossExplosionTimer = 0;
                }

                if (bossTimer >= 3f) {
                    boss = false;
                    bossDead = false;
                    bossSpawned = false;
                    bossTimer = 0f;
                    bossExplosionTimer = 0f;
                    bossHealthFrame.remove();
                    bossHealthBar.remove();
                    Invasion.game.enemies.bossDeath(bossIndex);

                    Invasion.game.setLevelReached(++level);
                    levelUp = true;
                    delayTimer = 0f;

                    Invasion.game.stage.addActor(levelUpImg);
                    levelUpSound.play(0.2f);
                }
            }
        }

        // Display current level
        if (levelDigitImgs != null) {
            for (int i = 0; i < levelDigitImgs.length; i++) {
                levelDigitImgs[i].remove();
            }
        }
        levelDigitImgs = new Image[("" + level).length()];
        for (int i = 0; i < levelDigitImgs.length; i++){
            int number = (int)(level / Math.pow(10, (levelDigitImgs.length - i - 1)));
            levelDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                levelDigitImgs[i].setPosition(879f, 1012f);
            }
            else {
                levelDigitImgs[i].setPosition(
                        levelDigitImgs[i - 1].getX() + levelDigitImgs[i].getWidth(),
                        levelDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(levelDigitImgs[i]);
        }
        Invasion.game.enemies.setDifficultyMultiplier(level);

        // Display currency
        if (currencyDigitImgs != null) {
            for (int i = 0; i < currencyDigitImgs.length; i++) {
                currencyDigitImgs[i].remove();
            }
        }

        int currency = Invasion.game.getCurrency();
        if (currency > 99999) currency = 99999;

        currencyDigitImgs = new Image[("" + currency).length()];
        for (int i = 0; i < currencyDigitImgs.length; i++){
            int number = (int)(currency / Math.pow(10, (currencyDigitImgs.length - i - 1)));
            currencyDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == 0) {
                currencyDigitImgs[i].setPosition(218, 978);
            }
            else {
                currencyDigitImgs[i].setPosition(
                        currencyDigitImgs[i - 1].getX() + currencyDigitImgs[i].getWidth(),
                        currencyDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(currencyDigitImgs[i]);
        }

        // Update Timer display
//        int minutes = (int)(gameTimer / 60);
//        minDigitImgs[0].setDrawable(new TextureRegionDrawable(numberTexRegs[(minutes / 10) % 10]));
//        minDigitImgs[1].setDrawable(new TextureRegionDrawable(numberTexRegs[minutes % 10]));
//
//        int seconds = (int)gameTimer % 60;
//        secDigitImgs[0].setDrawable(new TextureRegionDrawable(numberTexRegs[(seconds / 10) % 10]));
//        secDigitImgs[1].setDrawable(new TextureRegionDrawable(numberTexRegs[seconds % 10]));

        // Update Ability cooldown bars
        for (int i = 0; i < abilityBars.length; i++) {
            abilityBars[i].setValue(Invasion.game.abilityLoadout[i].getCooldownTimer());
        }

        // Index of selected turret
        for (int i = 0; i < turretBtns.length; i++) {
            if (turretBtns[i].isChecked()) {
                selectedIndex = i;
                break;
            }
        }

        turretTimer += deltaTime;
        shootTimer += deltaTime;
        beamSoundTimer += deltaTime;

        Vector2 leftBoundaryClick = Invasion.game.stage.stageToScreenCoordinates(new Vector2(wallImg.getX(),0));
        leftBoundaryClick.x -= Invasion.game.viewport.getLeftGutterWidth();

        // Continue touching
        if (Gdx.input.isTouched() && Gdx.input.getX() > leftBoundaryClick.x) {
            // Rotate turret
            touchPos.x = Gdx.input.getX() - Invasion.game.viewport.getLeftGutterWidth();
            touchPos.y = Gdx.input.getY() - Invasion.game.viewport.getTopGutterHeight();

            Vector2 turretPos = Invasion.game.stage.stageToScreenCoordinates(new Vector2(
                    turretBtns[selectedIndex].getX() + turretBtns[selectedIndex].getWidth() / 2f,
                    turretBtns[selectedIndex].getY() + turretBtns[selectedIndex].getHeight() / 2f));
            turretPos.x -= Invasion.game.viewport.getLeftGutterWidth();
            turretPos.y += Invasion.game.viewport.getTopGutterHeight();

            float deltaX = touchPos.x - turretPos.x;
            float deltaY = turretPos.y - touchPos.y;
            double degAngle = Math.toDegrees(Math.atan2(deltaY, deltaX));
            if (Invasion.game.turretLoadout[selectedIndex].isRotatable) {
                turretBtns[selectedIndex].setRotation((float) degAngle);
            }

            // Spawn projectile from turret after rotation
            if (turretTimer >= 1.0f / Invasion.game.turretLoadout[selectedIndex].getFireRate()) {
                Vector2 stageTouchCoords = Invasion.game.stage.screenToStageCoordinates(
                        new Vector2(touchPos.x + Invasion.game.viewport.getLeftGutterWidth(),
                                touchPos.y + Invasion.game.viewport.getTopGutterHeight()));

                if (Invasion.game.turretLoadout[selectedIndex].getType() == Invasion.DefTurretEnum.TESLA) {
                    teslaAttack = true;
                }
                else if (Invasion.game.turretLoadout[selectedIndex].getType() == Invasion.DefTurretEnum.BEAM) {
                    beamAttack = true;
                }
                else { // turrets that use projectiles
                    int i = Invasion.game.projectiles.spawn(Invasion.game.turretLoadout[selectedIndex],
                            degAngle, stageTouchCoords);
                    if (i >= 0) {
                        Invasion.game.stage.addActor(Invasion.game.projectiles.images[i]);
                    }
                }

                turretTimer = 0f;
            }
        }
        else {
            shootTimer = 0f;
        }

        // Cooldown of abilities
        for (int i = 0; i < Invasion.game.abilityLoadout.length; i++) {
            Invasion.game.abilityLoadout[i].increaseCooldownTimer(deltaTime);

            if (Invasion.game.abilityLoadout[i].isUsable()) {
                abilityImgs[i].setDrawable(new TextureRegionDrawable(
                        Invasion.game.abilityLoadout[i].getTexRegSel()));
            }
        }

        // Level Up
        if (levelUp && delayTimer < 3f) {
            if (levelUpImg.getColor().r <= 0) {
                levelUpImg.addAction(Actions.color(new Color(1f, 0f, 0f, 1f), 0.25f));
            }
            else if (levelUpImg.getColor().b <= 0) {
                levelUpImg.addAction(Actions.color(new Color(0f, 0f, 1f, 1f), 0.25f));
            }
        }
        else {
            levelUp = false;
            levelUpImg.remove();
        }

        // Boss Warning
        if (boss && delayTimer < 3f) {
            if (bossWarningImg.getColor().r <= 0) {
                bossWarningImg.addAction(Actions.color(new Color(1f, 0f, 0f, 1f), 0.25f));
            }
            else if (bossWarningImg.getColor().b <= 0) {
                bossWarningImg.addAction(Actions.color(new Color(0f, 0f, 1f, 1f), 0.25f));
            }
        }
        else {
            bossWarningImg.remove();
        }

        // Enemy spawning
        if (enemySpawnTimer >= 0.5f && !boss && delayTimer >= 3f) {
            int spawnChance = rnd(1, 20);

            if (spawnChance <= 1 && level > 1) { // artillery 5% for level 2 and higher
                Invasion.game.enemies.spawn(Enemies.Type.ARTILLERY);
                enemySpawnCounter++;
            }
            else if (spawnChance <= 13) { // melee 60%
                Invasion.game.enemies.spawn(Enemies.Type.MELEE);
                enemySpawnCounter++;
            }
            else { // ranged 35%
                Invasion.game.enemies.spawn(Enemies.Type.RANGED);
                enemySpawnCounter++;
            }

            enemySpawnTimer = 0;
        }

        // Using mega bomb ability
        if(megaBomb)
        {
            wave += 1500f * deltaTime;

            particleTimer += deltaTime;
            enemySpawnTimer -= 2 * deltaTime;
            if(particleTimer > 0.05f)
            {
                final int EXPLOSIONS = 10;
                for(int i = 0; i < EXPLOSIONS; i++)
                {
                    int index = Invasion.game.particles.spawn(ParticleSystem.Type.MISSILE_EXPLOSION);
                    Invasion.game.particles.position[index].set(
                            wave - Invasion.game.particles.size[index].x / 2f,
                            (i + 1) * Invasion.game.GW_HEIGHT / (EXPLOSIONS + 1) - Invasion.game.particles.size[index].y / 2f);
                }
                particleTimer = 0;
            }

            if(wave > Invasion.game.GW_WIDTH)
            {
                wave = getWallEdge();
                megaBomb = false;

            }

            Invasion.game.enemies.megaBomb(wave);
        }

        // Using freeze ability
        if(freeze) {
            Freeze f = Freeze.getInstance();

            if (f.getTimer() < f.getDuration()) {
                f.increaseFreezeTimer(deltaTime);
                enemySpawnTimer -= deltaTime;
            }
            else {
                freeze = false;
                Invasion.game.enemies.unFreeze();
            }
        }
        else {
            if (freezeMusic.isPlaying()) {
                freezeMusic.stop();
            }
        }

        Structure.getInstance().regen(deltaTime);

        // Update status bars
        shieldBar.setValue(Structure.getInstance().getShield());
        integrityBar.setValue(Structure.getInstance().getIntegrity());

        // Reset coin spin
        if (Invasion.game.particles.lifetime[coinPIndex] - deltaTime <= 0)
            Invasion.game.particles.lifetime[coinPIndex] = Invasion.game.particles.LIFETIME;
    }

    private void updateTeslaAttack() {
        Vector2 stageTouchCoords = Invasion.game.stage.screenToStageCoordinates(
                new Vector2(touchPos.x + Invasion.game.viewport.getLeftGutterWidth(),
                        touchPos.y + Invasion.game.viewport.getTopGutterHeight()));

        int index = Invasion.game.enemies.getClosestEnemy(stageTouchCoords, 200f);
        if (index >= 0) {
            // Initial enemy
            TeslaTower tt = TeslaTower.getInstance();
            Vector2 enemyMidPoint = new Vector2(
                    Invasion.game.enemies.position[index].x + Invasion.game.enemies.size[index].x / 2f,
                    Invasion.game.enemies.position[index].y + Invasion.game.enemies.size[index].y / 2f);

            tt.drawChainLightning(Invasion.game.stage.getBatch(), enemyMidPoint);
            Invasion.game.enemies.takeDamage(index, tt.getDamage());
            Invasion.game.turretLoadout[selectedIndex].playAttackSound();

            int jumps = 0;
            int[] prevIndices = new int[tt.getJump()];
            for (int i = 0; i < prevIndices.length; i++) {
                prevIndices[i] = -1;
            }

            // Next enemies enemies
            while (index > 0 && jumps < tt.getJump()) {
                prevIndices[jumps] = index;
                Vector2 prevEnemyMidPoint = new Vector2(enemyMidPoint);
                index = Invasion.game.enemies.getClosestEnemy(prevEnemyMidPoint, prevIndices,
                        tt.getJumpDist());

                if (index < 0) break;

                enemyMidPoint.set(Invasion.game.enemies.position[index].x +
                                Invasion.game.enemies.size[index].x / 2f,
                        Invasion.game.enemies.position[index].y +
                                Invasion.game.enemies.size[index].y / 2f);

                tt.drawChainLightning(Invasion.game.stage.getBatch(), prevEnemyMidPoint, enemyMidPoint);

                float damage = tt.getDamage() * (float)Math.pow((double)(1 - tt.getDamageDrop()), jumps + 1);
                Invasion.game.enemies.takeDamage(index, damage);

                jumps++;
            }
        }
    }

    public void updateBeamAttack() {
        beamImage.setDrawable(new TextureRegionDrawable(beamTexRegs[(int) (
                shootTimer * Invasion.game.turretLoadout[selectedIndex].getFireRate()) % beamTexRegs.length]));

        float degAngle = turretBtns[selectedIndex].getRotation();

        beamImage.setOrigin(0, beamImage.getHeight() / 2f);

        beamImage.setScale(1f + BeamTurret.getInstance().getBeamWidth(), 1f + BeamTurret.getInstance().getBeamWidth());
        beamImage.setRotation(degAngle);

        double radAngle = Math.toRadians(degAngle);
        Vector2 dirUnitVec = new Vector2((float) Math.cos(radAngle), (float) Math.sin(radAngle));
        Vector2 imagePosition = new Vector2(Invasion.game.turretLoadout[selectedIndex].getCenter().x,
                Invasion.game.turretLoadout[selectedIndex].getCenter().y);
        imagePosition.mulAdd(dirUnitVec, Invasion.game.turretLoadout[selectedIndex].getTexRegSel().getRegionWidth() / 2);

        beamImage.setPosition(imagePosition.x - 30f, imagePosition.y - beamImage.getHeight() / 2f);

        beamImage.draw(Invasion.game.stage.getBatch(), 1f);

        degAngle = 90 - degAngle;

        dirUnitVec.rotate(-90);
        Vector2 botVec = new Vector2(imagePosition);
        botVec.mulAdd(dirUnitVec, beamImage.getHeight() * beamImage.getScaleY() / 2f);

        dirUnitVec.rotate(180);
        Vector2 topVec = new Vector2(imagePosition);
        topVec.mulAdd(dirUnitVec, beamImage.getHeight() * beamImage.getScaleY() / 2f);

        Invasion.game.enemies.beamHit(degAngle, botVec, topVec, deltaTime);
        if (beamSoundTimer > 0.25f) {
            BeamTurret.getInstance().playAttackSound();
            beamSoundTimer = 0f;
        }
    }

    public float getWallEdge() {
        return wallImg.getX() + wallImg.getWidth();
    }

    private void bringTurretsToTop() {
        // Display turrets on top
        for (int i = 0; i < turretBtns.length; i++) {
            turretBtns[i].setZIndex(Integer.MAX_VALUE);
        }
    }

    private void failState() {
        Invasion.game.stopMusic();
        freezeMusic.stop();
        megaBombMusic.stop();
        Invasion.game.playGameOverMusic();

        gamePaused = true;
        Invasion.game.stage.addActor(pauseBg);
        Invasion.game.stage.addActor(failTxtImg);
        Invasion.game.stage.addActor(restartBtn);
        Invasion.game.stage.addActor(workshopBtn);

        // Level reached
        finalLevelDigitImgs = new Image[("" + level).length()];
        for (int i = finalLevelDigitImgs.length - 1; i >= 0; i--) {
            int number = (int)(level / Math.pow(10, (finalLevelDigitImgs.length - i - 1)));
            finalLevelDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == finalLevelDigitImgs.length - 1) {
                finalLevelDigitImgs[i].setPosition(failTxtImg.getX() + failTxtImg.getWidth() -
                        finalLevelDigitImgs[finalLevelDigitImgs.length - 1].getWidth() - 14f,
                        failTxtImg.getY() + 86f);
            }
            else {
                finalLevelDigitImgs[i].setPosition(
                        finalLevelDigitImgs[i + 1].getX() - finalLevelDigitImgs[i].getWidth(),
                        finalLevelDigitImgs[i + 1].getY());
            }

            Invasion.game.stage.addActor(finalLevelDigitImgs[i]);
        }

        // highestLevel
        highLevelDigitImgs = new Image[("" + Invasion.game.getLevelReached()).length()];
        for (int i = highLevelDigitImgs.length - 1; i >= 0; i--) {
            int number = (int)(Invasion.game.getLevelReached() / Math.pow(10,
                    (highLevelDigitImgs.length - i - 1)));
            highLevelDigitImgs[i] = new Image(numberTexRegs[number % 10]);
            if (i == highLevelDigitImgs.length - 1) {
                highLevelDigitImgs[i].setPosition(failTxtImg.getX() + failTxtImg.getWidth() -
                        highLevelDigitImgs[highLevelDigitImgs.length - 1].getWidth() - 14f,
                        failTxtImg.getY() + 14f);
            }
            else {
                highLevelDigitImgs[i].setPosition(
                        highLevelDigitImgs[i + 1].getX() - highLevelDigitImgs[i].getWidth(),
                        highLevelDigitImgs[i + 1].getY());
            }

            Invasion.game.stage.addActor(highLevelDigitImgs[i]);
        }

        // Time played
//        int seconds = (int)gameTimer % 60;
//        finalSecDigitImgs = new Image[2];
//        finalSecDigitImgs[0] = new Image(new TextureRegionDrawable(numberRedTexRegs[(seconds / 10) % 10]));
//        finalSecDigitImgs[1] = new Image(new TextureRegionDrawable(numberRedTexRegs[seconds % 10]));
//
//        int minutes = (int)(gameTimer / 60);
//        finalMinDigitImgs = new Image[2];
//        finalMinDigitImgs[0] = new Image(new TextureRegionDrawable(numberRedTexRegs[(minutes / 10) % 10]));
//        finalMinDigitImgs[1] = new Image(new TextureRegionDrawable(numberRedTexRegs[minutes % 10]));
//
//        finalSecDigitImgs[1].setPosition(failTxtImg.getX() + failTxtImg.getWidth() -
//                finalSecDigitImgs[1].getWidth(), failTxtImg.getY());
//        finalSecDigitImgs[0].setPosition(finalSecDigitImgs[1].getX() - finalSecDigitImgs[0].getWidth(),
//                finalSecDigitImgs[1].getY());
//
//        finalMinDigitImgs[1].setPosition(finalSecDigitImgs[0].getX() - 48f -
//                finalMinDigitImgs[1].getWidth(), finalSecDigitImgs[0].getY());
//        finalMinDigitImgs[0].setPosition(finalMinDigitImgs[1].getX() - finalMinDigitImgs[0].getWidth(),
//                finalMinDigitImgs[1].getY());
//
//        Invasion.game.stage.addActor(finalMinDigitImgs[0]);
//        Invasion.game.stage.addActor(finalMinDigitImgs[1]);
//        Invasion.game.stage.addActor(finalSecDigitImgs[0]);
//        Invasion.game.stage.addActor(finalSecDigitImgs[1]);
    }

    private void spawnBoss() {
        if (level % 2 == 1) {
            bossIndex = Invasion.game.enemies.spawn(Enemies.Type.BOSS_TANK);
        }
        else {
            bossIndex = Invasion.game.enemies.spawn(Enemies.Type.BOSS_SHIP);
        }

        // Status bars
        int barWidth = 954;
        int barHeight = 40;

        // Health bar
        Pixmap hpPixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        hpPixmap.setColor(Color.DARK_GRAY);
        hpPixmap.fill();
        TextureRegionDrawable hpBarDrawable = new TextureRegionDrawable(new TextureRegion(
                new Texture(hpPixmap)));
        hpPixmap.dispose();

        ProgressBar.ProgressBarStyle hpBarStyle = new ProgressBar.ProgressBarStyle();
        hpBarStyle.background = hpBarDrawable;

        // Filled pixmap
        hpPixmap = new Pixmap(0, barHeight, Pixmap.Format.RGBA8888);
        hpPixmap.setColor(Color.RED);
        hpPixmap.fill();
        hpBarDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(hpPixmap)));
        hpPixmap.dispose();
        hpBarStyle.knob = hpBarDrawable;

        // Empty pixmap
        hpPixmap = new Pixmap(barWidth, barHeight, Pixmap.Format.RGBA8888);
        hpPixmap.setColor(Color.RED);
        hpPixmap.fill();
        hpBarDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(hpPixmap)));
        hpPixmap.dispose();
        hpBarStyle.knobBefore = hpBarDrawable;

        // Status progress bars
        bossHealthBar = new ProgressBar(0f, Invasion.game.enemies.health[bossIndex], 1f, false, hpBarStyle);
        bossHealthBar.setBounds(bossHealthFrame.getX() + 367f, bossHealthFrame.getY() + 24f,
                (float)barWidth, (float)barHeight);
        bossHealthBar.setAnimateDuration(0.1f);

        Invasion.game.stage.addActor(bossHealthFrame);
        Invasion.game.stage.addActor(bossHealthBar);
    }

    public float random(float a, float b) {
        // Random float value between a (inclusive) and b (exclusive)
        return (float)Math.random() * (b - a) + a;
    }
}