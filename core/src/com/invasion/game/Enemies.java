package com.invasion.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Enemies {
    public enum Type {
        NONE, MELEE, RANGED, ARTILLERY, BOSS_TANK, BOSS_SHIP
    }

    public static final int MAX_ENEMIES = 128;
    private static final float MELEE_ATTACK_FREQ = 0.1f;
    private static final float RANGED_ATTACK_FREQ = 2f;
    private static final float ARTILLERY_ATTACK_FREQ = 5f;

    private float levelMultiplier = 1f;

    public Type[] type;
    public Vector2[] position;
    public float[] speed;
    private float[] accel;
    public Vector2[] size;
    public Image[] images;
    public float[] health;
    private float[] stopPoint;
    private float[] lifeTimer;
    private float[] attackTimer;
    private Image[] freezeImg;

    private boolean[] knockback;

    private Color hitColor = new Color(0.2f, 0.2f, 0.2f, 1f);

    private Texture tankMeleeTex;
    private Texture tankRangedTex;
    private Texture tankArtilleryTex;
    private Texture shipTex;

    private Texture freezeTex;

    private TextureRegion[] tankMeleeTexReg;
    private TextureRegion[] shipTexReg;

    private Sound coinSound;
    private Sound drillSound;
    private Sound cannonSound;
    private Sound deathSound;

    public Enemies() {}

    public void init() {
        type = new Type[MAX_ENEMIES];
        position = new Vector2[MAX_ENEMIES];
        speed = new float[MAX_ENEMIES];
        accel = new float[MAX_ENEMIES];
        size = new Vector2[MAX_ENEMIES];
        images = new Image[MAX_ENEMIES];
        stopPoint = new float[MAX_ENEMIES];
        lifeTimer = new float[MAX_ENEMIES];
        attackTimer = new float[MAX_ENEMIES];
        knockback = new boolean[MAX_ENEMIES];
        health = new float[MAX_ENEMIES];
        freezeImg = new Image[MAX_ENEMIES];

        tankMeleeTex = new Texture(Gdx.files.internal("GameScreen/tankMelee.png"));
        tankRangedTex = new Texture(Gdx.files.internal("GameScreen/tankRanged.png"));
        tankArtilleryTex = new Texture(Gdx.files.internal("GameScreen/tankArtillery.png"));
        shipTex = new Texture(Gdx.files.internal("GameScreen/ship.png"));

        freezeTex = new Texture(Gdx.files.internal("GameScreen/freezeImg.png"));


        int rows = 3;
        int cols = 1;
        tankMeleeTexReg = new TextureRegion[rows * cols];
        for (int i = 0; i < tankMeleeTexReg.length; i++) {
            tankMeleeTexReg[i] = new TextureRegion(tankMeleeTex, (i % cols) * tankMeleeTex.getWidth() / cols,
                    (i / cols) * tankMeleeTex.getHeight() / rows,
                    tankMeleeTex.getWidth() / cols, tankMeleeTex.getHeight() / rows);
        }

        rows = 5;
        cols = 1;
        shipTexReg = new TextureRegion[rows * cols];
        for (int i = 0; i < shipTexReg.length; i++) {
            shipTexReg[i] = new TextureRegion(shipTex, (i % cols) * shipTex.getWidth() / cols,
                    (i / cols) * shipTex.getHeight() / rows,
                    shipTex.getWidth() / cols, shipTex.getHeight() / rows);
        }

        for (int i = 0; i < MAX_ENEMIES; i++)
        {
            type[i] = Type.NONE;
        }

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Audio/coins.wav"));
        drillSound = Gdx.audio.newSound(Gdx.files.internal("Audio/drill.wav"));
        cannonSound = Gdx.audio.newSound(Gdx.files.internal("Audio/cannon_fire.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("Audio/enemyDeath.wav"));
    }

    public int spawn(Type t)
    {
        if (t == Type.NONE) return -1;

        int freeSlot = -1;
        for (int i = 0; i < MAX_ENEMIES; i++)
        {
            if(this.type[i] == Type.NONE)
            {
                freeSlot = i;
                break;
            }
        }

        if(freeSlot < 0)
        {
            return -1;
        }

        type[freeSlot] = t;

        switch(t)
        {
            case MELEE:
            {
                speed[freeSlot] = 200f;
                images[freeSlot] = new Image(tankMeleeTexReg[0]);
                images[freeSlot].setScale(2f, 2f);
                size[freeSlot] = new Vector2(
                        images[freeSlot].getWidth() * images[freeSlot].getScaleX(),
                        images[freeSlot].getHeight() * images[freeSlot].getScaleY());
                health[freeSlot] = 2f * levelMultiplier;
                stopPoint[freeSlot] = Invasion.game.gameScreen.getWallEdge();

                position[freeSlot] = new Vector2(Invasion.game.GW_WIDTH,
                        random(50f, Invasion.game.GW_HEIGHT - size[freeSlot].y - 88f));

                Invasion.game.stage.addActor(images[freeSlot]);
                break;
            }
            case RANGED:
            {
                speed[freeSlot] = 100f;
                images[freeSlot] = new Image(tankRangedTex);
                images[freeSlot].setScale(2.5f, 2.5f);
                size[freeSlot] = new Vector2(images[freeSlot].getWidth() * images[freeSlot].getScaleX(),
                        images[freeSlot].getHeight() * images[freeSlot].getScaleY());
                health[freeSlot] = 4f * levelMultiplier;
                stopPoint[freeSlot] = (Invasion.game.GW_WIDTH / 2f) * random(0.8f, 1.2f);

                position[freeSlot] = new Vector2(Invasion.game.GW_WIDTH,
                        random(50f, Invasion.game.GW_HEIGHT - size[freeSlot].y - 88f));

                Invasion.game.stage.addActor(images[freeSlot]);
                break;
            }
            case ARTILLERY:
            {
                speed[freeSlot] = 50f;

                images[freeSlot] = new Image(tankArtilleryTex);
                images[freeSlot].setScale(4f, 4f);
                size[freeSlot] = new Vector2(images[freeSlot].getWidth() * images[freeSlot].getScaleX(),
                        images[freeSlot].getHeight() * images[freeSlot].getScaleY());
                health[freeSlot] = 20f * levelMultiplier;
                stopPoint[freeSlot] = Invasion.game.GW_WIDTH - (Invasion.game.GW_WIDTH / 5f);

                position[freeSlot] = new Vector2(Invasion.game.GW_WIDTH,
                        random(88f, Invasion.game.GW_HEIGHT - size[freeSlot].y - 88f));

                Invasion.game.stage.addActor(images[freeSlot]);

                break;
            }
            case BOSS_TANK:
            {
                speed[freeSlot] = 50f;
                images[freeSlot] = new Image(tankArtilleryTex);
                images[freeSlot].setScale(5f, 5f);
                stopPoint[freeSlot] = Invasion.game.GW_WIDTH - (Invasion.game.GW_WIDTH / 3f);

                health[freeSlot] = 100f * levelMultiplier;
                size[freeSlot] = new Vector2(images[freeSlot].getWidth() * images[freeSlot].getScaleX(),
                        images[freeSlot].getHeight() * images[freeSlot].getScaleY());
                position[freeSlot] = new Vector2(Invasion.game.GW_WIDTH,
                        Invasion.game.GW_HEIGHT / 2f - size[freeSlot].y / 2f);
                Invasion.game.stage.addActor(images[freeSlot]);

                break;
            }
            case BOSS_SHIP:
            {
                speed[freeSlot] = 60f;
                images[freeSlot] = new Image(shipTexReg[1]);
                images[freeSlot].setScale(1.5f, 1.5f);
                stopPoint[freeSlot] = Invasion.game.GW_WIDTH - (Invasion.game.GW_WIDTH / 3f);

                health[freeSlot] = 100f * levelMultiplier;
                size[freeSlot] = new Vector2(images[freeSlot].getWidth() * images[freeSlot].getScaleX(),
                        images[freeSlot].getHeight() * images[freeSlot].getScaleY());
                position[freeSlot] = new Vector2(Invasion.game.GW_WIDTH,
                        Invasion.game.GW_HEIGHT / 2f - size[freeSlot].y / 2f);
                Invasion.game.stage.addActor(images[freeSlot]);
            }
        }

        images[freeSlot].setPosition(position[freeSlot].x, position[freeSlot].y);
        lifeTimer[freeSlot] = 0f;

        return freeSlot;
    }

    public void update(float deltaTime, boolean frozen)
    {
        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            if(type[i] == Type.NONE)
            {
                continue;
            }

            removeHitColor(i);

            if (frozen) continue;

            lifeTimer[i] += deltaTime;
            switch(type[i])
            {
                case MELEE:
                {
                    if(images[i].getX() > stopPoint[i]) {
                        position[i].x -= speed[i] * deltaTime;
                    }
                    else if (images[i].getX() <= stopPoint[i] && images[i].getX() > 0)
                    {
                        images[i].setDrawable(new TextureRegionDrawable(
                                tankMeleeTexReg[(int)(lifeTimer[i] * 10) % tankMeleeTexReg.length]));

                        attackTimer[i] += deltaTime;
                        if(attackTimer[i] >= MELEE_ATTACK_FREQ)
                        {
                            drillSound.play(0.05f);

                            Structure.getInstance().takeDamage(0.1f * levelMultiplier);
                            attackTimer[i] = 0f;
                        }
                    }
                    break;
                }
                case RANGED:
                {
                    if(images[i].getX() > stopPoint[i] ) {
                        position[i].x -= speed[i] * deltaTime;
                    }
                    else
                    {
                       attackTimer[i] += deltaTime;
                        if(attackTimer[i] >= RANGED_ATTACK_FREQ)
                        {
                            // ranged projectile
                            int index = Invasion.game.projectiles.spawn(type[i]);
                            Invasion.game.projectiles.position[index].set(
                                    position[i].x,
                                    position[i].y + size[i].y / 2f);

                            attackTimer[i] = 0;
                        }
                    }
                    break;
                }
                case ARTILLERY:
                {
                    if(knockback[i])
                    {
                        if(position[i].x > stopPoint[i] + 50f)
                        {
                            knockback[i] = false;
                        }
                        position[i].x += 500f * deltaTime;
                    }
                    else if(images[i].getX() > stopPoint[i]) {
                        position[i].x -= speed[i] * deltaTime;
                    }
                    else
                    {
                        attackTimer[i] += deltaTime;
                        if(attackTimer[i] >= ARTILLERY_ATTACK_FREQ)
                        {
                            // muzzle flash particle effect
                            int index = Invasion.game.particles.spawn(ParticleSystem.Type.MUZZLE);
                            Invasion.game.particles.position[index].set(
                                    position[i].x + size[i].x / 2f - 50f,
                                    position[i].y + size[i].y / 2f +
                                            Invasion.game.particles.size[index].y / 2f);

                            // cannon projectile
                            index = Invasion.game.projectiles.spawn(type[i]);
                            Invasion.game.projectiles.position[index].set(
                                    position[i].x,
                                    position[i].y + size[i].y / 2f);

                            cannonSound.play();

                            knockback[i] = true;
                            attackTimer[i] = 0f;
                        }
                    }

                    break;
                }
                case BOSS_TANK:
                {
                    if (health[i] <= 0) return;

                    // Repair
                    health[i] += 1f * deltaTime * levelMultiplier;

                    // Tank boss updates
                    if(knockback[i])
                    {
                        if(position[i].x > stopPoint[i] + 50f)
                        {
                            knockback[i] = false;
                        }
                        position[i].x += 500f * deltaTime;
                    }
                    else if(images[i].getX() > stopPoint[i]) {
                        position[i].x -= speed[i] * deltaTime;
                    }
                    else
                    {
                        attackTimer[i] += deltaTime;
                        if(attackTimer[i] >= ARTILLERY_ATTACK_FREQ)
                        {
                            // muzzle flash particle effect
                            int index = Invasion.game.particles.spawn(ParticleSystem.Type.MUZZLE);
                            Invasion.game.particles.position[index].set(
                                    position[i].x + size[i].x / 2f - 60f,
                                    position[i].y + size[i].y / 2f +
                                            Invasion.game.particles.size[index].y / 2f);

                            // cannon projectile
                            index = Invasion.game.projectiles.spawn(Type.ARTILLERY);
                            Invasion.game.projectiles.position[index].set(
                                    position[i].x,
                                    position[i].y + size[i].y / 2f);

                            cannonSound.play();

                            knockback[i] = true;
                            attackTimer[i] = 0f;
                        }
                    }

                    break;
                }
                case BOSS_SHIP:
                {
                    if (health[i] <= 0) return;

                    if(images[i].getX() > stopPoint[i]) {
                        images[i].setDrawable(new TextureRegionDrawable(
                                shipTexReg[(int)(lifeTimer[i] * 20) % (shipTexReg.length - 3)]));

                        position[i].x -= speed[i] * deltaTime;

                        if (position[i].x <= stopPoint[i]) {
                            images[i].setDrawable(new TextureRegionDrawable(shipTexReg[shipTexReg.length - 1]));
                            speed[i] = 0f;
                            accel[i] = 40f;
                        }
                    }
                    else
                    {
                        // Max speed 80
                        if (speed[i] >= 80f) {
                            accel[i] = 0f;
                        }

                        // Sideways movement
                        if (position[i].y > Invasion.game.GW_HEIGHT - size[i].y - 300f) {
                            accel[i] = -40f;
                        }
                        else if (position[i].y < 300f) {
                            accel[i] = 40f;
                        }
                        speed[i] += accel[i] * deltaTime;
                        position[i].y += speed[i] * deltaTime;

                        // Attacks
                        int prev = (int)(attackTimer[i] * 5); // 5 attacks per sec
                        attackTimer[i] += deltaTime;
                        int next = (int)(attackTimer[i] * 5);

                        // Side cannons
                        if (prev != next) {
                            int index = Invasion.game.projectiles.spawn(type[i]);

                            if (next % 2 == 0) {
                                // left side cannon
                                Invasion.game.projectiles.position[index].set(
                                        position[i].x + 30f,
                                        position[i].y + 50f);
                            }
                            else {
                                // right side cannon
                                Invasion.game.projectiles.position[index].set(
                                        position[i].x + 30f,
                                        position[i].y + size[i].y - 50f);
                            }
                        }

                        // Front cannon
                        else if(attackTimer[i] >= RANGED_ATTACK_FREQ)
                        {
                            // cannon projectile
                            int index = Invasion.game.projectiles.spawn(Type.RANGED);
                            Invasion.game.projectiles.position[index].set(
                                    position[i].x,
                                    position[i].y + size[i].y / 2f);

                            attackTimer[i] = 0f;
                        }
                    }

                    break;
                }
            }

            images[i].setPosition(position[i].x, position[i].y);
        }
    }

    public boolean damage(Vector2 pos, Projectiles.Type t)
    {
        boolean hit = false;
        for (int i = 0; i < MAX_ENEMIES; i++)
        {
            if(type[i] == Type.NONE)
            {
                continue;
            }

            Vector2 center = new Vector2(position[i].x + size[i].x / 2f, position[i].y + size[i].y / 2f);
            switch(t)
            {
                case LASER:
                {
                    if(pos.dst(center) <= size[i].x / 2f + 50f)
                    {
                        LaserTurret lt = LaserTurret.getInstance();
                        hit = true;
                        if (type[i] != Type.BOSS_TANK && type[i] != Type.BOSS_SHIP)
                            position[i].x = position[i].x + lt.getKnockback();
                        takeDamage(i, lt.getDamage());
                    }
                    break;
                }
                case MISSILE:
                {
                    if(pos.dst(center) <= size[i].x / 2f + 120f)
                    {
                        MissileTurret mt = MissileTurret.getInstance();

                        hit = true;
                        takeDamage(i, mt.getDamage());

                    }
                    break;
                }
            }

            if (t == Projectiles.Type.LASER && hit) {
                break;
            }
        }
        return hit;
    }

    public void takeDamage(int index, float damage) {
        health[index] -= damage;

        if (health[index] <= 0f) {
            switch(type[index]) {
                case MELEE: {
                    int coins = (int)(1 + levelMultiplier * 0.5f);
                    if (coins > 15) coins = 15;
                    for (int i = 0; i < coins; i++) {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
                        Invasion.game.particles.position[pIndex].set(
                                position[index].x + size[index].x / 2f -
                                        Invasion.game.particles.size[pIndex].x / 2f,
                                position[index].y + size[index].y / 2f -
                                        Invasion.game.particles.size[pIndex].y / 2f);

                        Invasion.game.particles.velocity[pIndex].set(
                                random(-250f, 250f), random(-250f, 250f));

                        Invasion.game.particles.image[pIndex].setPosition(
                                Invasion.game.particles.position[pIndex].x,
                                Invasion.game.particles.position[pIndex].y);
                    }

                    type[index] = Type.NONE;
                    images[index].remove();

                    Invasion.game.addCurrency((int)(1 + levelMultiplier * 0.5f));
                    deathSound.play(0.15f);
                    coinSound.play(0.15f);
                    break;
                }
                case RANGED: {
                    int coins = (int) (2 + levelMultiplier);
                    if (coins > 15) coins = 15;
                    for (int i = 0; i < coins; i++) {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
                        Invasion.game.particles.position[pIndex].set(
                                position[index].x + size[index].x / 2f -
                                        Invasion.game.particles.size[pIndex].x / 2f,
                                position[index].y + size[index].y / 2f -
                                        Invasion.game.particles.size[pIndex].y / 2f);

                        Invasion.game.particles.velocity[pIndex].set(
                                random(-350f, 350f), random(-350f, 350f));

                        Invasion.game.particles.image[pIndex].setPosition(
                                Invasion.game.particles.position[pIndex].x,
                                Invasion.game.particles.position[pIndex].y);
                    }

                    type[index] = Type.NONE;
                    images[index].remove();

                    Invasion.game.addCurrency((int) (2 + levelMultiplier));
                    deathSound.play(0.15f);
                    coinSound.play(0.15f);
                    break;
                }
                case ARTILLERY: {
                    int coins = (int) (5 + levelMultiplier * 2.5f);
                    if (coins > 15) coins = 15;
                    for (int i = 0; i < coins; i++) {
                        int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
                        Invasion.game.particles.position[pIndex].set(
                                position[index].x + size[index].x / 2f -
                                        Invasion.game.particles.size[pIndex].x / 2f,
                                position[index].y + size[index].y / 2f -
                                        Invasion.game.particles.size[pIndex].y / 2f);

                        Invasion.game.particles.velocity[pIndex].set(
                                random(-500f, 500f), random(-500f, 500f));

                        Invasion.game.particles.image[pIndex].setPosition(
                                Invasion.game.particles.position[pIndex].x,
                                Invasion.game.particles.position[pIndex].y);
                    }

                    type[index] = Type.NONE;
                    images[index].remove();

                    Invasion.game.addCurrency((int) (5 + levelMultiplier * 2.5f));
                    deathSound.play(0.15f);
                    coinSound.play(0.15f);
                    break;
                }
            }

            if (freezeImg[index] != null) {
                freezeImg[index].remove();
            }
        }
        else {
            images[index].addAction(Actions.color(hitColor, 0.05f));
        }
    }

    public void megaBomb(float wave)
    {
        for(int i = 0;i < MAX_ENEMIES;i++)
        {
            if(type[i] == Type.NONE)
        {
            continue;
        }
            if(wave >= position[i].x)
            {
                takeDamage(i, 3f * levelMultiplier);
            }
        }
    }

    public int getClosestEnemy(Vector2 coords, float maxDistance) {
        int[] indexArr = {-1};
        return getClosestEnemy(coords, indexArr, maxDistance);
    }

    public int getClosestEnemy(Vector2 coords, int[] prevEnemyIndices, float maxDistance) {
        Vector2 midPointCurrent = new Vector2(0f, 0f);
        Vector2 midPointMin = new Vector2(-Integer.MAX_VALUE, -Integer.MAX_VALUE);
        int index = -1;

        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] == Type.NONE) continue;

            boolean skip = false;
            for(int j : prevEnemyIndices) {
                if (j == i) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;

            midPointCurrent.set(position[i].x + size[i].x / 2f, position[i].y + size[i].y / 2f);

            if (midPointCurrent.dst(coords) > maxDistance) continue;

            if (index < 0 || midPointCurrent.dst(coords) < midPointMin.dst(coords)) {
                midPointMin.set(midPointCurrent);
                index = i;
            }
        }

        return index;
    }

    public void reset() {
        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] == Type.NONE) continue;

            type[i] = Type.NONE;
            images[i].remove();
        }

        levelMultiplier = 1f;
    }

    public void beamHit(float degAngle, Vector2 botBeamVec, Vector2 topBeamVec, float deltaTime) {
        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] == Type.NONE) continue;

            // Top of enemy
            Vector2 enemyTLPos = new Vector2(position[i].x, position[i].y + size[i].y);
            enemyTLPos.sub(botBeamVec);
            enemyTLPos.rotate(90);

            Vector2 enemyTRPos = new Vector2(position[i].x + size[i].x, position[i].y + size[i].y);
            enemyTRPos.sub(botBeamVec);
            enemyTRPos.rotate(90);

            // Bottom of enemy
            Vector2 enemyBLPos = new Vector2(position[i].x, position[i].y);
            enemyBLPos.sub(topBeamVec);
            enemyBLPos.rotate(90);

            Vector2 enemyBRPos = new Vector2(position[i].x + size[i].x, position[i].y);
            enemyBRPos.sub(topBeamVec);
            enemyBRPos.rotate(90);

            if ((degAngle > 180 - enemyTLPos.angle() || degAngle > 180 - enemyTRPos.angle()) &&
                    (degAngle < 180 - enemyBLPos.angle() || degAngle < 180 - enemyBRPos.angle())) {
                takeDamage(i, BeamTurret.getInstance().getDamage() * deltaTime);
            }
        }
    }

    public float getDifficultyMultiplier() {
        return levelMultiplier;
    }

    public void setDifficultyMultiplier(float dm) {
        levelMultiplier = dm;
    }

    public float random(float a, float b) {
        // Random float value between a (inclusive) and b (exclusive)
        return (float)Math.random() * (b - a) + a;
    }

    public void removeHitColor(int i) {
        if (images[i].getColor().r <= hitColor.r) {
            images[i].addAction(Actions.color(Color.WHITE, 0.05f));
        }
    }

    public void bossDeath(int index) {
        int coins = (int) (10 * levelMultiplier);
        if (coins > 50) coins = 50;
        for (int i = 0; i < coins; i++) {
            int pIndex = Invasion.game.particles.spawn(ParticleSystem.Type.COIN);
            Invasion.game.particles.position[pIndex].set(
                    position[index].x + size[index].x / 2f -
                            Invasion.game.particles.size[pIndex].x / 2f,
                    position[index].y + size[index].y / 2f -
                            Invasion.game.particles.size[pIndex].y / 2f);

            Invasion.game.particles.velocity[pIndex].set(
                    random(-1000f, 1000f), random(-1000f, 1000f));

            Invasion.game.particles.image[pIndex].setPosition(
                    Invasion.game.particles.position[pIndex].x,
                    Invasion.game.particles.position[pIndex].y);
        }

        type[index] = Type.NONE;
        images[index].remove();

        Invasion.game.addCurrency((int) (20 + levelMultiplier * 10));
        deathSound.play(0.2f);
        coinSound.play(0.25f);
    }

    public void freeze() {
        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (type[i] == Type.NONE) continue;

            freezeImg[i] = new Image(freezeTex);

            if (type[i] == Type.MELEE) {
                freezeImg[i].setWidth(size[i].x / 2f);
                freezeImg[i].setHeight(size[i].y);
                freezeImg[i].setPosition(position[i].x + size[i].x / 2f, position[i].y);
            }
            else {
                freezeImg[i].setWidth(size[i].x);
                freezeImg[i].setHeight(size[i].y);
                freezeImg[i].setPosition(position[i].x, position[i].y);
            }

            Invasion.game.stage.addActor(freezeImg[i]);
        }
    }

    public void unFreeze() {
        for (int i = 0; i < MAX_ENEMIES; i++) {
            if (freezeImg[i] != null) {
                freezeImg[i].remove();
            }
        }
    }

    public void dispose()
    {
        tankMeleeTex.dispose();
        tankRangedTex.dispose();
        tankArtilleryTex.dispose();
        shipTex.dispose();

        freezeTex.dispose();

        coinSound.dispose();
        drillSound.dispose();
        cannonSound.dispose();
        deathSound.dispose();
    }
}
