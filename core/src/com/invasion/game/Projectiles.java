package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Projectiles {
    public static final int MAX_PROJECTILES = 128;

    public enum Type { NONE, LASER, MISSILE, BEAM, ENEMY_RANGED, ENEMY_CANNON, ENEMY_LASER}

    public Rectangle collider = new Rectangle();

    private Texture texLaser;
    private Texture texMissile;
    private Texture texBeam;
    private Texture texRanged;
    private Texture texCannon;
    private TextureRegion texRegLaser;
    private TextureRegion[] texRegRanged;
    private TextureRegion[] texRegMissile;
    private TextureRegion texRegCannon;

    public Type[] type = new Type[MAX_PROJECTILES];
    public Vector2[] position = new Vector2[MAX_PROJECTILES];
    public Vector2[] velocity = new Vector2[MAX_PROJECTILES];
    public float[] lifetime = new float[MAX_PROJECTILES];
    public Image[] images = new Image[MAX_PROJECTILES];
    public Vector2[] size = new Vector2[MAX_PROJECTILES];

    private Sound explosionSound;
    private Sound missileSound;
    private Sound rangedAttackSound;
    private Sound rangedImpactSound;
    private Sound laserAttackSound;
    private Sound laserImpactSound;

    public void init() {
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            type[i] = Type.NONE;
        }

        texLaser = new Texture(Gdx.files.internal("Projectiles/M484BulletCollection1.png"));
        texMissile = new Texture(Gdx.files.internal("Projectiles/missile.png"));
        texBeam = new Texture(Gdx.files.internal("Projectiles/M484BulletCollection1.png"));
        texCannon = new Texture(Gdx.files.internal("Particles/MissileExplosion.png"));
        texRanged = new Texture(Gdx.files.internal("Particles/RangedProjectile.png"));

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("Audio/explosion_sound.wav"));
        missileSound = Gdx.audio.newSound(Gdx.files.internal("Audio/missile_whoosh.wav"));
        rangedAttackSound = Gdx.audio.newSound(Gdx.files.internal("Audio/ranged_attack.wav"));
        rangedImpactSound = Gdx.audio.newSound(Gdx.files.internal("Audio/ranged_impact.wav"));
        laserAttackSound = Gdx.audio.newSound(Gdx.files.internal("Audio/laser_attack.mp3"));
        laserImpactSound = Gdx.audio.newSound(Gdx.files.internal("Audio/laser_impact.wav"));

        texRegLaser = new TextureRegion(texLaser, 376, 316, 39, 11);
        texRegCannon = new TextureRegion(texCannon, 2254, 206, 100, 100);
        texRegCannon.flip(true, false);

        int cols = 1;
        int rows = 4;
        texRegMissile = new TextureRegion[cols * rows];
        for (int i = 0; i < texRegMissile.length; i++) {
            texRegMissile[i] = new TextureRegion(texMissile, (i % cols) * texMissile.getWidth() / cols,
                    (i / cols) * texMissile.getHeight() / rows,
                    texMissile.getWidth() / cols, texMissile.getHeight() / rows);
        }

        cols = 4;
        rows = 4;
        texRegRanged = new TextureRegion[cols * rows];
        for (int i = 0; i < texRegRanged.length; i++) {
            texRegRanged[i] = new TextureRegion(texRanged, (i % cols) * texRanged.getWidth() / cols,
                    (i / cols) * texRanged.getHeight() / rows,
                    texRanged.getWidth() / cols, texRanged.getHeight() / rows);
        }
    }

    public int spawn(Turret turret, double degAngle, Vector2 touchPos) {
        int free = -1;
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (type[i] == Type.NONE) {
                free = i;
                break;
            }
        }

        if (free < 0) return -1;

        double radAngle = Math.toRadians(degAngle);
        Vector2 dirUnitVec = new Vector2((float)Math.cos(radAngle), (float)Math.sin(radAngle));

        position[free] = new Vector2(turret.getCenter());

        switch (turret.getType()) {
            case LASER: {
                type[free] = Type.LASER;

                images[free] = new Image(new TextureRegionDrawable(texRegLaser));
                images[free].scaleBy(2f, 2f);

                lifetime[free] = 3f;

                laserAttackSound.play(0.75f);

                break;
            }
            case MISSILE: {
                type[free] = Type.MISSILE;

                images[free] = new Image(new TextureRegionDrawable(texRegMissile[0]));

                float dist = turret.getCenter().dst(touchPos);
                lifetime[free] = dist / turret.getBulletSpeed();

                missileSound.play(0.5f);
                break;
            }
            default:
                return -1;
        }

        velocity[free] = new Vector2(dirUnitVec);
        velocity[free].scl(turret.getBulletSpeed());

        images[free].setPosition(position[free].x - images[free].getWidth() / 2f,
                position[free].y - images[free].getHeight() / 2f);
        images[free].setOrigin(images[free].getWidth() / 2f, images[free].getHeight() / 2f);
        images[free].setRotation((float)degAngle);
        images[free].setZIndex(0);

        size[free] = new Vector2(images[free].getWidth() * images[free].getScaleX(),
                images[free].getHeight() * images[free].getScaleY());
        collider.setCenter(position[free]);
        collider.setSize(size[free].x, size[free].y);

        return free;
    }

    public int spawn(Enemies.Type t) {
        int free = -1;
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (type[i] == Type.NONE) {
                free = i;
                break;
            }
        }

        if (free < 0) return -1;

        position[free] = new Vector2(0f, 0f);

        switch(t) {
            case RANGED:
            {
                rangedAttackSound.play(0.25f);

                type[free] = Type.ENEMY_RANGED;
                lifetime[free] = 2f;
                images[free] = new Image(new TextureRegionDrawable(texRegRanged[0]));

                velocity[free] = new Vector2(-1500f, 0f);
                break;
            }
            case ARTILLERY:
            {
                type[free] = Type.ENEMY_CANNON;
                collider.setSize(texRegCannon.getRegionWidth(), texRegCannon.getRegionHeight());
                lifetime[free] = 3f;
                images[free] = new Image(new TextureRegionDrawable(texRegCannon));

                velocity[free] = new Vector2(-1000f, 0f);

                collider.setCenter(position[free]);
                break;
            }
            case BOSS_SHIP:
            {
                type[free] = Type.ENEMY_LASER;
                collider.setSize(texRegLaser.getRegionWidth(), texRegLaser.getRegionHeight());
                lifetime[free] = 3f;
                images[free] = new Image(new TextureRegionDrawable(texRegLaser));
                images[free].setScale(2f, 2f);

                velocity[free] = new Vector2(-2000f, 0f);

                collider.setCenter(position[free]);

                laserAttackSound.play(0.5f);
                break;
            }
        }

        size[free] = new Vector2(images[free].getWidth() + images[free].getScaleX(),
                images[free].getHeight() + images[free].getScaleY());

        Invasion.game.stage.addActor(images[free]);

        return free;
    }

    public void update(float deltaTime) {
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (type[i] == Type.NONE) continue;

            switch (type[i]) {
                case LASER:
                {
                    if (Invasion.game.enemies.damage(position[i], type[i]))
                    {
                        lifetime[i] = 0;
                        laserImpactSound.play(0.15f);
                    }
                    break;
                }
                case MISSILE:
                {
                    int frame = (int)(lifetime[i] * 10) % texRegMissile.length;
                    images[i].setDrawable(new TextureRegionDrawable(texRegMissile[frame]));
                    break;
                }
                case ENEMY_RANGED:
                {
                    int frame = (int)(lifetime[i] * 10) % texRegRanged.length;
                    images[i].setDrawable(new TextureRegionDrawable(texRegRanged[frame]));
                }
                case ENEMY_CANNON:
                {
                    if (position[i].x < Invasion.game.gameScreen.getWallEdge()) {
                        lifetime[i] = 0;
                    }
                    break;
                }
                case ENEMY_LASER:
                {
                    if (position[i].x < Invasion.game.gameScreen.getWallEdge()) {
                        lifetime[i] = 0;
                    }
                    break;
                }
            }

            if (lifetime[i] <= 0f) {
                switch (type[i]) {
                    case LASER: {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.LASER_EXPLOSION);
                        Invasion.game.particles.position[pIndex].set(position[i].x,
                                position[i].y + images[i].getOriginY() -
                                        Invasion.game.particles.size[pIndex].y / 2f);
                        Invasion.game.particles.image[pIndex].setOrigin(images[i].getOriginX(),
                                Invasion.game.particles.image[pIndex].getHeight() / 2f);
                        Invasion.game.particles.image[pIndex].setRotation(images[i].getRotation());

                        break;
                    }
                    case MISSILE: {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.MISSILE_EXPLOSION);
                        Invasion.game.particles.position[pIndex].set(
                                position[i].x - Invasion.game.particles.size[pIndex].x / 2f,
                                position[i].y - Invasion.game.particles.size[pIndex].y / 2f);

                        explosionSound.play();
                        Invasion.game.enemies.damage(position[i], type[i]);
                        break;
                    }
                    case ENEMY_RANGED: {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.RANGED_EXPLOSION);
                        Invasion.game.particles.position[pIndex].set(
                                position[i].x - Invasion.game.particles.size[pIndex].x / 2f,
                                position[i].y - Invasion.game.particles.size[pIndex].y / 2f);

                        Structure.getInstance().takeDamage(
                                10f * Invasion.game.enemies.getDifficultyMultiplier());

                        rangedImpactSound.play(0.25f);
                        break;
                    }
                    case ENEMY_CANNON: {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.MISSILE_EXPLOSION);
                        Invasion.game.particles.position[pIndex].set(
                                position[i].x + size[i].x / 2f -
                                        Invasion.game.particles.size[pIndex].x / 2f,
                                position[i].y - Invasion.game.particles.size[pIndex].y / 2f);

                        Structure.getInstance().takeDamage(
                                20f * Invasion.game.enemies.getDifficultyMultiplier());

                        explosionSound.play();
                        break;
                    }
                    case ENEMY_LASER: {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.LASER_EXPLOSION);

                        Invasion.game.particles.position[pIndex].set(
                                position[i].x + images[i].getOriginX() - Invasion.game.particles.size[pIndex].x / 2f,
                                position[i].y + images[i].getOriginY() - Invasion.game.particles.size[pIndex].y / 2f);
                        Invasion.game.particles.image[pIndex].setOrigin(
                                Invasion.game.particles.image[pIndex].getWidth() / 2f,
                                Invasion.game.particles.image[pIndex].getHeight() / 2f);
                        Invasion.game.particles.image[pIndex].setRotation(180f);

                        Structure.getInstance().takeDamage(
                                1f * Invasion.game.enemies.getDifficultyMultiplier());

                        laserImpactSound.play(0.15f);

                        break;
                    }
                }

                type[i] = Type.NONE;
                images[i].remove();

                continue;
            }

            switch (type[i]) {
                default:
                    lifetime[i] -= deltaTime;
                    position[i].mulAdd(velocity[i], deltaTime);
                    images[i].setPosition(position[i].x - images[i].getWidth() / 2f,
                            position[i].y - images[i].getHeight() / 2f);
            }
        }
    }

    public void bringToTop() {
        // Display projectiles on top
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (type[i] == Type.NONE) continue;

            images[i].setZIndex(Integer.MAX_VALUE);
        }
    }

    public void reset() {
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (type[i] == Type.NONE) continue;

            type[i] = Type.NONE;
            images[i].remove();
        }
    }

    public void dispose() {
        texLaser.dispose();
        texMissile.dispose();
        texBeam.dispose();
        texCannon.dispose();
        texRanged.dispose();

        explosionSound.dispose();
        missileSound.dispose();
        rangedAttackSound.dispose();
        rangedImpactSound.dispose();
        laserAttackSound.dispose();
        laserImpactSound.dispose();
    }
}
