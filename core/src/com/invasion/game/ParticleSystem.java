package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ParticleSystem {
    public static final int MAX_PARTICLES = 512;
    public static final float LIFETIME = 0.5f;

    public enum Type{
        NONE, LASER_EXPLOSION, MISSILE_EXPLOSION, RANGED_EXPLOSION, MUZZLE, COIN
    }

    public Type[] type;
    public Vector2[] position;
    public Vector2[] velocity;
    public Image[] image;
    public Vector2[] size;
    public float[] lifetime;

    private Texture laserExplosionTexSht;
    private TextureRegion[] laserExplosionFrames;

    private Texture missileExplosionTexSht;
    private TextureRegion[] missileExplosionFrames;

    private Texture muzzleTexSht;
    private TextureRegion[] muzzleFrames;

    private Texture rangedExplosionTexSht;
    private TextureRegion[] rangedExplosionFrames;

    private Texture coinTexSht;
    private TextureRegion[] coinFrames;

    public void init()
    {
        type = new Type[MAX_PARTICLES];
        position = new Vector2[MAX_PARTICLES];
        velocity = new Vector2[MAX_PARTICLES];
        image = new Image[MAX_PARTICLES];
        size = new Vector2[MAX_PARTICLES];
        lifetime = new float[MAX_PARTICLES];

        for (int i = 0;i < MAX_PARTICLES;i++)
        {
            type[i] = Type.NONE;
            position[i] = new Vector2();
            velocity[i] = new Vector2();
            size[i] = new Vector2();
        }

        int rows = 8;
        int cols = 8;
        laserExplosionTexSht = new Texture(Gdx.files.internal("Particles/LaserExplosion.png"));
        laserExplosionFrames = new TextureRegion[rows * cols];
        for (int i = 0;i < laserExplosionFrames.length; i++)
        {
            laserExplosionFrames[i] = new TextureRegion(laserExplosionTexSht,
                    (i % cols) * laserExplosionTexSht.getWidth() / cols,
                    i / rows * laserExplosionTexSht.getHeight() / rows,
                    laserExplosionTexSht.getWidth() / cols,
                    laserExplosionTexSht.getHeight() / rows);
        }

//        rows = 8;
//        cols = 8;
        missileExplosionTexSht = new Texture(Gdx.files.internal("Particles/MissileExplosion.png"));
        missileExplosionFrames = new TextureRegion[rows * cols];
        for (int i = 0;i < missileExplosionFrames.length; i++)
        {
            missileExplosionFrames[i] = new TextureRegion(missileExplosionTexSht,
                    (i % cols) * missileExplosionTexSht.getWidth() / cols,
                    i / rows * missileExplosionTexSht.getHeight() / rows,
                    missileExplosionTexSht.getWidth() / cols,
                    missileExplosionTexSht.getHeight() / rows);
        }

//        rows = 8;
//        cols = 8;
        muzzleTexSht = new Texture(Gdx.files.internal("Particles/artillery_muzzle.png"));
        muzzleFrames = new TextureRegion[rows * cols];
        for (int i = 0;i < muzzleFrames.length; i++)
        {
            muzzleFrames[i] = new TextureRegion(muzzleTexSht,
                    (i % cols) * muzzleTexSht.getWidth() / cols,
                    i / rows * muzzleTexSht.getHeight() / rows,
                    muzzleTexSht.getWidth() / cols,
                    muzzleTexSht.getHeight() / rows);
        }

        rows = 4;
        cols = 4;
        rangedExplosionTexSht = new Texture(Gdx.files.internal("Particles/RangedExplosion.png"));
        rangedExplosionFrames = new TextureRegion[rows * cols];
        for (int i = 0;i < rangedExplosionFrames.length; i++)
        {
            rangedExplosionFrames[i] = new TextureRegion(rangedExplosionTexSht,
                    (i % cols) * rangedExplosionTexSht.getWidth() / cols,
                    i / rows * rangedExplosionTexSht.getHeight() / rows,
                    rangedExplosionTexSht.getWidth() / cols,
                    rangedExplosionTexSht.getHeight() / rows);
        }

        rows = 1;
        cols = 10;
        coinTexSht = new Texture(Gdx.files.internal("Particles/GoldStarCoinImgSht.png"));
        coinFrames = new TextureRegion[rows * cols];
        for (int i = 0;i < coinFrames.length; i++)
        {
            coinFrames[i] = new TextureRegion(coinTexSht,
                    (i % cols) * coinTexSht.getWidth() / cols, 0,
                    coinTexSht.getWidth() / cols, coinTexSht.getHeight());
        }
    }

    public int spawn(Type t)
    {
        if(t == null) return -1;

        int freeSlot = -1;
        for(int i = 0;i < MAX_PARTICLES;i++)
        {
            if(type[i] == Type.NONE)
            {
                freeSlot = i;
                break;
            }
        }

        switch(t)
        {
            case LASER_EXPLOSION:
            {
                type[freeSlot] = Type.LASER_EXPLOSION;
                image[freeSlot] = new Image(laserExplosionFrames[0]);
                lifetime[freeSlot] = LIFETIME;
                break;
            }
            case MISSILE_EXPLOSION:
            {
                type[freeSlot] = Type.MISSILE_EXPLOSION;
                image[freeSlot] = new Image(missileExplosionFrames[0]);
                image[freeSlot].setScale(2f, 2f);
                lifetime[freeSlot] = LIFETIME;
                break;
            }
            case MUZZLE:
            {
                type[freeSlot] = Type.MUZZLE;
                image[freeSlot] = new Image(muzzleFrames[0]);
                image[freeSlot].setRotation(180f);
                lifetime[freeSlot] = LIFETIME;

                break;
            }
            case RANGED_EXPLOSION:
            {
                type[freeSlot] = Type.RANGED_EXPLOSION;
                image[freeSlot] = new Image(rangedExplosionFrames[0]);
                image[freeSlot].setScale(2f, 2f);

                lifetime[freeSlot] = LIFETIME / 2;
                break;
            }
            case COIN:
            {
                type[freeSlot] = Type.COIN;
                velocity[freeSlot] = new Vector2(0, 200f);
                image[freeSlot] = new Image(coinFrames[0]);
                image[freeSlot].setScale(0.1f, 0.1f);

                lifetime[freeSlot] = LIFETIME;
                break;
            }
        }

        size[freeSlot].set(image[freeSlot].getWidth() * image[freeSlot].getScaleX(),
                image[freeSlot].getHeight() * image[freeSlot].getScaleY());

        Invasion.game.stage.addActor(image[freeSlot]);
        return freeSlot;
    }

    public void update(float deltaTime)
    {
        for(int i = 0;i < MAX_PARTICLES; i++)
        {
            if(type[i] == Type.NONE)
            {
                continue;
            }
            if(lifetime[i] <= 0)
            {
                type[i] = Type.NONE;
                image[i].remove();
                continue;
            }

            lifetime[i] -= deltaTime;
            image[i].setPosition(position[i].x, position[i].y);

            switch(type[i]) {
                case LASER_EXPLOSION: {
                    int frame = (int) ((LIFETIME - lifetime[i]) /
                            LIFETIME * laserExplosionFrames.length);
                    if (frame >= laserExplosionFrames.length)
                        frame = laserExplosionFrames.length - 1;
                    image[i].setDrawable(new TextureRegionDrawable(laserExplosionFrames[frame]));
                    break;
                }
                case MISSILE_EXPLOSION: {
                    int frame = (int) ((LIFETIME - lifetime[i]) /
                            LIFETIME * missileExplosionFrames.length);
                    if (frame >= missileExplosionFrames.length)
                        frame = missileExplosionFrames.length - 1;
                    image[i].setDrawable(new TextureRegionDrawable(missileExplosionFrames[frame]));
                    break;
                }
                case MUZZLE: {
                    int frame = (int) ((LIFETIME - lifetime[i]) /
                            LIFETIME * muzzleFrames.length);
                    if (frame >= muzzleFrames.length) frame = muzzleFrames.length - 1;
                    image[i].setDrawable(new TextureRegionDrawable(muzzleFrames[frame]));
                    break;
                }
                case RANGED_EXPLOSION: {
                    int frame = (int) ((LIFETIME / 2 - lifetime[i]) /
                            (LIFETIME / 2) * rangedExplosionFrames.length);
                    if (frame >= rangedExplosionFrames.length)
                        frame = rangedExplosionFrames.length - 1;
                    image[i].setDrawable(new TextureRegionDrawable(rangedExplosionFrames[frame]));
                    break;
                }
                case COIN: {
                    int frame = (int) ((LIFETIME - lifetime[i]) / LIFETIME *
                            coinFrames.length);
                    if (frame >= coinFrames.length) frame = coinFrames.length - 1;
                    image[i].setDrawable(new TextureRegionDrawable(coinFrames[frame]));

                    position[i].mulAdd(velocity[i], deltaTime);
                    velocity[i].scl(1 - deltaTime * 2);
                    break;
                }
            }
        }
    }

    public void bringCoinToTop() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (type[i] == Type.NONE) continue;

            switch(type[i]) {
                case COIN:
                    image[i].setZIndex(Integer.MAX_VALUE);
                    break;
            }
        }
    }

    public void reset() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (type[i] == Type.NONE) continue;

            type[i] = Type.NONE;
            image[i].remove();
        }
    }

    public void dispose() {
        laserExplosionTexSht.dispose();
        missileExplosionTexSht.dispose();
        muzzleTexSht.dispose();
        rangedExplosionTexSht.dispose();
        coinTexSht.dispose();
    }
}