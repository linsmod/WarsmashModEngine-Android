package com.etheller.warsmash.util;

public class WarsmashConstants {
	public static final int MAX_PLAYERS = 16;
	/*
	 * With version, we use 0 for RoC, 1 for TFT emulation, and probably 2+ or
	 * whatever for custom mods and other stuff
	 */
	public static int GAME_VERSION = 1;
	public static final int REPLACEABLE_TEXTURE_LIMIT = 64;
	public static final float SIMULATION_STEP_TIME = 1 / 20f;
	public static final int PORT_NUMBER = 6115;
	public static final float BUILDING_CONSTRUCT_START_LIFE = 0.1f;
	public static final int BUILD_QUEUE_SIZE = 7;
	// It looks like in Patch 1.22, "Particle" in video settings will change this
	// factor:
	// Low - unknown ?
	// Medium - 1.0f
	// High - 1.5f
	public static final float MODEL_DETAIL_PARTICLE_FACTOR = 1.5f;
	public static final float MODEL_DETAIL_PARTICLE_FACTOR_INVERSE = 1f / MODEL_DETAIL_PARTICLE_FACTOR;

	// I know this default string is from somewhere, maybe a language file? Didn't
	// find it yet so I used this
	public static final String DEFAULT_STRING = "Default string";
}