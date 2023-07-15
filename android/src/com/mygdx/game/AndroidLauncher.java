package com.mygdx.game;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.etheller.warsmash.GdxEnv;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.loader.GameLoader;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xShaders;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.TerrainShaders;
import com.mygdx.game.audio.AndroidAudioExtension;
import com.mygdx.game.audio.AndroidOpenALAudio;

public class AndroidLauncher extends AndroidApplication {
	private GameLoader gameLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WarsmashGdxMultiScreenGame game = new WarsmashGdxMultiScreenGame();
		GdxEnv.EXTERNAL_STORAGE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL30 = true;
		initialize(game, config);

		Gdx.app.postRunnable(() -> {
			// test
			try {
				WebGL webGL = new WebGL(Gdx.gl30);
				webGL.createShaderProgram(TerrainShaders.Terrain.codes());
				webGL.createShaderProgram(TerrainShaders.Cliffs.codes());
				webGL.createShaderProgram(TerrainShaders.Water.codes());
				webGL.createShaderProgram(W3xShaders.UberSplat.codes());
			}
			catch (Exception ex) {
				ex.printStackTrace();
				Gdx.app.exit();
			}
		});


//		WarCraft3.RunGame(game);
		gameLoader = new GameLoader(game,
				new ANGLEInstancedArraysGLES30(),
				new DynamicShadowExtensionGLES30(),
				new AndroidAudioExtension(),
				this::requestPermissionsAsync
		);
		gameLoader.runGameAsync();

//        Gdx.app.postRunnable(() -> {
//            game.setScreen(new GameLoaderScreen());
//            System.out.println("WarsmashModEngine");
//            System.out.println("https://github.com/Retera/WarsmashModEngine");
//            System.out.println();
//
//
//            this.dsd = AppSettings.initialize();
//            new Thread(() -> {
//                Looper.prepare();
//                permGrantLooper = Looper.myLooper();
//                requestPermissions();
//                Looper.loop();
//            }).start();
//        });
	}

	void requestPermissionsAsync() {
		Looper.prepare();
		requestPermissions();
		Looper.loop();
	}

	void requestPermissions() {
		Preferences p = this.getPreferences("app");
		boolean b = p.getBoolean("permRequested");
		if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			System.out.println("storage permission already granted.");
			gameLoader.onStoragePermGranted();
//            new Thread(() -> {
//                try {
//                    System.out.print("parsing mpq archive...");
//                    com.etheller.warsmash.datasources.DataSource ds = dsd.createDataSource();
//                    ds.load();
//                    AppSettings.LoadedDs = ds;
//                    System.out.println("done.");
//                    System.out.println("loading...");
//
//                    ExtensionLoader.loadExtensions(new ANGLEInstancedArraysGLES30());
//                    WarsmashGdxMenuScreen menuScreen = new WarsmashGdxMenuScreen(AppSettings.LoadedDt, this.game);
//                    menuScreen.show();
//                    System.out.println("done.");
//                    this.postRunnable(() -> {
//                        game.setScreen(menuScreen);
//                        AppSettings.glEnabled = true;
//                    });
//                } catch (Throwable ex) {
//                    ex.printStackTrace();
//                }
////                WarCraft3.RunGame(game);
//            }).start();
		}
		else if (!b) {
			this.onCreateDialog(1).show();
		}
	}

	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		AndroidOpenALAudio audio = new AndroidOpenALAudio();
		if (audio.noDevice)
			return super.createAudio(context, config);
		return audio;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        Preferences p = this.getPreferences("app");
//        p.putBoolean("permRequested", true);
		for (int i = 0; i < permissions.length; i++) {
			if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					System.out.println("storage permission granted.");
					gameLoader.onStoragePermRejected();
				}
				else {
//                    System.err.println("storage permission request rejected.");
//                    System.out.println("to use this app, storage permission in phone settings for this app should be permitted.");
//                    System.out.println("bye.");
//                    Timer.schedule(new Timer.Task() {
//                        @Override
//                        public void run() {
//                            AndroidLauncher.this.exit();
//                        }
//                    }, 10);

					gameLoader.onStoragePermRejected();
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        permGrantLooper.quit();
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = new AlertDialog.Builder(getContext())
									 .setTitle("Warsmash")
									 .setMessage("You will permit this app to load/extract resources from MPQ archive in EXTERNAL_STORAGE "
														 + ". mpq files must be copy/put there to continue. theme should be found under Warcraft III install dir on your pc. "
									 )
									 .setPositiveButton("确定", new DialogInterface.OnClickListener() {
										 @Override
										 public void onClick(DialogInterface dialog, int which) {
											 AndroidLauncher.this.requestPermissions(new String[]{
													 Manifest.permission.READ_EXTERNAL_STORAGE,
													 Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2
											 );
										 }
									 }).create();
		return dialog;
	}
}
