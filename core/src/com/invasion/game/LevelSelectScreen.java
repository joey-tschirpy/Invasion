package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class LevelSelectScreen implements Screen {
    TextureRegion[] numberTexRegs = new TextureRegion[10];
    final int ROWS = 4;
    final int COLS = 5;
    final float PAD = 50f;
    final int MAX_LEVELS = ROWS * COLS; // max levels per table
    Table[] levelSelectTables;
    int tableSelection;
    ImageButton prevBtn;
    ImageButton nextBtn;
    ImageButton backBtn;

    Image[][] levelDigitImgs;

    public LevelSelectScreen() {
    }

    public void create() {
        Invasion.game.playMenuMusic();

        // Setting up texture assets
        Invasion.game.assetManager.load("LevelSelectScreen/LevelSelectBgImg.png", Texture.class);
        Invasion.game.assetManager.load("LevelSelectScreen/LevelSelectTable.png", Texture.class);
        Invasion.game.assetManager.load("LevelSelectScreen/LevelSelectImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("LevelSelectScreen/PrevNextImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("LevelSelectScreen/BackImgBtnSht.png", Texture.class);
        Invasion.game.assetManager.load("NumbersTxtSht_72.png", Texture.class);
        Invasion.game.assetManager.finishLoading();

        // Background image
        Image bgImg;
        if (Invasion.game.assetManager.isLoaded("LevelSelectScreen/LevelSelectBgImg.png")) {
            bgImg = new Image(new Sprite(Invasion.game.assetManager.get("LevelSelectScreen/LevelSelectBgImg.png", Texture.class)));

            Invasion.game.stage.addActor(bgImg);
        }

        // Level Select Table image
        Image levelSelectTableImg;
        if (Invasion.game.assetManager.isLoaded("LevelSelectScreen/LevelSelectTable.png")) {
            levelSelectTableImg = new Image(new Sprite(Invasion.game.assetManager.get(
                    "LevelSelectScreen/LevelSelectTable.png", Texture.class)));

            Invasion.game.stage.addActor(levelSelectTableImg);
        }

        // Back button
        if (Invasion.game.assetManager.isLoaded("LevelSelectScreen/BackImgBtnSht.png")) {
            Texture backBtnTexture = Invasion.game.assetManager.get("LevelSelectScreen/BackImgBtnSht.png",
                    Texture.class);

            // Regions
            TextureRegion[] backBtnRegions = new TextureRegion[2];
            backBtnRegions[0] = new TextureRegion(backBtnTexture, 0, 0,
                    backBtnTexture.getWidth(), backBtnTexture.getHeight() / 2);
            backBtnRegions[1] = new TextureRegion(backBtnTexture, 0, backBtnTexture.getHeight() / 2,
                    backBtnTexture.getWidth(), backBtnTexture.getHeight() / 2);

            // Drawables
            TextureRegionDrawable[] backBtnRegionDrawables = new TextureRegionDrawable[2];
            backBtnRegionDrawables[0] = new TextureRegionDrawable(backBtnRegions[0]);
            backBtnRegionDrawables[1] = new TextureRegionDrawable(backBtnRegions[1]);

            // Style
            ImageButton.ImageButtonStyle backBtnStyle = new ImageButton.ImageButtonStyle();
            backBtnStyle.up = backBtnRegionDrawables[0];
            backBtnStyle.down = backBtnRegionDrawables[1];

            // Button
            backBtn = new ImageButton(backBtnStyle);
            backBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();
                    Invasion.game.setScreen(Invasion.workshopScreen);
                }
            });

            backBtn.setPosition(Invasion.game.GW_WIDTH / 2f - backBtn.getWidth() / 2f,
                    backBtn.getHeight() / 2f);

            Invasion.game.stage.addActor(backBtn);
        }

        // Number Texture Regions
        if (Invasion.game.assetManager.isLoaded("NumbersTxtSht_72.png")) {
            Texture numbersTex = Invasion.game.assetManager.get("NumbersTxtSht_72.png", Texture.class);

            for (int i = 0; i < numberTexRegs.length; i++) {
                numberTexRegs[i] = new TextureRegion(numbersTex, i * numbersTex.getWidth() / 10, 0,
                        numbersTex.getWidth() / 10, numbersTex.getHeight());
            }
        }

        if (Invasion.game.assetManager.isLoaded("LevelSelectScreen/LevelSelectImgBtnSht.png")) {
            Texture levelBtnTexture = Invasion.game.assetManager.get(
                    "LevelSelectScreen/LevelSelectImgBtnSht.png", Texture.class);

            // Regions
            TextureRegion[] levelBtnRegions = new TextureRegion[2];
            levelBtnRegions[0] = new TextureRegion(levelBtnTexture, 0, 0,
                    levelBtnTexture.getWidth(), levelBtnTexture.getHeight() / 2);
            levelBtnRegions[1] = new TextureRegion(levelBtnTexture, 0, levelBtnTexture.getHeight() / 2,
                    levelBtnTexture.getWidth(), levelBtnTexture.getHeight() / 2);

            // Drawables
            TextureRegionDrawable[] levelBtnRegionDrawables = new TextureRegionDrawable[2];
            levelBtnRegionDrawables[0] = new TextureRegionDrawable(levelBtnRegions[0]);
            levelBtnRegionDrawables[1] = new TextureRegionDrawable(levelBtnRegions[1]);

            // Style
            ImageButton.ImageButtonStyle levelBtnStyle = new ImageButton.ImageButtonStyle();
            levelBtnStyle.up = levelBtnRegionDrawables[0];
            levelBtnStyle.down = levelBtnRegionDrawables[1];

            // Tables for level select buttons
            int levels = Invasion.game.getLevelReached();
            levelSelectTables = new Table[(levels - 1) / MAX_LEVELS + 1];
            for (int i = 0; i < levelSelectTables.length; i++) {
                levelSelectTables[i] = new Table();
                levelSelectTables[i].setPosition(9f, 1.5f * backBtn.getHeight());
                levelSelectTables[i].setSize(1902f, 860f - 1.5f * backBtn.getHeight());
                levelSelectTables[i].defaults().align(Align.center).pad(PAD / 2);
            }
            tableSelection = levelSelectTables.length - 1;

            for (int i = 1; i <= levels; i++) {
                final int LEVEL = i;

                // Button
                ImageButton levelBtn = new ImageButton(levelBtnStyle);
                levelBtn.addListener(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        Invasion.game.playHitBtnsound();
                        Invasion.game.setCurrentLevel(LEVEL);
                        Invasion.game.setScreen(Invasion.gameScreen);
                    }
                });

                levelSelectTables[(i - 1) / MAX_LEVELS].add(levelBtn);
                if (i % COLS == 0) levelSelectTables[(i - 1) / MAX_LEVELS].row();
            }

            Invasion.game.stage.addActor(levelSelectTables[tableSelection]);
        }

        if (Invasion.game.assetManager.isLoaded("LevelSelectScreen/PrevNextImgBtnSht.png") &&
                levelSelectTables.length > 1) {
            Texture tex = Invasion.game.assetManager.get(
                    "LevelSelectScreen/PrevNextImgBtnSht.png", Texture.class);

            // Regions
            TextureRegion[] prevBtnRegions = new TextureRegion[2];
            prevBtnRegions[0] = new TextureRegion(tex, 0, 0,
                    tex.getWidth() / 2, tex.getHeight() / 2);
            prevBtnRegions[1] = new TextureRegion(tex, 0, tex.getHeight() / 2,
                    tex.getWidth() / 2, tex.getHeight() / 2);

            TextureRegion[] nextBtnRegions = new TextureRegion[2];
            nextBtnRegions[0] = new TextureRegion(tex, tex.getWidth() / 2, 0,
                    tex.getWidth() / 2, tex.getHeight() / 2);
            nextBtnRegions[1] = new TextureRegion(tex, tex.getWidth() / 2, tex.getHeight() / 2,
                    tex.getWidth() / 2, tex.getHeight() / 2);

            // Drawables
            TextureRegionDrawable[] prevBtnRegionDrawables = new TextureRegionDrawable[2];
            prevBtnRegionDrawables[0] = new TextureRegionDrawable(prevBtnRegions[0]);
            prevBtnRegionDrawables[1] = new TextureRegionDrawable(prevBtnRegions[1]);

            TextureRegionDrawable[] nextBtnRegionDrawables = new TextureRegionDrawable[2];
            nextBtnRegionDrawables[0] = new TextureRegionDrawable(nextBtnRegions[0]);
            nextBtnRegionDrawables[1] = new TextureRegionDrawable(nextBtnRegions[1]);

            // Style
            ImageButton.ImageButtonStyle prevBtnStyle = new ImageButton.ImageButtonStyle();
            prevBtnStyle.up = prevBtnRegionDrawables[0];
            prevBtnStyle.down = prevBtnRegionDrawables[1];

            ImageButton.ImageButtonStyle nextBtnStyle = new ImageButton.ImageButtonStyle();
            nextBtnStyle.up = nextBtnRegionDrawables[0];
            nextBtnStyle.down = nextBtnRegionDrawables[1];

            // Button
            prevBtn = new ImageButton(prevBtnStyle);
            prevBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();

                    levelSelectTables[tableSelection].remove();
                    Invasion.game.stage.addActor(levelSelectTables[--tableSelection]);
                    if (tableSelection == 0) prevBtn.remove();

                    updateLevelImages();

                    nextBtn.remove();
                    Invasion.game.stage.addActor(nextBtn);
                }
            });
            prevBtn.setPosition(59.5f - prevBtn.getWidth() / 2f, 434.5f - prevBtn.getHeight() / 2f);

            nextBtn = new ImageButton(nextBtnStyle);
            nextBtn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Invasion.game.playHitBtnsound();

                    levelSelectTables[tableSelection].remove();
                    Invasion.game.stage.addActor(levelSelectTables[++tableSelection]);
                    if (tableSelection == levelSelectTables.length - 1) nextBtn.remove();

                    updateLevelImages();

                    prevBtn.remove();
                    Invasion.game.stage.addActor(prevBtn);
                }
            });
            nextBtn.setPosition(1860.5f - nextBtn.getWidth() / 2f, 434.5f - nextBtn.getHeight() / 2f);

            Invasion.game.stage.addActor(prevBtn);
        }

        updateLevelImages();
    }

    public void render (float f) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Invasion.game.assetManager.update();

        Invasion.game.stage.act();
        Invasion.game.stage.draw();
    }

    public void updateLevelImages() {
        Vector2 center = new Vector2(960f, 434.5f);
        int btnAmount = Invasion.game.getLevelReached() - tableSelection * MAX_LEVELS;
        if (btnAmount > MAX_LEVELS) btnAmount = MAX_LEVELS;

        float btnWidth = levelSelectTables[0].getChildren().toArray()[0].getWidth();
        float btnHeight = levelSelectTables[0].getChildren().toArray()[0].getHeight();

        // first  position
        float x = btnAmount >= COLS ?
                center.x - (0.5f * COLS * btnWidth) - (0.5f * (COLS - 1) * PAD) :
                center.x - (btnAmount * 0.5f * btnWidth) - ((btnAmount - 1) * 0.5f * PAD);
        float y = center.y +
                (((btnAmount - 1) / COLS - 1f) * 0.5f * (btnHeight)) +
                ((btnAmount - 1) / COLS) * 0.5f * PAD +
                1.5f * backBtn.getHeight() / 2f;

        if (levelDigitImgs != null) {
            for (int i = 0; i < levelDigitImgs.length; i++) {
                for (int j = 0; j < levelDigitImgs[i].length; j++) {
                    levelDigitImgs[i][j].remove();
                }
            }
        }

        levelDigitImgs = new Image[btnAmount][];
        for (int i = 0; i < levelDigitImgs.length; i++) {
            int level = i + 1 + tableSelection * MAX_LEVELS;
            levelDigitImgs[i] = new Image[Integer.toString(level).length()];

            for (int j = 0; j < levelDigitImgs[i].length; j++) {
                int number = (int)(level / Math.pow(10, (levelDigitImgs[i].length - j - 1)));
                levelDigitImgs[i][j] = new Image(numberTexRegs[number % 10]);

                if (j == 0) {
                    levelDigitImgs[i][j].setPosition(
                            x + (i % COLS) * (btnWidth + PAD) + btnWidth / 2f -
                                    levelDigitImgs[i].length * levelDigitImgs[i][j].getWidth() / 2f,
                            y - (i / COLS) * (btnHeight + PAD) + btnHeight / 2f -
                                    levelDigitImgs[i][j].getHeight() / 2f);
                }
                else {
                    levelDigitImgs[i][j].setPosition(
                            levelDigitImgs[i][j - 1].getX() + levelDigitImgs[i][j - 1].getWidth(),
                            levelDigitImgs[i][j - 1].getY());
                }

                levelDigitImgs[i][j].setTouchable(Touchable.disabled);
                Invasion.game.stage.addActor(levelDigitImgs[i][j]);
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
}
