package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class WorkshopScreen implements Screen{
    private ImageButton structureBtn;
    private ImageButton[] turretBtns = new ImageButton[Invasion.DefTurretEnum.values().length];
    private ImageButton[] abilityBtns = new ImageButton[Invasion.DefTurretEnum.values().length];

    private Image[] costDigitImgs;
    TextureRegion[] numberTexRegs = new TextureRegion[10];

    private boolean isNewSelection;
    int coinPIndex;
    int[] upgradeCoinPIndexArr;

    Skin skin;

    private Sound loadoutSwapSound;

    public WorkshopScreen() {

    }

    public void create() {
        Invasion.game.stopGameMusic();
        Invasion.game.stopGameOverMusic();
        Invasion.game.playMenuMusic();

        skin = new Skin(Gdx.files.internal("Skins/tracer-ui.json"));
        isNewSelection = true;

        // Setting up texture assets
        Invasion.game.assetManager.load("WorkshopScreen/WorkshopBgImg.png", Texture.class);
        Invasion.game.assetManager.load("WorkshopScreen/WorkshopTable.png", Texture.class);
        Invasion.game.assetManager.load("WorkshopScreen/PlayImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("NumbersTxtSht_96.png", Texture.class);
        Invasion.game.assetManager.finishLoading();

        // Background image
        Image bgImg;
        if (Invasion.game.assetManager.isLoaded("WorkshopScreen/WorkshopBgImg.png")) {
            bgImg = new Image(new Sprite(Invasion.game.assetManager.get("WorkshopScreen/WorkshopBgImg.png", Texture.class)));

            Invasion.game.stage.addActor(bgImg);
        }

        // Workshop Table image
        Image workshopTableImg;
        if (Invasion.game.assetManager.isLoaded("WorkshopScreen/WorkshopTable.png")) {
            workshopTableImg = new Image(new Sprite(Invasion.game.assetManager.get(
                    "WorkshopScreen/WorkshopTable.png", Texture.class)));

            Invasion.game.stage.addActor(workshopTableImg);
        }

        // Structure image button
        TextureRegionDrawable texRegDrawableNorm = new TextureRegionDrawable(
                Structure.getInstance().getTexRegNorm());
        TextureRegionDrawable texRegDrawableSel = new TextureRegionDrawable(
                Structure.getInstance().getTexRegSel());

        ImageButton.ImageButtonStyle btnStyle = new ImageButton.ImageButtonStyle();
        btnStyle.up = texRegDrawableNorm;
        btnStyle.checked = texRegDrawableSel;

        structureBtn = new ImageButton(btnStyle);
        structureBtn.setPosition(283.5f - structureBtn.getWidth() / 2f,
                648.5f - structureBtn.getHeight() / 2f);
        structureBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Invasion.game.playSelectSound();

                structureBtn.setChecked(false);
                Structure.getInstance().removeUpgradePage();

                for (int i = 0; i < turretBtns.length; i++) {
                    turretBtns[i].setChecked(false);
                    Invasion.game.turrets[i].removeUpgradePage();
                }

                for (int i = 0; i < abilityBtns.length; i++) {
                    abilityBtns[i].setChecked(false);
                    Invasion.game.abilities[i].removeUpgradePage();
                }

                structureBtn.setChecked(true);
                isNewSelection = true;
            }
        });
        Invasion.game.stage.addActor(structureBtn);

        structureBtn.setChecked(true);
        Structure.getInstance().addUpgradePage();
        Structure.getInstance().updateUpgradePage();

        // Table of turrets
        Table turretTable = new Table();
        turretTable.defaults().pad(5, 0, 5, 0);
        turretBtns = new ImageButton[Invasion.game.turrets.length];
        for (int i = 0; i < turretBtns.length; i++) {
            turretBtns[i] = createDefBtn(Invasion.game.turrets[i].getTexRegNorm(),
                    Invasion.game.turrets[i].getTexRegSel());
            turretTable.add(turretBtns[i]);
            if (i < turretBtns.length - 1) turretTable.row();
        }
        ScrollPane turretSP = new ScrollPane(turretTable, skin);
        turretSP.setFadeScrollBars(false);
        turretSP.setPosition(9, 9);
        turretSP.setSize(270, 456);
        Invasion.game.stage.addActor(turretSP);

        // Table of abilities
        Table abilityTable = new Table();
        abilityTable.defaults().pad(5, 0, 5, 0);
        abilityBtns = new ImageButton[Invasion.game.abilities.length];
        for (int i = 0; i < abilityBtns.length; i++) {
            abilityBtns[i] = createDefBtn(Invasion.game.abilities[i].getTexRegNorm(),
                    Invasion.game.abilities[i].getTexRegSel());
            abilityTable.add(abilityBtns[i]);
            if (i < Invasion.game.abilities.length - 1) abilityTable.row();
        }
        ScrollPane abilitySP = new ScrollPane(abilityTable, skin);
        abilitySP.setFadeScrollBars(false);
        abilitySP.setPosition(288, 9);
        abilitySP.setSize(270, 456);
        Invasion.game.stage.addActor(abilitySP);

        // Loadout tables
        Table turretLoadoutTable = new Table();
        turretLoadoutTable.defaults().pad(0f, 23f, 0f, 23f);
        turretLoadoutTable.setPosition(567, 9);
        turretLoadoutTable.setSize(270, 672);

        Table abilityLoadoutTable = new Table();
        abilityLoadoutTable.defaults().pad(0f, 23f, 0f, 23f);
        abilityLoadoutTable.setPosition(846, 9);
        abilityLoadoutTable.setSize(270, 672);

        for (int i = 0; i < Invasion.game.turretLoadout.length; i++) {
            final int index = i;
            Image turretLoadoutImg = new Image(new Sprite(Invasion.game.turretLoadout[i].getTexRegNorm()));
            turretLoadoutImg.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    for (int i = 0; i < turretBtns.length; i++) {
                        if (turretBtns[i].isChecked()) {
                            int foundAtIndex = -1;

                            for (int j = 0; j < Invasion.game.turretLoadout.length; j++) {
                                if (Invasion.game.turretLoadout[j] == Invasion.game.turrets[i]) {
                                    foundAtIndex = j;
                                }
                            }

                            if (foundAtIndex < 0) {
                                Invasion.game.turretLoadout[index] = Invasion.game.turrets[i];

                                ((Image)event.getTarget()).setDrawable(new TextureRegionDrawable(
                                        Invasion.game.turrets[i].getTexRegNorm()));

                                loadoutSwapSound.play(0.5f);
                            }
                            else if (foundAtIndex != index) {
                                Turret tempType = Invasion.game.turretLoadout[index];
                                Invasion.game.turretLoadout[index] = Invasion.game.turrets[i];
                                Invasion.game.turretLoadout[foundAtIndex] = tempType;

                                Drawable tempDrawable = ((Image)event.getTarget()).getDrawable();
                                ((Image)event.getTarget()).setDrawable(new TextureRegionDrawable(
                                        Invasion.game.turrets[i].getTexRegNorm()));
                                ((Image)event.getTarget().getParent().getChildren().get(foundAtIndex)).setDrawable(tempDrawable);

                                loadoutSwapSound.play(0.5f);
                            }

                            break;
                        }
                    }
                }
            });

            Image abilityLoadoutImg = new Image(new Sprite(Invasion.game.abilityLoadout[i].getTexRegNorm()));
            abilityLoadoutImg.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    for (int i = 0; i < abilityBtns.length; i++) {
                        if (abilityBtns[i].isChecked()) {
                            int foundAtIndex = -1;

                            for (int j = 0; j < Invasion.game.abilityLoadout.length; j++) {
                                if (Invasion.game.abilityLoadout[j] == Invasion.game.abilities[i]) {
                                    foundAtIndex = j;
                                }
                            }

                            if (foundAtIndex < 0) {
                                Invasion.game.abilityLoadout[index] = Invasion.game.abilities[i];
                                ((Image)event.getTarget()).setDrawable(new TextureRegionDrawable(
                                        Invasion.game.abilities[i].getTexRegNorm()));

                                loadoutSwapSound.play(0.5f);
                            }
                            else if (foundAtIndex != index) {
                                Ability tempType = Invasion.game.abilityLoadout[index];
                                Invasion.game.abilityLoadout[index] = Invasion.game.abilities[i];
                                Invasion.game.abilityLoadout[foundAtIndex] = tempType;

                                Drawable tempDrawable = ((Image)event.getTarget()).getDrawable();
                                ((Image)event.getTarget()).setDrawable(new TextureRegionDrawable(
                                        Invasion.game.abilities[i].getTexRegNorm()));
                                ((Image)event.getTarget().getParent().getChildren().get(foundAtIndex)).setDrawable(tempDrawable);

                                loadoutSwapSound.play(0.5f);
                            }

                            break;
                        }
                    }
                }
            });

            turretLoadoutTable.add(turretLoadoutImg);
            abilityLoadoutTable.add(abilityLoadoutImg);
            if (i < Invasion.game.turretLoadout.length - 1) {
                turretLoadoutTable.row();
                abilityLoadoutTable.row();
            }
        }

        Invasion.game.stage.addActor(turretLoadoutTable);
        Invasion.game.stage.addActor(abilityLoadoutTable);

        // Number Texture Regions
        if (Invasion.game.assetManager.isLoaded("NumbersTxtSht_96.png")) {
            Texture numbersTex = Invasion.game.assetManager.get("NumbersTxtSht_96.png", Texture.class);

            for (int i = 0; i < numberTexRegs.length; i++) {
                numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                        numbersTex.getWidth() / 10, numbersTex.getHeight());
            }
        }

        // Play image button
        // Region 0: up
        // Region 1: down
        if (Invasion.game.assetManager.isLoaded("WorkshopScreen/PlayImgBtnSht.png")) {
            Texture playBtnTexture = Invasion.game.assetManager.get("WorkshopScreen/PlayImgBtnSht.png",
                    Texture.class);

            // Regions
            TextureRegion[] playBtnRegions = new TextureRegion[2];
            playBtnRegions[0] = new TextureRegion(playBtnTexture, 0, 0,
                    playBtnTexture.getWidth(), playBtnTexture.getHeight() / 2);
            playBtnRegions[1] = new TextureRegion(playBtnTexture, 0, playBtnTexture.getHeight() / 2,
                    playBtnTexture.getWidth(), playBtnTexture.getHeight() / 2);

            // Drawables
            TextureRegionDrawable[] playBtnRegionDrawables = new TextureRegionDrawable[2];
            playBtnRegionDrawables[0] = new TextureRegionDrawable(playBtnRegions[0]);
            playBtnRegionDrawables[1] = new TextureRegionDrawable(playBtnRegions[1]);

            // Style
            ImageButton.ImageButtonStyle playBtnStyle = new ImageButton.ImageButtonStyle();
            playBtnStyle.up = playBtnRegionDrawables[0];
            playBtnStyle.down = playBtnRegionDrawables[1];

            // Button
            ImageButton playBtn = new ImageButton(playBtnStyle);
            playBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();

                    if (Invasion.game.getLevelReached() == 1) {
                        Invasion.game.setScreen(Invasion.gameScreen);
                    }
                    else {
                        Invasion.game.setScreen(Invasion.levelSelectScreen);
                    }
                }
            });

            playBtn.setPosition(1759f - playBtn.getWidth() / 2f, 58f - playBtn.getHeight() / 2f);

            Invasion.game.stage.addActor(playBtn);
        }

        coinPIndex = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
        Invasion.game.particles.velocity[coinPIndex].set(0f, 0f);
        Invasion.game.particles.image[coinPIndex].setScale(0.14f, 0.14f);
        Invasion.game.particles.size[coinPIndex].set(
                Invasion.game.particles.image[coinPIndex].getWidth() *
                        Invasion.game.particles.image[coinPIndex].getScaleX(),
                Invasion.game.particles.image[coinPIndex].getHeight() *
                        Invasion.game.particles.image[coinPIndex].getScaleY());
        Invasion.game.particles.position[coinPIndex].set(
                1175f - Invasion.game.particles.size[coinPIndex].x / 2f,
                58.5f - Invasion.game.particles.size[coinPIndex].y / 2f);

        loadoutSwapSound = Gdx.audio.newSound(Gdx.files.internal("Audio/loadoutSwap.wav"));
    }

    public void render (float f) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        Invasion.game.assetManager.update();
        update(deltaTime);
        Invasion.game.particles.update(deltaTime);

        Invasion.game.stage.act();
        Invasion.game.stage.draw();
    }

    @Override
    public void dispose() {
        skin.dispose();
        loadoutSwapSound.dispose();
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

    private void update(float deltaTime) {
        int index = 0;

        // Check if turret is selected
        while (index < turretBtns.length) {
            if (turretBtns[index].isChecked()) {
                break;
            }

            index++;
        }

        // If no turret select, check if ability is selected
        if (index == turretBtns.length) {
            while (index < turretBtns.length + abilityBtns.length) {
                if (abilityBtns[index - turretBtns.length].isChecked()) {
                    break;
                }

                index++;
            }
        }

        switch (index) {
            case 0: // Laser Turret
                if (isNewSelection) {
                    LaserTurret.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[2];
                }

                LaserTurret.getInstance().updateUpgradePage();
                break;
            case 1: // Missile Turret
                if (isNewSelection) {
                    MissileTurret.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[3];
                }

                MissileTurret.getInstance().updateUpgradePage();
                break;
            case 2: // Tesla Tower
                if (isNewSelection) {
                    TeslaTower.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[4];
                }
                TeslaTower.getInstance().updateUpgradePage();
                break;
            case 3: // Beam Turret
                if (isNewSelection) {
                    BeamTurret.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[2];
                }

                BeamTurret.getInstance().updateUpgradePage();
                break;
            case 4: // Mega Bomb
                if (isNewSelection) {
                    MegaBomb.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[2];
                }

                MegaBomb.getInstance().updateUpgradePage();
                break;
            case 5: // Shield Booster
                if (isNewSelection) {
                    ShieldBooster.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[3];
                }

                ShieldBooster.getInstance().updateUpgradePage();
                break;
            case 6: // Freeze Trap
                if (isNewSelection) {
                    Freeze.getInstance().addUpgradePage();
//                    isNewSelection = false;

                    for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                        Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                    }
                    upgradeCoinPIndexArr = new int[3];
                }

                Freeze.getInstance().updateUpgradePage();
                break;
            default:
                if (structureBtn.isChecked()) {
                    if (isNewSelection) {
                        Structure.getInstance().addUpgradePage();
//                        isNewSelection = false;

                        if (upgradeCoinPIndexArr != null) {
                            for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                                Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] = 0;
                            }
                        }
                        upgradeCoinPIndexArr = new int[4];
                    }

                    Structure.getInstance().updateUpgradePage();
                }
                break;
        }

        if (isNewSelection) {
            for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
                upgradeCoinPIndexArr[i] = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
                Invasion.game.particles.velocity[upgradeCoinPIndexArr[i]].set(0f, 0f);
                Invasion.game.particles.image[upgradeCoinPIndexArr[i]].setScale(0.12f, 0.12f);
                Invasion.game.particles.size[upgradeCoinPIndexArr[i]].set(
                        Invasion.game.particles.image[upgradeCoinPIndexArr[i]].getWidth() *
                                Invasion.game.particles.image[upgradeCoinPIndexArr[i]].getScaleX(),
                        Invasion.game.particles.image[upgradeCoinPIndexArr[i]].getHeight() *
                                Invasion.game.particles.image[upgradeCoinPIndexArr[i]].getScaleY());
                Invasion.game.particles.position[upgradeCoinPIndexArr[i]].set(
                        1688f - Invasion.game.particles.size[upgradeCoinPIndexArr[i]].x / 2f,
                        629f - (94f * i) - Invasion.game.particles.size[upgradeCoinPIndexArr[i]].y / 2f);

                isNewSelection = false;
            }
        }

        for (int i = 0; i < upgradeCoinPIndexArr.length; i++) {
            if (Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] - deltaTime <= 0) {
                Invasion.game.particles.lifetime[upgradeCoinPIndexArr[i]] =
                        Invasion.game.particles.LIFETIME;
            }
        }

        // Currency
        if (costDigitImgs != null) {
            for (int i = 0; i < costDigitImgs.length; i++) {
                costDigitImgs[i].remove();
            }
        }

        int currency = Invasion.game.getCurrency();
        if (currency > 99999) currency = 99999;
        costDigitImgs = new Image[("" + currency).length()];
        for (int i = 0; i < costDigitImgs.length; i++) {
            int number = (int)(currency / Math.pow(10, (costDigitImgs.length - i - 1)));
            costDigitImgs[i] = new Image(numberTexRegs[number % 10]);

            if (i == 0) {
                costDigitImgs[i].setPosition(1225, 58 - costDigitImgs[i].getHeight() / 2);
            }
            else {
                costDigitImgs[i].setPosition(
                        costDigitImgs[i - 1].getX() + costDigitImgs[i].getWidth(),
                        costDigitImgs[i - 1].getY());
            }

            Invasion.game.stage.addActor(costDigitImgs[i]);
        }

        if (Invasion.game.particles.lifetime[coinPIndex] - deltaTime <= 0)
            Invasion.game.particles.lifetime[coinPIndex] = Invasion.game.particles.LIFETIME;
    }

    private ImageButton createDefBtn(TextureRegion texRegNorm, TextureRegion texRegSel) {
        TextureRegionDrawable texRegDrawableNorm = new TextureRegionDrawable(texRegNorm);
        TextureRegionDrawable texRegDrawableSel = new TextureRegionDrawable(texRegSel);

        ImageButton.ImageButtonStyle btnStyle = new ImageButton.ImageButtonStyle();
        btnStyle.up = texRegDrawableNorm;
        btnStyle.checked = texRegDrawableSel;

        ImageButton imgBtn = new ImageButton(btnStyle);
        imgBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Invasion.game.playSelectSound();

                structureBtn.setChecked(false);
                Structure.getInstance().removeUpgradePage();

                for (int i = 0; i < turretBtns.length; i++) {
                    turretBtns[i].setChecked(false);
                    Invasion.game.turrets[i].removeUpgradePage();
                }

                for (int i = 0; i < abilityBtns.length; i++) {
                    abilityBtns[i].setChecked(false);
                    Invasion.game.abilities[i].removeUpgradePage();
                }
                ((ImageButton)event.getTarget()).setChecked(true);

                isNewSelection = true;
            }
        });

        return imgBtn;
    }
}
