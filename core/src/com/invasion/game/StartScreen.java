package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class StartScreen implements Screen {
    private int[] eIndex;
    private float enemyTimer;

    private ImageButton startBtn;
    private Image titleImg;

    public StartScreen() {
    }

    public void create() {
        Invasion.game.playMenuMusic();

        enemyTimer = 0f;
        eIndex = new int[Invasion.game.enemies.MAX_ENEMIES];

        // Setting up texture assets
        Invasion.game.assetManager.load("StartScreen/TitleBgImg.png", Texture.class);
        Invasion.game.assetManager.load("StartScreen/TitleTxtImg_200.png", Texture.class);
        Invasion.game.assetManager.load("StartScreen/StartImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.finishLoading();

        // Background image
        Image bgImg;
        if (Invasion.game.assetManager.isLoaded("StartScreen/TitleBgImg.png")) {
            bgImg = new Image(new Sprite(Invasion.game.assetManager.get("StartScreen/TitleBgImg.png", Texture.class)));
            bgImg.setColor(0.75f, 0.75f, 0.75f, 1f);
            Invasion.game.stage.addActor(bgImg);
        }

        // Title image
        if (Invasion.game.assetManager.isLoaded("StartScreen/TitleTxtImg_200.png")) {
            titleImg = new Image(new Sprite(Invasion.game.assetManager.get("StartScreen/TitleTxtImg_200.png",
                    Texture.class)));

            // Position
            titleImg.setPosition(Invasion.game.GW_WIDTH / 2f - titleImg.getWidth() / 2f,
                    4f * Invasion.game.GW_HEIGHT / 5f - titleImg.getHeight() / 2f);

            Invasion.game.stage.addActor(titleImg);
        }

        // Start image button
        // Region 0: up
        // Region 1: down
        if (Invasion.game.assetManager.isLoaded("StartScreen/StartImgBtnSht.png")) {
            Texture StartBtnTexture = Invasion.game.assetManager.get("StartScreen/StartImgBtnSht.png",
                    Texture.class);

            // Regions
            TextureRegion[] startBtnRegions = new TextureRegion[2];
            startBtnRegions[0] = new TextureRegion(StartBtnTexture, 0, 0,
                    StartBtnTexture.getWidth(), StartBtnTexture.getHeight() / 2);
            startBtnRegions[1] = new TextureRegion(StartBtnTexture, 0, StartBtnTexture.getHeight() / 2,
                    StartBtnTexture.getWidth(), StartBtnTexture.getHeight() / 2);

            // Drawables
            TextureRegionDrawable[] startBtnRegionDrawables = new TextureRegionDrawable[2];
            startBtnRegionDrawables[0] = new TextureRegionDrawable(startBtnRegions[0]);
            startBtnRegionDrawables[1] = new TextureRegionDrawable(startBtnRegions[1]);

            // Style
            ImageButton.ImageButtonStyle startBtnStyle = new ImageButton.ImageButtonStyle();
            startBtnStyle.up = startBtnRegionDrawables[0];
            startBtnStyle.down = startBtnRegionDrawables[1];

            // Button
            startBtn = new ImageButton(startBtnStyle);
            startBtn.setPosition(Invasion.game.GW_WIDTH / 2f - startBtn.getWidth() / 2f,
                    startBtn.getHeight());

            // Button events
            startBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();
                    Invasion.game.setScreen(Invasion.workshopScreen);
                }
            });

            Invasion.game.stage.addActor(startBtn);
        }

        for (int i = 0; i < eIndex.length; i++) {
            eIndex[i] = -1;
        }
    }

    public void render (float f) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        Invasion.game.assetManager.update();
        update(deltaTime);

        Invasion.game.stage.act();
        Invasion.game.stage.draw();
    }

    public void update(float deltaTime) {
        enemyTimer += deltaTime;

        // Spawn enemy
        if (enemyTimer >= 2f) {
            for (int i = 0; i < eIndex.length; i++) {
                if (eIndex[i] < 0f) {
                    eIndex[i] = Invasion.game.enemies.spawn(Enemies.Type.RANGED);

                    Invasion.game.enemies.position[eIndex[i]].set(Invasion.game.GW_WIDTH,
                            random(startBtn.getY() + startBtn.getHeight() + 20f,
                                    titleImg.getY() - Invasion.game.enemies.size[eIndex[i]].y - 20f));
                    break;
                }
            }

            enemyTimer = 0f;
        }

        // Move enemies across the screen
        for (int i = 0; i < eIndex.length; i++) {
            if (eIndex[i] < 0f) continue;

            Invasion.game.enemies.position[eIndex[i]].x -=
                    Invasion.game.enemies.speed[eIndex[i]] * deltaTime;
            Invasion.game.enemies.images[eIndex[i]].setPosition(
                    Invasion.game.enemies.position[eIndex[i]].x,
                    Invasion.game.enemies.position[eIndex[i]].y);

            if (Invasion.game.enemies.position[eIndex[i]].x < -Invasion.game.enemies.size[eIndex[i]].x) {
                Invasion.game.enemies.type[eIndex[i]] = Enemies.Type.NONE;
                Invasion.game.enemies.images[eIndex[i]].remove();
                eIndex[i] = -1;
            }
        }
    }

    @Override
    public void dispose() {

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

    public float random(float a, float b) {
        // Random float value between a (inclusive) and b (exclusive)
        return (float)Math.random() * (b - a) + a;
    }
}
