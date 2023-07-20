package com.etheller.warsmash.loader;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.AudioExtension;
import com.etheller.warsmash.viewer5.gl.DynamicShadowExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GameLoader {

	private final WarsmashGdxMultiScreenGame gameInitialized;
	private final Runnable requestStoragePerm;
	private final ANGLEInstancedArrays angleInstancedArrays;

	public GameLoader(WarsmashGdxMultiScreenGame gameInitialized,
			ANGLEInstancedArrays angleInstancedArrays,
			DynamicShadowExtension dynamicShadowExtension,
			AudioExtension audioExtension,
			Runnable requestStoragePerm) {
		this.gameInitialized = gameInitialized;
		this.angleInstancedArrays = angleInstancedArrays;
		this.requestStoragePerm=requestStoragePerm;
		ExtensionLoader.setupExtensions(this.angleInstancedArrays,dynamicShadowExtension,audioExtension);
	}

	Thread thread;

	public void runGameAsync() {
//		AppSettings.EXTERNAL_STORAGE = externalStoragePath;
//		AppSettings.INTERNAL_STORAGE= internalStoragePath;
		Gdx.app.postRunnable(() -> {
//            gameInitialized.setScreen(new GameLoaderScreen());
			System.out.println("WarsmashModEngine");
			System.out.println("WarsmashModEngine");
			System.out.println("https://github.com/Retera/WarsmashModEngine");
			System.out.println();
			if (requestStoragePerm != null) {
				thread = new Thread(requestStoragePerm);
				thread.start();
			}
			else {
				//LinsThread.postThread(this::onStoragePermGranted);
				onStoragePermGranted();
			}
		});
	}

	public void onStoragePermGranted() {
		System.out.println("loading...");
		final DataTable warsmashIni = loadWarsmashIni("warsmash.ini");
		final Element emulatorConstants = warsmashIni.get("Emulator");
		WarsmashConstants.loadConstants(emulatorConstants, warsmashIni);
		Gdx.app.postRunnable(() -> {
			WarsmashGdxMenuScreen menuScreen = new WarsmashGdxMenuScreen(warsmashIni, gameInitialized);
			menuScreen.show();
			gameInitialized.setScreen(menuScreen);
			System.out.println("done.");
		});
	}

	static DataTable loadWarsmashIni(final String iniPath) {
		final DataTable warsmashIni = new DataTable(StringBundle.EMPTY);
		try (InputStream stream = Gdx.files.internal(iniPath).read()) {
			warsmashIni.readTXT(stream, true);
		}
		catch (final FileNotFoundException e) {
			System.out.println("NOTE: if the ini file really exists there, try select gradle runGame rather than gradle run or run directly.it is about the working dir.");
			throw new RuntimeException(e);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return warsmashIni;
	}

	public void onStoragePermRejected() {
		System.out.println("storage permission request rejected.");
		System.err.println("to use this app, storage permissions for warsmash should be permitted.");
		System.out.println("bye.");
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				Gdx.app.exit();
			}
		}, 5);
	}
}
