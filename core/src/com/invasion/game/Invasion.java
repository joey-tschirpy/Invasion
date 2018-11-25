package com.invasion.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.StringWriter;

public class Invasion extends Game implements ApplicationListener {
	public static Invasion game;

	public static StartScreen startScreen;
	public static WorkshopScreen workshopScreen;
	public static LevelSelectScreen levelSelectScreen;
	public static GameScreen gameScreen;

	public OrthographicCamera camera;
	public Viewport viewport;

	public final float GW_WIDTH = 1920f;
	public final float GW_HEIGHT = 1080f;

	public AssetManager assetManager;
	public Stage stage;

	public enum DefTurretEnum {
		LASER, MISSILE, TESLA, BEAM
	}

	public enum DefAbilityEnum {
		MEGA_BOMB, SHIELD_BOOSTER, FREEZE
	}

	public final int MAX_TURRET_LOADOUT = 3;
	public final int MAX_ABILITY_LOADOUT = 3;

	public Turret[] turrets = new Turret[DefTurretEnum.values().length];
	public Ability[] abilities = new Ability[DefAbilityEnum.values().length];

	public Turret[] turretLoadout = new Turret[MAX_TURRET_LOADOUT];
	public Ability[] abilityLoadout = new Ability[MAX_ABILITY_LOADOUT];

	public Structure base;

	private int currency = 99999999;
	private int levelReached = 100;
	private int currentLevel = 100;

	public Projectiles projectiles = new Projectiles();
	public ParticleSystem particles = new ParticleSystem();
	public Enemies enemies = new Enemies();

	private Sound hitBtnSound;
	private Sound upgradeSound;
	private Sound selectSound;

	private Music menuMusic;
	private Music gameMusic;
	private Music gameOverMusic;

	public FileHandle file;

	public Invasion() {
		game = this;
	}

	@Override
	public void create () {
		// Create each screen to navigate
		startScreen = new StartScreen();
		workshopScreen = new WorkshopScreen();
		levelSelectScreen = new LevelSelectScreen();
		gameScreen = new GameScreen();

		// Sets up the camera and viewport to the aspect ratio.
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport = new FitViewport(GW_WIDTH, GW_HEIGHT, camera);
		viewport.apply();

		// Setting stage for display and processing input
		stage = new Stage();
		stage.setViewport(viewport);
		Gdx.input.setInputProcessor(stage);

		// For managing textures
		assetManager = new AssetManager();

		// Adding all turrets/abilities
		turrets[0] = LaserTurret.getInstance();
		turrets[1] = MissileTurret.getInstance();
		turrets[2] = TeslaTower.getInstance();
		turrets[3] = BeamTurret.getInstance();

		abilities[0] = MegaBomb.getInstance();
		abilities[1] = ShieldBooster.getInstance();
		abilities[2] = Freeze.getInstance();

		for (int i = 0; i < Invasion.game.turretLoadout.length; i++) {
			// Turret and ability loadout defaults to first 3
			turretLoadout[i] = turrets[i];
			abilityLoadout[i] = abilities[i];
		}

		projectiles.init();
		particles.init();
		enemies.init();

		hitBtnSound = Gdx.audio.newSound(Gdx.files.internal("Audio/hitBtnSound.mp3"));
		upgradeSound = Gdx.audio.newSound(Gdx.files.internal("Audio/upgrade.wav"));
		selectSound = Gdx.audio.newSound(Gdx.files.internal("Audio/selectSound.wav"));

		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/menuMusic.wav"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/inGameMusic.wav"));
		gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/gameOver.wav"));

		load();

		// Set starting screen as the shell screen
		setScreen(startScreen);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		for (int i = 0; i < turrets.length; i++) {
			turrets[i].dispose();
		}
		for (int i = 0; i < abilities.length; i++) {
			abilities[i].dispose();
		}

		assetManager.dispose();
		stage.dispose();

		enemies.dispose();
		particles.dispose();
		projectiles.dispose();

		hitBtnSound.dispose();
		upgradeSound.dispose();
		selectSound.dispose();

		menuMusic.dispose();
		gameMusic.dispose();
		gameOverMusic.dispose();

		save();

		super.dispose();
		System.exit(0);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	public int getCurrency() {
		return currency;
	}

	public int getLevelReached() {
		return levelReached;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void addCurrency(int currency) {
		this.currency += currency;
	}

	public boolean useCurrency(int currency) {
		if (this.currency < currency) return false;

		this.currency -= currency;
		return true;
	}

	public void setLevelReached(int level) {
		if (levelReached < level) levelReached = level;
	}

	public void setCurrentLevel(int level) {
		currentLevel = level;
	}

	public void playHitBtnsound() {
		hitBtnSound.play();
	}

	public void playUpgradeSound() {
		upgradeSound.play(0.3f);
	}

	public void playSelectSound() {
		selectSound.play(0.3f);
	}

	public void playMenuMusic() {
		if (!menuMusic.isPlaying()) {
			menuMusic.play();
			menuMusic.setVolume(0.2f);
			menuMusic.setLooping(true);
		}
	}

	public void playGameMusic() {
		if (!gameMusic.isPlaying()) {
			gameMusic.play();
			gameMusic.setVolume(0.1f);
			gameMusic.setLooping(true);
		}
	}

	public void playGameOverMusic() {
		if (!gameOverMusic.isPlaying()) {
			gameOverMusic.play();
		}
	}

	public void scaleGameMusicVolume(float scale) {
		gameMusic.setVolume(gameMusic.getVolume() * scale);
	}

	public void stopMenuMusic() {
		if (menuMusic.isPlaying()) menuMusic.stop();
	}

	public void stopGameMusic() {
		if (gameMusic.isPlaying()) gameMusic.stop();
	}

	public void stopGameOverMusic() {
		if (gameOverMusic.isPlaying()) gameOverMusic.stop();
	}

	public void stopMusic() {
		stopMenuMusic();
		stopGameMusic();
		stopGameOverMusic();
	}

	public void save() {
		int[][] data = new int[2 + turrets.length + abilities.length][];

		// Invasion data
		int[] InvasionData = new int[2];
		InvasionData[0] = currency;
		InvasionData[1] = levelReached;
		data[0] = InvasionData;

		// Structure data
		data[1] = Structure.getInstance().getData();

		// Turret data
		for (int i = 0; i < turrets.length; i++) {
			data[i + 2] = turrets[i].getData();
		}

		// Ability data
		for (int i = 0; i < abilities.length; i++) {
			data[i + 2 + turrets.length] = abilities[i].getData();
		}

		// Write data to file
		Json json = new Json();
		file.writeString(Base64Coder.encodeString(json.toJson(data)), false);
	}

	public void load() {
		file = Gdx.files.local("data.json");

		if (file.exists()) {
			// Get data from file
			Json json = new Json();
			int[][] data = json.fromJson(int[][].class,
					Base64Coder.decodeString(Invasion.game.file.readString()));

			// Load Invasion data
			currency = data[0][0];
			levelReached = data[0][1];

			// Load Structure data
			Structure.getInstance().setData(data[1]);

			// Load Turret data
			for (int i = 0; i < turrets.length; i++) {
				turrets[i].setData(data[i + 2]);
			}

			// Load Ability data
			for (int i = 0; i < abilities.length; i++) {
				abilities[i].setData(data[i + 2 + turrets.length]);
			}
		}
	}
}